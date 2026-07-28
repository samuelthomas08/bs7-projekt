package bs7projekt.src.utility;

import bs7projekt.src.models.Address;
import bs7projekt.src.models.Customer;
import bs7projekt.src.models.Order;

import java.util.*;

public class Tui {

    /**
     *
     * @param customerMap
     * @param orderMap
     * @param addressMap
     */
    public static void renderMenu(
            Map<String, Customer> customerMap,
            Map<Integer, Order> orderMap,
            Map<Integer, Address> addressMap
    ) {
        clearConsole();

        System.out.println("---- BS7 Projekt ----\n");

        Map<String, Runnable> menuOptions = new HashMap<>(){{
            put("Daten exportieren", () -> {
                Utility.exportData(orderMap, customerMap, addressMap);
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
     * Fetches the path to the .csv-file and tries to read it with the {@code Utility.readLinesFromFlatfile()} method
     * @return
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
     * Clears the current console output
     */
    public static void clearConsole() {
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (Exception e) {
            System.out.println("Fehler beim leeren der Konsole: " + e.getMessage());

        }
    }
}
