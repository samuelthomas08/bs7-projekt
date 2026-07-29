package bs7projekt.src.utility;
import bs7projekt.src.dtos.DataContextDto;
import bs7projekt.src.models.Address;
import bs7projekt.src.models.Customer;
import bs7projekt.src.models.Order;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Utility {

    /**
     * Reads a flatfile (e.g. a pipe-delimited CSV file) from the given path and
     * returns its content as a {@code String[]}, one array entry per line.
     *
     * @param filePath the absolute path to the file that should be read
     * @return a {@code String[]} containing one array entry per line of the file,
     *         or {@code null} if an error occurred while reading the file
     */
    public static String[] readLinesFromFlatfile(String filePath) {
        String[] data = null;
        ArrayList<String> tmpData = new ArrayList<>();
        Path path = java.nio.file.Paths.get(filePath);

        try (BufferedReader brd =
                     Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line = "";
            while((line = brd.readLine()) != null) {
                tmpData.add(line);
            }
            data = tmpData.toArray(new String[0]);
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
        return data;
    }

    /**
     * Parses the raw lines returned by {@link #readLinesFromFlatfile(String)} into
     * the project's domain models and stores them in the given {@link DataContextDto}.
     * Customers and addresses are deduplicated across lines; lines that remain invalid
     * after normalization are skipped and logged instead of imported.
     *
     * @param lines       the raw lines of the source file
     * @param dataContext the {@link DataContextDto} whose maps will be populated
     */
    public static void importData(
            String[] lines,
            DataContextDto dataContext
    ) {
        int addressId = 1;
        int orderId = 1;
        int customerId = 1;
        int skippedLines = 0;

        Map<String, Integer> addressLookup = new HashMap<>();

        for (String line : lines) {
            try {
                String[] data = line.split("\\|");

                // ------------------------
                // Customer
                // ------------------------
                String email = data[4];
                Customer customer = dataContext.customers().get(email);

                if (customer == null) {
                    customer = new Customer(
                            customerId++,
                            data[0],
                            data[1],
                            parseDate(data[2]),
                            data[4],
                            parseDate(data[3])
                    );
                    dataContext.customers().put(email, customer);
                }

                // ------------------------
                // Address
                // ------------------------
                String addressKey =
                        data[5] + "|" +
                                data[6] + "|" +
                                data[7] + "|" +
                                data[8];

                Integer existingAddressId = addressLookup.get(addressKey);
                Address address;

                if (existingAddressId == null) {
                    address = new Address(
                            addressId,
                            data[5],
                            data[6],
                            data[7],
                            data[8]
                    );

                    dataContext.addresses().put(addressId, address);
                    addressLookup.put(addressKey, addressId);
                    addressId++;
                } else {
                    address = dataContext.addresses().get(existingAddressId);
                }

                // ------------------------
                // Order
                // ------------------------
                Order order = new Order(
                        orderId,
                        parseDate(data[9]),
                        new BigDecimal(normalizePrice(data[10])),
                        customer,
                        address
                );

                dataContext.orders().put(orderId, order);
                orderId++;

            } catch (Exception e) {
                skippedLines++;
                System.out.println("Fehlerhafte Zeile übersprungen: " + line + " (" + e.getMessage() + ")");
            }
        }

        System.out.println(skippedLines + " Zeile(n) wegen unkorrigierbarer Fehler übersprungen.");
    }

    /**
     * Exports all data held by the given {@link DataContextDto} into three separate
     * CSV files ({@code customers.csv}, {@code addresses.csv}, {@code orders.csv})
     * in a user-specified directory, which is created if it does not yet exist.
     *
     * @param dataContext the {@link DataContextDto} containing the data to be exported
     */
    public static void exportData(
            DataContextDto dataContext
    ) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("In welches Verzeichnis sollen die Daten exportiert werden? ");
        String outputPath = scanner.next();

        File dir = new File(outputPath);
        System.out.println("Export-Pfad: " + dir.getAbsolutePath());
        if (!dir.exists()) {
            dir.mkdirs();
        }
        exportCustomers(outputPath + "/customers.csv", dataContext.customers());
        exportAddresses(outputPath + "/addresses.csv", dataContext.addresses());
        exportOrders(outputPath + "/orders.csv", dataContext.orders());

        System.out.println("Der Export wurde erfolgreich durchgeführt!");
    }

    /**
     * Writes all customers from the given map to a CSV file at the specified path,
     * with a header row followed by one comma-separated line per customer.
     *
     * @param path        the full file path (including file name) to write to
     * @param customerMap the map of customers to export, keyed by email address
     */
    private static void exportCustomers(String path, Map<String, Customer> customerMap) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            writer.write("id,firstname,lastname,birthday,email,customerSince");
            writer.newLine();

            for (Customer c : customerMap.values()) {
                writer.write(
                        c.getId() + "," +
                                c.getFirstname() + "," +
                                c.getLastname() + "," +
                                c.getBirthday() + "," +
                                c.getEmail() + "," +
                                c.getCustomerSince()
                );
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes all addresses from the given map to a CSV file at the specified path,
     * with a header row followed by one comma-separated line per address.
     *
     * @param path       the full file path (including file name) to write to
     * @param addressMap the map of addresses to export, keyed by internal address ID
     */
    private static void exportAddresses(String path, Map<Integer, Address> addressMap) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            writer.write("id,street,houseNumber,postalCode,location");
            writer.newLine();

            for (Address a : addressMap.values()) {
                writer.write(
                        a.getId() + "," +
                                a.getStreet() + "," +
                                a.getHouseNumber() + "," +
                                a.getPostalCode() + "," +
                                a.getLocation()
                );
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes all orders from the given map to a CSV file at the specified path.
     * Customer and address references are exported as their IDs (foreign keys)
     * rather than full objects, so the file can be loaded into a relational database.
     *
     * @param path     the full file path (including file name) to write to
     * @param orderMap the map of orders to export, keyed by internal order ID
     */
    private static void exportOrders(String path, Map<Integer, Order> orderMap) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            writer.write("id,orderDate,orderPrice,customerId,addressId");
            writer.newLine();

            for (Order o : orderMap.values()) {
                writer.write(
                        o.getId() + "," +
                                o.getOrderDate() + "," +
                                o.getOrderPrice() + "," +
                                o.getCustomer().getId() + "," +
                                o.getAddress().getId()
                );
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Replaces German-style decimal commas with dots (e.g. {@code "46,85"} to
     * {@code "46.85"}) so the value can be parsed by {@link Double#parseDouble(String)}.
     *
     * @param raw the raw, potentially comma-separated price string
     * @return the normalized price string, using a dot as the decimal separator
     */
    private static String normalizePrice(String raw) {
        return raw.replace(",", ".");
    }

    /**
     * Lookup table mapping full German month names to their two-digit numeric
     * representation, used by {@link #normalizeDate(String)}.
     */
    private static final Map<String, String> GERMAN_MONTHS = new HashMap<>() {{
        put("Januar", "01"); put("Februar", "02"); put("März", "03"); put("April", "04");
        put("Mai", "05"); put("Juni", "06"); put("Juli", "07"); put("August", "08");
        put("September", "09"); put("Oktober", "10"); put("November", "11"); put("Dezember", "12");
    }};

    /**
     * Normalizes a raw date string into ISO-8601 format ({@code yyyy-MM-dd}) by
     * replacing dots with dashes and spelled-out German month names with their
     * numeric value. Does not validate that the resulting date actually exists.
     *
     * @param raw the raw date string, potentially malformed
     * @return the normalized date string in ISO-8601 format
     */
    private static String normalizeDate(String raw) {
        raw = raw.replace(".", "-"); // 2021.08.24 -> 2021-08-24

        for (Map.Entry<String, String> entry : GERMAN_MONTHS.entrySet()) {
            raw = raw.replace(entry.getKey(), entry.getValue()); // 2022-Juli-04 -> 2022-07-04
        }

        return raw;
    }

    /**
     * Normalizes and strictly parses a raw date string into a {@link LocalDate}.
     * Unlike {@link java.sql.Date#valueOf(String)}, this throws on impossible
     * calendar dates (e.g. {@code "1966-02-31"}) instead of silently rolling them over.
     *
     * @param raw the raw date string to normalize and parse
     * @return a {@link LocalDate} representing the parsed date
     * @throws java.time.format.DateTimeParseException if the normalized string does
     *         not represent a valid, existing calendar date
     */
    private static LocalDate parseDate(String raw) throws DateTimeParseException {
        return LocalDate.parse(normalizeDate(raw));
    }
}