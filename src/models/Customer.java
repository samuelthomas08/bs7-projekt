package bs7projekt.src.models;

import bs7projekt.src.dtos.CustomerRevenueDto;

import java.time.DayOfWeek;
import java.time.Month;
import java.time.Year;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Customer {

    private int id;
    private String firstname;
    private String lastname;
    private Date birthday;
    private String email;

    public Customer(int id, String firstname, String lastname, Date birthday, String email) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.birthday = birthday;
        this.email = email;
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

    public void setId(int id) {
        this.id = id;
    }

    // ##########################################
    // Custom Methods
    // ##########################################
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

            if (week != null &&
                    calendar.get(Calendar.WEEK_OF_YEAR) != week) {
                continue;
            }

            if (day != null) {
                DayOfWeek orderDay = DayOfWeek.of(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY ? 7 : calendar.get(Calendar.DAY_OF_WEEK) - 1);

                if (orderDay != day) {
                    continue;
                }
            }

            if (month != null &&
                    calendar.get(Calendar.MONTH) + 1 != month.getValue()) {
                continue;
            }

            if (year != null &&
                    calendar.get(Calendar.YEAR) != year.getValue()) {
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
    }
}
