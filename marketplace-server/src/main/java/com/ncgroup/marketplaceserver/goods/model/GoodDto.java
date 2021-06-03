package com.ncgroup.marketplaceserver.goods.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GoodDto {
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
}
