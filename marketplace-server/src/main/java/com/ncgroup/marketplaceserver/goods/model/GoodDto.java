package com.ncgroup.marketplaceserver.goods.model;

import com.ncgroup.marketplaceserver.goods.model.Good;
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


    public void mapTo(Good good) {
        good.setGoodName(goodName);
        good.setFirmName(firmName);
        good.setQuantity(quantity);
        good.setPrice(price, discount);
        good.setDiscount(discount);
        good.setInStock(inStock);
        good.setDescription(description);
        good.setCategoryName(categoryName);
    }
}
