package bs7projekt.src.dtos;

import bs7projekt.src.models.Customer;

public class CustomerRevenueDto {

    public Customer customer;
    public double salesVolume;

    public CustomerRevenueDto(Customer customer, double salesVolume) {
        this.customer = customer;
        this.salesVolume = salesVolume;
    }

}
