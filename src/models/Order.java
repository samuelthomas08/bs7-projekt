package bs7projekt.src.models;

import bs7projekt.src.dtos.CustomerRevenueDto;
import bs7projekt.src.dtos.OrderAnalysisDto;

import java.time.DayOfWeek;
import java.time.Month;
import java.time.Year;
import java.util.*;

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

    /**
     * Filters orders by any combination of customer, postal code, exact date,
     * week, weekday, month, and year, and aggregates the count and total revenue
     * of the matching orders. All filter parameters are optional (nullable);
     * any that are {@code null} are ignored.
     *
     * @param orderMap   the map of all orders to evaluate
     * @param customer   only include orders from this customer, or {@code null} to ignore
     * @param postalCode only include orders with this postal code, or {@code null} to ignore
     * @param date       only include orders on this exact date, or {@code null} to ignore
     * @param week       only include orders in this ISO week of the year, or {@code null} to ignore
     * @param day        only include orders on this weekday, or {@code null} to ignore
     * @param month      only include orders in this month, or {@code null} to ignore
     * @param year       only include orders in this year, or {@code null} to ignore
     * @return an {@link OrderAnalysisDto} containing the count and total revenue
     *         of the matching orders
     */
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

    /**
     * Aggregates the count and total revenue of all orders whose date falls
     * within the given timespan. Either bound may be {@code null}, in which
     * case that side of the timespan is left unrestricted.
     *
     * @param orderMap  the map of all orders to evaluate
     * @param startDate only include orders on or after this date, or {@code null} for no lower bound
     * @param endDate   only include orders on or before this date, or {@code null} for no upper bound
     * @return an {@link OrderAnalysisDto} containing the count and total revenue
     *         of the matching orders
     */
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

    /**
     * Finds the customers with the highest revenue generated within a fixed
     * window of days after their {@code customerSince} date (e.g. their first
     * three weeks). Customers without a {@code customerSince} date are skipped.
     *
     * @param orderMap     the map of all orders to evaluate
     * @param windowInDays the size of the window in days from each customer's {@code customerSince} date
     * @param rowsLimit    the maximum number of top customers to return
     * @return a list of {@link CustomerRevenueDto}, sorted descending by revenue,
     *         containing at most {@code rowsLimit} entries
     */
    public static List<CustomerRevenueDto> getTopCustomersByEarlyRevenue(
            Map<Integer, Order> orderMap,
            int windowInDays,
            int rowsLimit
    ) {
        Map<Customer, Double> earlyRevenue = new HashMap<>();

        for (Order order : orderMap.values()) {
            Customer customer = order.getCustomer();
            Date customerSince = customer.getCustomerSince();
            Date orderDate = order.getOrderDate();

            if (customerSince == null) {
                continue;
            }

            Calendar windowEnd = Calendar.getInstance();
            windowEnd.setTime(customerSince);
            windowEnd.add(Calendar.DAY_OF_YEAR, windowInDays);

            boolean withinWindow =
                    !orderDate.before(customerSince) &&
                            orderDate.before(windowEnd.getTime());

            if (!withinWindow) {
                continue;
            }

            double currentRevenue = earlyRevenue.getOrDefault(customer, 0.0);
            earlyRevenue.put(customer, currentRevenue + order.getOrderPrice());
        }

        List<CustomerRevenueDto> result = new ArrayList<>();
        for (Map.Entry<Customer, Double> entry : earlyRevenue.entrySet()) {
            result.add(new CustomerRevenueDto(entry.getKey(), entry.getValue()));
        }

        result.sort((a, b) -> Double.compare(b.salesVolume, a.salesVolume));

        return result.subList(0, Math.min(rowsLimit, result.size()));
    }

}