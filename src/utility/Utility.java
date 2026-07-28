package bs7projekt.src.utility;
import bs7projekt.src.models.Address;
import bs7projekt.src.models.Customer;
import bs7projekt.src.models.Order;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

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


    /**
     * This method will read all the lines, that are returned by {@code Utility.readLinesFromFlatfile()}
     * It analyzes them and splits them into several data models, which will fit the data model created for the project.
     * Last but not least, the filtered data will be written into the HashMaps, defined above.
     *
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
            try {
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

            } catch (Exception e) {}
        }
    }

    /**
     * This method is designed to export all the data, that was read in the {@code importData()} method
     * and export the Maps, that were used to manage the data.
     *
     * @param orderMap
     * @param customerMap
     * @param addressMap
     */
    public static void exportData(
            Map<Integer, Order> orderMap,
            Map<String, Customer> customerMap,
            Map<Integer, Address> addressMap
    ) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("In welches Verzeichnis sollen die Daten exportiert werden? ");
        String outputPath = scanner.next();

        File dir = new File(outputPath);
        System.out.println("Export-Pfad: " + dir.getAbsolutePath());
        if (!dir.exists()) {
            dir.mkdirs();
        }
        exportCustomers(outputPath + "/customers.csv", customerMap);
        exportAddresses(outputPath + "/addresses.csv", addressMap);
        exportOrders(outputPath + "/orders.csv", orderMap);

        System.out.println("Der Export wurde erfolgreich durchgeführt!");
    }

    private static void exportCustomers(String path, Map<String, Customer> customerMap) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            writer.write("id,firstname,lastname,birthday,email");
            writer.newLine();

            for (Customer c : customerMap.values()) {
                writer.write(
                        c.getId() + "," +
                                c.getFirstname() + "," +
                                c.getLastname() + "," +
                                c.getBirthday() + "," +
                                c.getEmail()
                );
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

}
