package bs7projekt.src.utility;

import bs7projekt.src.dtos.DataContextDto;
import bs7projekt.src.models.Address;
import bs7projekt.src.models.Customer;
import bs7projekt.src.models.Order;

import java.util.*;

public class Tui {

    /**
     * Renders the main text-based menu to the console and handles a single round of
     * user interaction.
     * <p>
     * The console is cleared first, then a title is printed, followed by a numbered
     * list of the available menu options. Menu options are represented internally as
     * a {@link Map} from a human-readable label to a {@link Runnable} that performs the
     * corresponding action; the {@link DataContextDto} is captured by these lambdas so
     * each action operates on the current application state.
     * <p>
     * The user is prompted to enter the number of the desired menu option via the
     * console. To resolve that number back to the correct action, the map's entries are
     * copied into a {@link List}, which allows positional access by index (note that
     * {@link HashMap} itself does not guarantee a stable iteration order, but since the
     * printed numbering and the list conversion both iterate over the same map instance
     * within a single call, they are consistent with each other for the duration of this
     * method call).
     * <p>
     * If the entered number does not correspond to a valid menu option (e.g. it is out
     * of range, or not a valid byte), the resulting exception is caught and its message
     * is printed to the console rather than crashing the program.
     * <p>
     * After the selected action has been executed (or the error has been printed), the
     * console is cleared again before the method returns.
     *
     * @param dataContext the {@link DataContextDto} holding the current customer, order,
     *                     and address data, passed to menu actions that need to read or
     *                     modify it (e.g. exporting data)
     */
    public static void renderMenu(
            DataContextDto dataContext
    ) {
        clearConsole();

        System.out.println("---- BS7 Projekt ----\n");

        Map<String, Runnable> menuOptions = new HashMap<>(){{
            put("Daten exportieren", () -> {
                Utility.exportData(dataContext);
            });
            put("Bestellungen filtern", () -> {});
        }};

        byte listMenuCount = 1;
        for(Map.Entry<String, Runnable> entry : menuOptions.entrySet()) {
            System.out.println("[" + listMenuCount + "]" + entry.getKey());
            listMenuCount++;
        }

        System.out.println("Was möchtest du tun?");

        Scanner scanner = new Scanner(System.in);
        byte userChoice = Byte.parseByte(scanner.nextLine());

        List<Map.Entry<String, Runnable>> options = new ArrayList<>(menuOptions.entrySet());

        try {
            options.get(userChoice - 1).getValue().run();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        clearConsole();
    }

    /**
     * Prompts the user via the console to enter the absolute path to a CSV file,
     * validates that the entered path actually points to a file with a {@code .csv}
     * extension, and then delegates to {@link Utility#readLinesFromFlatfile(String)}
     * to read its content.
     * <p>
     * The user is repeatedly prompted in a loop until a path ending in {@code .csv}
     * is entered; the file extension is determined by looking at the substring after
     * the last backslash ({@code \}) and the last dot in the resulting file name. Note
     * that this validation only checks the file extension of the entered string and
     * does not verify that the file actually exists or is readable — that is handled
     * separately by {@link Utility#readLinesFromFlatfile(String)}.
     *
     * @return a {@code String[]} containing one array entry per line of the selected
     *         CSV file, as returned by {@link Utility#readLinesFromFlatfile(String)}
     *         (which may itself return {@code null} if the file cannot be read)
     */
    public static String[] getResourceFile() {
        String path;

        while (true) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Wo befindet sich die CSV-Datei (Absoluter Pfad):");
            path = scanner.nextLine();

            String extension = (path.substring(path.lastIndexOf('\\') + 1).lastIndexOf('.') > 0) ? path.substring(path.lastIndexOf('\\') + 1).substring(path.substring(path.lastIndexOf('\\') + 1).lastIndexOf('.') + 1) : "";

            if(!extension.equals("csv")) {
                System.out.println("Die angegebene Datei ist keine CSV-Datei!\n");
            } else {
                break;

            }
        }

        return Utility.readLinesFromFlatfile(path);
    }

    /**
     * Clears the current console output.
     * <p>
     * This is implemented by spawning a new {@code cmd /c cls} process and waiting for
     * it to finish, with {@link ProcessBuilder#inheritIO()} used so the child process
     * shares the current process's standard input, output, and error streams (allowing
     * the {@code cls} command to actually affect the visible console window). Note that
     * this implementation is Windows-specific, since {@code cmd} and {@code cls} are not
     * available on other operating systems.
     * <p>
     * If starting or waiting for the process fails for any reason (e.g. an
     * {@link InterruptedException} or {@link java.io.IOException}), the error is caught
     * and a message is printed to the console instead of propagating the exception
     * further.
     */
    public static void clearConsole() {
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (Exception e) {
            System.out.println("Fehler beim leeren der Konsole: " + e.getMessage());

        }
    }
}