package com.ncgroup.marketplaceserver.goods.service;

import com.ncgroup.marketplaceserver.goods.exceptions.GoodAlreadyExistsException;
import com.ncgroup.marketplaceserver.goods.model.Good;
import com.ncgroup.marketplaceserver.goods.model.GoodDto;
import com.ncgroup.marketplaceserver.shopping.cart.exceptions.NotFoundException;

import java.util.Collection;

public interface GoodsService {
    Good create(GoodDto goodDto) throws GoodAlreadyExistsException;
    Good edit(GoodDto goodDto, long id) throws NotFoundException;
    Good findById(long id) throws NotFoundException;

    Collection<Good> getAll();
//    Good addDiscount();
//    Good editDiscount();
//    Good deactivate();

//    List<Good> display(Optional<String> filter, Optional<String> category,
//                   Optional<String> minPrice, Optional<String> maxPrice,
//                   Optional<String> sortBy, Optional<String> sortDirection,
//                   Optional<Integer> page);
}
