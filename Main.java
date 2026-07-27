package bs7projekt;
import bs7projekt.src.models.Address;
import bs7projekt.src.models.Customer;
import bs7projekt.src.models.Order;
import bs7projekt.src.utility.Tui;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        String[] lines = Tui.getResourceFile();

        Map<String, Customer> customers = new HashMap<>();
        Map<Integer, Order> orders = new HashMap<>();
        Map<Integer, Address> addresses = new HashMap<>();

        importData(
                lines,
                customers,
                orders,
                addresses
        );
    }

    /**
     * This method will read all the lines, that are returned by {@code Utility.readLinesFromFlatfile()}
     * It analyzes them and splits them into several data models, which will fit the data model created for the project.
     * Last but not least, the filtered data will be written into the HashMaps, defined above.
     * @param lines
     * @param customers
     * @param orders
     * @param addresses
     */
    public static void importData(
            String[] lines,
            Map<String, Customer> customers,
            Map<Integer, Order> orders,
            Map<Integer, Address> addresses
    ) {
        int addressId = 1;
        int orderId = 1;
        int customerId = 1;

        Map<String, Integer> addressLookup = new HashMap<>();

        for (String line : lines) {
            String[] data = line.split("\\|");

            // ------------------------
            // Customer
            // ------------------------
            String email = data[4];
            Customer customer = customers.get(email);

            if (customer == null) {
                customer = new Customer(
                        customerId++,
                        data[0],
                        data[1],
                        Date.valueOf(data[2]),
                        data[4]
                );
                customers.put(email, customer);
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

                addresses.put(addressId, address);
                addressLookup.put(addressKey, addressId);
                addressId++;
            } else {
                address = addresses.get(existingAddressId);
            }

            // ------------------------
            // Order
            // ------------------------
            Order order = new Order(
                    orderId,
                    Date.valueOf(data[9]),
                    Double.parseDouble(data[10]),
                    customer,
                    address
            );

            orders.put(orderId, order);
            orderId++;
        }
    }
}