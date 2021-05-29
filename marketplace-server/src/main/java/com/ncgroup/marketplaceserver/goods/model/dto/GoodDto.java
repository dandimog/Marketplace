package com.ncgroup.marketplaceserver.goods.model.dto;

import com.ncgroup.marketplaceserver.goods.model.Good;
import com.ncgroup.marketplaceserver.goods.model.Unit;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class GoodDto {
    private String goodName;
    private String firmName;
    private int quantity;
    private double price;
    //private Unit unit;
    private byte discount;
    private LocalDateTime shippingDate;
    private boolean inStock;
    //private String imageUrl;
    private String description;
    private String categoryName;
    //private String status;
    //public double discountPrice();

    public Good convertToGood() {
        return Good.builder()
                .goodName(this.getGoodName())
                .firmName(this.getFirmName())
                .quantity(this.getQuantity())
                .price(this.getPrice())
                //.unit(this.getUnit())
                .discount(this.getDiscount())
                //.shippingDate(this.getShippingDate())
                .inStock(this.isInStock())
                .description(this.getDescription())
                .categoryName(this.getCategoryName())
                .build();
    }
}
