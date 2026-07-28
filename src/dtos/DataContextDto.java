package bs7projekt.src.dtos;

import bs7projekt.src.models.Address;
import bs7projekt.src.models.Customer;
import bs7projekt.src.models.Order;

import java.util.Map;

public class DataContextDto {

    private Map<String, Customer> customers;
    private Map<Integer, Order> orders;
    private Map<Integer, Address> addresses;

    public DataContextDto(
            Map<String, Customer> customers,
            Map<Integer, Order> orders,
            Map<Integer, Address> addresses
    ) {
        this.customers = customers;
        this.orders = orders;
        this.addresses = addresses;
    }

    public Map<String, Customer> getCustomers() {
        return customers;
    }

    public Map<Integer, Order> getOrders() {
        return orders;
    }

    public Map<Integer, Address> getAddresses() {
        return addresses;
    }
}