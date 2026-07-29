package bs7projekt.src.models;

import bs7projekt.src.dtos.CustomerRevenueDto;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.temporal.WeekFields;
import java.util.*;

public class Customer {

    private int id;
    private String firstname;
    private String lastname;
    private LocalDate birthday;
    private String email;
    private LocalDate customerSince;

    public Customer(int id, String firstname, String lastname, LocalDate birthday, String email, LocalDate customerSince) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.birthday = birthday;
        this.email = email;
        this.customerSince = customerSince;
    }

    public int getId() {
        return id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getCustomerSince() {
        return customerSince;
    }

    public void setCustomerSince(LocalDate customerSince) {
        this.customerSince = customerSince;
    }

    public void setId(int id) {
        this.id = id;
    }


    // ##########################################
    // Custom Methods
    // ##########################################
    /**
     * Finds the customer with the highest total revenue across all orders.
     * Revenue is summed per customer via {@code orderPrice}, and the customer
     * with the largest sum is returned.
     *
     * @param orderMap the map of all orders to evaluate
     * @return a {@link CustomerRevenueDto} containing the customer with the highest
     *         total revenue and their total sales volume
     */
    public static CustomerRevenueDto getCustomerWithHighestSalesVolume(
            Map<Integer, Order> orderMap
    ) {
        Map<Customer, BigDecimal> customerSales = new HashMap<>();

        for (Order order : orderMap.values()) {
            Customer customer = order.getCustomer();
            BigDecimal currentSales = customerSales.getOrDefault(customer, new BigDecimal(0.0));
            customerSales.put(
                    customer,
                    currentSales.add(order.getOrderPrice())
            );
        }

        Customer highestCustomer = null;
        BigDecimal highestSales = new BigDecimal(0);

        for (Map.Entry<Customer, BigDecimal> entry : customerSales.entrySet()) {
            if (entry.getValue().compareTo(highestSales) > 0) {
                highestSales = entry.getValue();
                highestCustomer = entry.getKey();
            }
        }

        return new CustomerRevenueDto(highestCustomer, highestSales);
    }

    /**
     * Finds the customer with the highest revenue among orders matching the given
     * filters. All filter parameters are optional (nullable); any that are
     * {@code null} are ignored, and only orders satisfying every non-null filter
     * are counted towards a customer's revenue.
     *
     * @param orderMap the map of all orders to evaluate
     * @param date     only include orders on or after this date, or {@code null} to ignore
     * @param week     only include orders in this ISO week of the year, or {@code null} to ignore
     * @param day      only include orders on this weekday, or {@code null} to ignore
     * @param month    only include orders in this month, or {@code null} to ignore
     * @param year     only include orders in this year, or {@code null} to ignore
     * @return a {@link CustomerRevenueDto} containing the matching customer with the
     *         highest revenue and their sales volume
     */
    public static CustomerRevenueDto getCustomerSalesVolumeSinceTime(
            Map<Integer, Order> orderMap,
            LocalDate date,
            Byte week,
            DayOfWeek day,
            Month month,
            Year year
    ) {
        Map<Customer, BigDecimal> customerSales = new HashMap<>();

        for (Order order : orderMap.values()) {
            LocalDate orderDate = order.getOrderDate();

            if (date != null && orderDate.isBefore(date)) {
                continue;
            }

            if (week != null && orderDate.get(WeekFields.ISO.weekOfWeekBasedYear()) != week) {
                continue;
            }

            if (day != null && orderDate.getDayOfWeek() != day) {
                continue;
            }

            if (month != null && orderDate.getMonth() != month) {
                continue;
            }

            if (year != null && orderDate.getYear() != year.getValue()) {
                continue;
            }

            Customer customer = order.getCustomer();
            BigDecimal currentSales = customerSales.getOrDefault(customer, new BigDecimal(0.0));

            customerSales.put(
                    customer,
                    currentSales.add(order.getOrderPrice())
            );
        }

        Customer highestCustomer = null;
        BigDecimal highestSalesVolume = new BigDecimal(0);

        for (Map.Entry<Customer, BigDecimal> entry : customerSales.entrySet()) {
            if (entry.getValue().compareTo(highestSalesVolume) > 0) {
                highestCustomer = entry.getKey();
                highestSalesVolume = entry.getValue();
            }
        }

        return new CustomerRevenueDto(highestCustomer, highestSalesVolume);
    }
}