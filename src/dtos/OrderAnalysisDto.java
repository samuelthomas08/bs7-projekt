package bs7projekt.src.dtos;

public class OrderAnalysisDto {

    public Integer totalOrders;
    public Double totalOrderSum;

    public OrderAnalysisDto(Integer totalOrders, Double totalOrderSum) {
        this.totalOrders = totalOrders;
        this.totalOrderSum = totalOrderSum;
    }
}
