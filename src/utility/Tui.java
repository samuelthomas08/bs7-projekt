package bs7projekt.src.utility;

import java.util.Scanner;

public class Tui {

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

}
