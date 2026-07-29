package bs7projekt.src.dtos;

import java.math.BigDecimal;

public record OrderAnalysisDto(
        Integer totalOrders,
        BigDecimal totalOrderSum
) {
}
