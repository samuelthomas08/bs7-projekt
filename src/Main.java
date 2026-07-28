package bs7projekt.src;
import bs7projekt.src.dtos.DataContextDto;
import bs7projekt.src.utility.Tui;
import bs7projekt.src.utility.Utility;

import java.util.HashMap;

public class Main {

    public static void main(String[] args) {
        String[] lines = Tui.getResourceFile();

        DataContextDto dataContext = new DataContextDto(
                new HashMap<>(),
                new HashMap<>(),
                new HashMap<>()
        );

        // Assign data from lines[] to the according HashMaps
        Utility.importData(
                lines,
                dataContext
        );

        // Render menu and start "production"-loop
        Tui.renderMenu(
                dataContext
        );
    }
}