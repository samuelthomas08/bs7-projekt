package bs7projekt.src;
import bs7projekt.src.models.Address;
import bs7projekt.src.models.Customer;
import bs7projekt.src.models.Order;
import bs7projekt.src.utility.Tui;
import bs7projekt.src.utility.Utility;

import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        String[] lines = Tui.getResourceFile();

        Map<String, Customer> customers = new HashMap<>();
        Map<Integer, Order> orders = new HashMap<>();
        Map<Integer, Address> addresses = new HashMap<>();

        // Assign data from lines[] to the according HashMaps
        Utility.importData(
                lines,
                customers,
                orders,
                addresses
        );

        Tui.renderMenu(
                customers,
                orders,
                addresses
        );
    }
}