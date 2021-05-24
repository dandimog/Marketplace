package com.ncgroup.marketplaceserver.goods.repository;

import com.ncgroup.marketplaceserver.goods.model.Good;

import java.util.Collection;
import java.util.Optional;

public interface GoodsRepository {
    Good create(Good good);
    Collection<Good> findByName(String name);
    Collection<Good> filterByGoodCategory(long categoryId);
    Collection<Good> filterByPrice(int downLimit, int upLimit);
    Good edit(Good good);
}
