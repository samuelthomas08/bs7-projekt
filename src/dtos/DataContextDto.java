package bs7projekt.src.dtos;

import bs7projekt.src.models.Address;
import bs7projekt.src.models.Customer;
import bs7projekt.src.models.Order;

import java.util.Map;

public record DataContextDto(
        Map<String, Customer> customers,
        Map<Integer, Order> orders,
        Map<Integer, Address> addresses
) {
}