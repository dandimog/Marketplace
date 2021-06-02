package com.ncgroup.marketplaceserver.goods.repository;

import com.ncgroup.marketplaceserver.goods.exceptions.GoodAlreadyExistsException;
import com.ncgroup.marketplaceserver.goods.model.Good;
import com.ncgroup.marketplaceserver.goods.model.GoodDto;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface GoodsRepository {
    Long create(GoodDto goodDto) throws GoodAlreadyExistsException;

//    List<Good> findByName(String name);
//    List<Good> filterByGoodCategory(long categoryId);
//    List<Good> filterByPrice(int downLimit, int upLimit);

    void edit(Good good);
    Optional<Good> findById(long id);

    Collection<Good> getAllGoods();

    List<Good> display(String query);

    Integer countGoods();
}
