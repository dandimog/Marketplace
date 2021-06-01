package com.ncgroup.marketplaceserver.goods.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Good {
    private long id;
    private String goodName;
    private String firmName;
    private int quantity;
    private double price;
    //private Unit unit;
    private double discount;
    //private LocalDateTime shippingDate;
    private boolean inStock;
    private String description;
    private String categoryName;
    //private String status;

    public void setPrice(double price, double discount) {
        this.price = price - (price * (discount / 100));
    }
}
