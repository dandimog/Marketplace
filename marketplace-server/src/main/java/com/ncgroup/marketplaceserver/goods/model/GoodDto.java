package com.ncgroup.marketplaceserver.goods.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class GoodDto {
    private String goodName;
    private String firmName;
    private int quantity;
    private double price;
    //private Unit unit;
    private double discount;

    /**
     * 03.04.2021
     * 03-04-2021
     * 03/04/2021
     */

    //private LocalDate shippingDate;

    private boolean inStock;
    private String description;
    private String categoryName;
    //private String status;
}
