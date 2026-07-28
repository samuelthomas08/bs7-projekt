package bs7projekt.src.models;

import bs7projekt.src.dtos.CustomerRevenueDto;

import java.time.DayOfWeek;
import java.time.Month;
import java.time.Year;
import java.util.*;

public class Customer {

    private int id;
    private String firstname;
    private String lastname;
    private Date birthday;
    private String email;
    private Date customerSince;

    public Customer(int id, String firstname, String lastname, Date birthday, String email, Date customerSince) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.birthday = birthday;
        this.email = email;
        this.customerSince = customerSince;
    }

    // ##########################################
    // Getter & Setter
    // ##########################################
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

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getCustomerSince() {
        return customerSince;
    }

    public void setCustomerSince(Date customerSince) {
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
        Map<Customer, Double> customerSales = new HashMap<>();

        for (Order order : orderMap.values()) {
            Customer customer = order.getCustomer();
            double currentSales = customerSales.getOrDefault(customer, 0.0);
            customerSales.put(
                    customer,
                    currentSales + order.getOrderPrice()
            );
        }

        Customer highestCustomer = null;
        double highestSales = 0;

        for (Map.Entry<Customer, Double> entry : customerSales.entrySet()) {
            if (entry.getValue() > highestSales) {
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
            Date date,
            Byte week,
            DayOfWeek day,
            Month month,
            Year year
    ) {
        Map<Customer, Double> customerSales = new HashMap<>();

        for (Order order : orderMap.values()) {
            Date orderDate = order.getOrderDate();

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(orderDate);

            if (date != null && orderDate.before(date)) {
                continue;
            }

            if (week != null && calendar.get(Calendar.WEEK_OF_YEAR) != week) {
                continue;
            }

            if (day != null) {
                DayOfWeek orderDay = DayOfWeek.of(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY ? 7 : calendar.get(Calendar.DAY_OF_WEEK) - 1);

                if (orderDay != day) {
                    continue;
                }
            }

            if (month != null && calendar.get(Calendar.MONTH) + 1 != month.getValue()) {
                continue;
            }

            if (year != null && calendar.get(Calendar.YEAR) != year.getValue()) {
                continue;
            }

            Customer customer = order.getCustomer();
            double currentSales = customerSales.getOrDefault(customer, 0.0);

            customerSales.put(
                    customer,
                    currentSales + order.getOrderPrice()
            );
        }

        Customer highestCustomer = null;
        double highestSalesVolume = 0;

        for (Map.Entry<Customer, Double> entry : customerSales.entrySet()) {
            if (entry.getValue() > highestSalesVolume) {
                highestCustomer = entry.getKey();
                highestSalesVolume = entry.getValue();
            }
        }

        return new CustomerRevenueDto(highestCustomer, highestSalesVolume);
    }}
