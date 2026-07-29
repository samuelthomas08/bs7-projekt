package bs7projekt.src.dtos;

import bs7projekt.src.models.Customer;
import java.math.BigDecimal;

public record CustomerRevenueDto(
        Customer customer,
        BigDecimal salesVolume
) {
}
