package com.ncgroup.marketplaceserver.goods.service;

import com.ncgroup.marketplaceserver.goods.model.Good;
import com.ncgroup.marketplaceserver.goods.model.dto.GoodDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface GoodsService {
    Good create(GoodDto goodDto);
    Good edit(GoodDto goodDto, long id);
    Good read(long id);
//    Good addDiscount();
//    Good editDiscount();
//    Good deactivate();
    List<Good> readAll();
    List<Good> display(Optional<String> filterCategory, Optional<String>  sortBy,
                       Optional<String>  sortDirection, Optional<Integer>  pageNumber);
}
