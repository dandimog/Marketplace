package com.ncgroup.marketplaceserver.goods.repository;

import com.ncgroup.marketplaceserver.goods.model.Good;
import com.ncgroup.marketplaceserver.goods.model.dto.GoodDto;

import java.util.List;
import java.util.Optional;

public interface GoodsRepository {
    Good create(GoodDto good);
    List<Good> showAll();
    List<Good> findByName(String name);
    List<Good> filterByGoodCategory(long categoryId);
    List<Good> filterByPrice(int downLimit, int upLimit);
    Good edit(Good good);
    Optional<Good> findById(long id);
}
