package bs7projekt.src.utility;
import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class Utility {
    /**
    * Reads the file specified in "filePath" and returns a String-array with every
    * line of the file as an array entry. In any error cases
    * the array will be null.
    * @param filePath Absolute path to the file to read.
    * @return File content or null if an error occurs.
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

}
