package bs7projekt.src.models;

import bs7projekt.src.dtos.OrderAnalysisDto;

import java.time.DayOfWeek;
import java.time.Month;
import java.time.Year;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class Order {
    
    private int id;
    private Date orderDate;
    private double orderPrice;
    private Customer customer;
    private Address address;

    public Order(int id, Date orderDate, double orderPrice, Customer customer, Address address) {
        this.id = id;
        this.orderDate = orderDate;
        this.orderPrice = orderPrice;
        this.customer = customer;
        this.address = address;
    }


    // ##########################################
    // Getter & Setter
    // ##########################################
    public int getId() {
        return id;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public double getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(double orderPrice) {
        this.orderPrice = orderPrice;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setId(int id) {
        this.id = id;
    }


    // ##########################################
    // Custom Methods
    // ##########################################
    public static OrderAnalysisDto filterOrders(
            Map<Integer, Order> orderMap,
            Customer customer,
            String postalCode,
            Date date,
            Byte week,
            DayOfWeek day,
            Month month,
            Year year
    ) {
        double sum = 0;
        int count = 0;

        for (Order order : orderMap.values()) {
            Date orderDate = order.getOrderDate();

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(orderDate);

            if (customer != null && !order.getCustomer().equals(customer)) {
                continue;
            }
            if (postalCode != null && !order.getAddress().getPostalCode().equals(postalCode)) {
                continue;
            }
            if (date != null && !orderDate.equals(date)) {
                continue;
            }
            if (week != null && calendar.get(Calendar.WEEK_OF_YEAR) != week) {
                continue;
            }
            if (day != null) {
                DayOfWeek orderDay = DayOfWeek.of(
                        calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
                                ? 7
                                : calendar.get(Calendar.DAY_OF_WEEK) - 1
                );

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

            sum += order.getOrderPrice();
            count++;
        }

        return new OrderAnalysisDto(count, sum);
    }

    public static OrderAnalysisDto filterOrdersInTimespan(
            Map<Integer, Order> orderMap,
            Date startDate,
            Date endDate
    ) {
        double sum = 0;
        int count = 0;

        for (Order order : orderMap.values()) {
            Date orderDate = order.getOrderDate();

            if (startDate != null && orderDate.before(startDate)) {
                continue;
            }
            if (endDate != null && orderDate.after(endDate)) {
                continue;
            }

            sum += order.getOrderPrice();
            count++;
        }

        return new OrderAnalysisDto(count, sum);
    }
}
