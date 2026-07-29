package bs7projekt.src.utility;

import bs7projekt.src.dtos.CustomerRevenueDto;
import bs7projekt.src.dtos.DataContextDto;
import bs7projekt.src.dtos.OrderAnalysisDto;
import bs7projekt.src.models.Customer;
import bs7projekt.src.models.Order;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.*;

public class Tui {

    /**
     * Renders the main text-based menu, lets the user pick an option by number,
     * and executes the corresponding action. Invalid input is caught and printed
     * instead of crashing the program.
     *
     * @param dataContext the current customer, order, and address data, passed to
     *                     menu actions that read or modify it
     */
    public static void renderMenu(
            DataContextDto dataContext
    ) {
        clearConsole();

        System.out.println("---- BS7 Projekt ----\n");

        Map<String, Runnable> menuOptions = new LinkedHashMap<>(){{
            put("Daten exportieren", () -> handleExportData(dataContext));
            put("Bestellungen filtern", () -> handleFilterOrders(dataContext));
            put("Analyse der Bestellungen in einem Zeitraum", () -> handleAnalyzeOrdersInTimespan(dataContext));
            put("Kunde mit höchstem Umsatz ermitteln", () -> handleHighestRevenueCustomer(dataContext));
            put("Umsatzreichster Kunde seit Zeitpunkt", () -> handleTopCustomerSinceTime(dataContext));
            put("Kunden mit höchstem Frühumsatz", () -> handleTopCustomersByEarlyRevenue(dataContext));
            put("Beenden", () -> System.exit(0));
        }};

        while (true) {
            byte listMenuCount = 1;
            for (Map.Entry<String, Runnable> entry : menuOptions.entrySet()) {
                System.out.println("\u001B[1m[" + listMenuCount + "]\u001B[0m " + entry.getKey());
                listMenuCount++;
            }

            System.out.println("Was möchtest du tun?");

            Scanner scanner = new Scanner(System.in);
            byte userChoice;
            try {
                userChoice = Byte.parseByte(scanner.nextLine());

                List<Map.Entry<String, Runnable>> options = new ArrayList<>(menuOptions.entrySet());
                options.get(userChoice - 1).getValue().run();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * Exports all current data via {@link Utility#exportData(DataContextDto)} and
     * returns to the main menu afterwards.
     *
     * @param dataContext the data to export
     */
    private static void handleExportData(DataContextDto dataContext) {
        Utility.exportData(dataContext);
    }

    /**
     * Prompts for optional order filters (customer, postal code, date, week,
     * weekday, month, year), runs {@link Order#filterOrders} with them, and
     * prints the resulting count and total revenue.
     *
     * @param dataContext the data to filter and read customers from
     */
    private static void handleFilterOrders(DataContextDto dataContext) {
        System.out.println("Welche Filteroptionen möchtest Du verwenden? (Leerlassen = Überspringen, \"exit\" = Zurück zum Hauptmenü)");

        Scanner scanner = new Scanner(System.in);

        try {
            Customer customer = null;
            String postalCode;
            LocalDate date = null;
            Byte week = null;
            DayOfWeek day = null;
            Month month = null;
            Year year = null;

            String customerEmail = readOrderAnalysisFilterInput(scanner, "Kunde (E-Mail-Adresse): ");
            if (customerEmail != null) {
                customer = dataContext.customers().get(customerEmail);
            }

            postalCode = readOrderAnalysisFilterInput(scanner, "Postleitzahl: ");

            String dateInput = readOrderAnalysisFilterInput(scanner, "Datum (yyyy-MM-dd): ");
            if (dateInput != null) {
                date = LocalDate.parse(dateInput);
            }

            String weekInput = readOrderAnalysisFilterInput(scanner, "Kalenderwoche (1-53): ");
            if (weekInput != null) {
                week = Byte.parseByte(weekInput);
            }

            String dayInput = readOrderAnalysisFilterInput(scanner, "Wochentag: ");
            if (dayInput != null) {
                switch (dayInput.toUpperCase()) {
                    case "MONTAG" -> dayInput = "monday";
                    case "DIENSTAG" -> dayInput = "tuesday";
                    case "MITTWOCH" -> dayInput = "wednesday";
                    case "DONNERSTAG" -> dayInput = "thursday";
                    case "FREITAG" -> dayInput = "friday";
                    case "SAMSTAG" -> dayInput = "saturday";
                    case "SONNTAG" -> dayInput = "sunday";
                    default -> System.out.println("Die Eingabe ist kein Wochentag");
                }

                day = DayOfWeek.valueOf(dayInput.toUpperCase());
            }

            String monthInput = readOrderAnalysisFilterInput(scanner, "Monat (1-12): ");
            if (monthInput != null) {
                month = Month.of(Integer.parseInt(monthInput));
            }

            String yearInput = readOrderAnalysisFilterInput(scanner, "Jahr: ");
            if (yearInput != null) {
                year = Year.of(Integer.parseInt(yearInput));
            }

            OrderAnalysisDto result = Order.filterOrders(
                    dataContext.orders(),
                    customer,
                    postalCode,
                    date,
                    week,
                    day,
                    month,
                    year
            );

            System.out.println("\nAnzahl Bestellungen: " + result.totalOrders());
            System.out.println("Gesamtsumme: " + result.totalOrderSum() + "€");
            System.out.println("\nDrücke Enter, um fortzufahren...");
            scanner.nextLine();

        } catch (MenuExitException e) {
            // Nutzer wollte abbrechen -> weiter zu renderMenu()
        } catch (Exception e) {
            System.out.println("Ungültige Eingabe: " + e.getMessage());
        }
    }

    /**
     * Prompts for an optional start and end date, runs
     * {@link Order#filterOrdersInTimespan} with them, and prints the resulting
     * count and total revenue.
     *
     * @param dataContext the data to filter
     */
    private static void handleAnalyzeOrdersInTimespan(DataContextDto dataContext) {
        System.out.println("Bitte Zeitraum eingeben (Leerlassen = unbegrenzt, \"exit\" = Zurück zum Hauptmenü)");
        Scanner scanner = new Scanner(System.in);

        try {
            LocalDate startDate = null;
            LocalDate endDate = null;

            String startInput = readOrderAnalysisFilterInput(scanner, "Startdatum (yyyy-MM-dd): ");
            if (startInput != null) {
                startDate = LocalDate.parse(startInput);
            }

            String endInput = readOrderAnalysisFilterInput(scanner, "Enddatum (yyyy-MM-dd): ");
            if (endInput != null) {
                endDate = LocalDate.parse(endInput);
            }

            OrderAnalysisDto result = Order.filterOrdersInTimespan(
                    dataContext.orders(),
                    startDate,
                    endDate
            );

            System.out.println("\nAnzahl Bestellungen: " + result.totalOrders());
            System.out.println("Gesamtsumme: " + result.totalOrderSum() + "€");
            System.out.println("\nDrücke Enter, um fortzufahren...");
            scanner.nextLine();

        } catch (MenuExitException e) {
            // Nutzer wollte abbrechen
        } catch (Exception e) {
            System.out.println("Ungültige Eingabe: " + e.getMessage());
        }
    }

    /**
     * Determines and prints the customer with the highest total revenue via
     * {@link Customer#getCustomerWithHighestSalesVolume}.
     *
     * @param dataContext the data to evaluate
     */
    private static void handleHighestRevenueCustomer(DataContextDto dataContext) {
        CustomerRevenueDto result = Customer.getCustomerWithHighestSalesVolume(dataContext.orders());

        if (result.customer() == null) {
            System.out.println("\nEs liegen keine Bestellungen vor.");
        } else {
            System.out.println("\nKunde mit höchstem Umsatz: " +
                    result.customer().getFirstname() + " " + result.customer().getLastname() +
                    " (" + result.salesVolume() + "€)");
        }

        System.out.println("\nDrücke Enter, um fortzufahren...");
        new Scanner(System.in).nextLine();
    }

    /**
     * Prompts for optional time filters (date, week, weekday, month, year),
     * runs {@link Customer#getCustomerSalesVolumeSinceTime} with them, and
     * prints the resulting top customer.
     *
     * @param dataContext the data to evaluate
     */
    private static void handleTopCustomerSinceTime(DataContextDto dataContext) {
        System.out.println("Welche Filteroptionen möchtest Du verwenden? (Leerlassen = Überspringen, \"exit\" = Zurück zum Hauptmenü)");
        Scanner scanner = new Scanner(System.in);

        try {
            LocalDate date = null;
            Byte week = null;
            DayOfWeek day = null;
            Month month = null;
            Year year = null;

            String dateInput = readOrderAnalysisFilterInput(scanner, "Datum ab (yyyy-MM-dd): ");
            if (dateInput != null) {
                date = LocalDate.parse(dateInput);
            }

            String weekInput = readOrderAnalysisFilterInput(scanner, "Kalenderwoche (1-53): ");
            if (weekInput != null) {
                week = Byte.parseByte(weekInput);
            }

            String dayInput = readOrderAnalysisFilterInput(scanner, "Wochentag (z. B. MONDAY): ");
            if (dayInput != null) {
                day = DayOfWeek.valueOf(dayInput.toUpperCase());
            }

            String monthInput = readOrderAnalysisFilterInput(scanner, "Monat (1-12): ");
            if (monthInput != null) {
                month = Month.of(Integer.parseInt(monthInput));
            }

            String yearInput = readOrderAnalysisFilterInput(scanner, "Jahr: ");
            if (yearInput != null) {
                year = Year.of(Integer.parseInt(yearInput));
            }

            CustomerRevenueDto result = Customer.getCustomerSalesVolumeSinceTime(
                    dataContext.orders(),
                    date,
                    week,
                    day,
                    month,
                    year
            );

            if (result.customer() == null) {
                System.out.println("\nKeine passenden Bestellungen gefunden.");
            } else {
                System.out.println("\nUmsatzreichster Kunde: " +
                        result.customer().getFirstname() + " " + result.customer().getLastname() +
                        " (" + result.salesVolume() + "€)");
            }

            System.out.println("\nDrücke Enter, um fortzufahren...");
            scanner.nextLine();

        } catch (MenuExitException e) {
            // Nutzer wollte abbrechen
        } catch (Exception e) {
            System.out.println("Ungültige Eingabe: " + e.getMessage());
        }
    }

    /**
     * Prompts for the absolute path to a CSV file, repeating until a
     * {@code .csv}-extension path is entered, and reads it via
     * {@link Utility#readLinesFromFlatfile(String)}.
     *
     * @return the file's lines, or {@code null} if reading failed
     */
    public static String[] getResourceFile() {
        String path;

        while (true) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Wo befindet sich die CSV-Datei (Absoluter Pfad):");
            path = scanner.nextLine();

            if(!path.toLowerCase().endsWith(".csv")) {
                System.out.println("Die angegebene Datei ist keine CSV-Datei!\n");
            } else {
                break;

            }
        }

        return Utility.readLinesFromFlatfile(path);
    }

    /**
     * Clears the console by running {@code cmd /c cls} and waiting for it to
     * finish. Windows-specific; errors are caught and printed rather than thrown.
     */
    public static void clearConsole() {
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (Exception e) {
            System.out.println("Fehler beim leeren der Konsole: " + e.getMessage());

        }
    }

    /**
     * Prompts for and reads a single filter value, trimmed. Returns {@code null}
     * if left empty ("skip this filter"), or throws a {@link MenuExitException}
     * if the user typed "exit".
     *
     * @param scanner the scanner to read from
     * @param prompt  the prompt text shown to the user
     * @return the entered value, or {@code null} if left empty
     */
    private static String readOrderAnalysisFilterInput(Scanner scanner, String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();

        if (input.equalsIgnoreCase("exit")) {
            throw new MenuExitException();
        }

        return input.isEmpty() ? null : input;
    }

    /**
     * Prompts for a time window (in days) and a result count, runs
     * {@link Order#getTopCustomersByEarlyRevenue} with them, and prints the
     * resulting ranked list of customers.
     *
     * @param dataContext the data to evaluate
     */
    private static void handleTopCustomersByEarlyRevenue(DataContextDto dataContext) {
        System.out.println("Analyse: Kunden mit höchstem Umsatz innerhalb eines Zeitfensters ab ihrem \"Kunde seit\"-Datum (\"exit\" = Zurück zum Hauptmenü)");
        Scanner scanner = new Scanner(System.in);

        try {
            String windowInput = readOrderAnalysisFilterInput(scanner, "Zeitfenster in Tagen (Enter = 21): ");
            int windowInDays = windowInput != null ? Integer.parseInt(windowInput) : 21;

            String limitInput = readOrderAnalysisFilterInput(scanner, "Anzahl der anzuzeigenden Kunden (Enter = 5): ");
            int rowsLimit = limitInput != null ? Integer.parseInt(limitInput) : 5;

            List<CustomerRevenueDto> results = Order.getTopCustomersByEarlyRevenue(
                    dataContext.orders(),
                    windowInDays,
                    rowsLimit
            );

            if (results.isEmpty()) {
                System.out.println("\nKeine passenden Bestellungen gefunden.");
            } else {
                System.out.println();
                int rank = 1;
                for (CustomerRevenueDto dto : results) {
                    System.out.println(rank + ". " + dto.customer().getFirstname() + " " + dto.customer().getLastname() + " (" + dto.salesVolume() + "€)");
                    rank++;
                }
            }

            System.out.println("\nDrücke Enter, um fortzufahren...");
            scanner.nextLine();

        } catch (MenuExitException e) {
            // Nutzer wollte abbrechen
        } catch (Exception e) {
            System.out.println("Ungültige Eingabe: " + e.getMessage());
        }
    }

    /**
     * Signals that the user typed "exit" during filter input and wants to
     * return to the main menu.
     */
    private static class MenuExitException extends RuntimeException {}
}