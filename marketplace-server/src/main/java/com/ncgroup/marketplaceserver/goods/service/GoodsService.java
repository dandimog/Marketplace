package com.ncgroup.marketplaceserver.goods.service;

import com.ncgroup.marketplaceserver.goods.model.Good;
import com.ncgroup.marketplaceserver.goods.model.dto.GoodDto;
import com.ncgroup.marketplaceserver.shopping.cart.exceptions.NotFoundException;

import java.util.List;
import java.util.Optional;

public interface GoodsService {
    Good create(GoodDto goodDto);
    Good edit(GoodDto goodDto, long id) throws NotFoundException;
    Good read(long id) throws NotFoundException;
//    Good addDiscount();
//    Good editDiscount();
//    Good deactivate();
    List<Good> findAll();

    List<Good> display(Optional<String> filter, Optional<String> category,
                   Optional<String> minPrice, Optional<String> maxPrice,
                   Optional<String> sortBy, Optional<String> sortDirection,
                   Optional<Integer> page);
}
