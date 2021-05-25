package com.ncgroup.marketplaceserver.goods.service;

import com.ncgroup.marketplaceserver.goods.model.Good;

import java.util.Collection;

public interface GoodsService {
    Good create();
    Good edit();
    Good addDiscount();
    Good editDiscount();
    Good deactivate();
    Collection<Good> readAll();
}
