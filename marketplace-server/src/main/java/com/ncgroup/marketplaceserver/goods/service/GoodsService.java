package com.ncgroup.marketplaceserver.goods.service;

import com.ncgroup.marketplaceserver.goods.model.Good;

public interface GoodsService {
    Good create();
    Good edit();
    Good addDiscount();
    Good editDiscount();
    Good deactivate();
    Good read();
}
