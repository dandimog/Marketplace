package com.ncgroup.marketplaceserver.goods.repository;

import com.ncgroup.marketplaceserver.goods.model.Good;
import com.ncgroup.marketplaceserver.goods.model.dto.GoodDto;

import java.util.List;
import java.util.Optional;

public interface GoodsRepository {
    Good create(GoodDto good);

//    List<Good> findByName(String name);
//    List<Good> filterByGoodCategory(long categoryId);
//    List<Good> filterByPrice(int downLimit, int upLimit);

    void edit(Good good);
    Optional<Good> findById(long id);

//    List<Good> display(Optional<String> filter, Optional<String> category,
//                   Optional<String> minPrice, Optional<String> maxPrice,
//                   Optional<String> sortBy, Optional<String> sortDirection,
//                   Optional<Integer> page);

}
