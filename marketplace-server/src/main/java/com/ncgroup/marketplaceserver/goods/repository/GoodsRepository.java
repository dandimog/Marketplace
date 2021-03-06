package com.ncgroup.marketplaceserver.goods.repository;

import com.ncgroup.marketplaceserver.goods.exceptions.GoodAlreadyExistsException;
import com.ncgroup.marketplaceserver.goods.model.Good;
import com.ncgroup.marketplaceserver.goods.model.GoodDto;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface GoodsRepository {
    Long createGood(GoodDto goodDto) throws GoodAlreadyExistsException;
    void editGood(GoodDto good, Long id);
    Optional<Good> findById(long id);
    List<Good> display(String query);
    int countGoods(String query);
    List<String> getCategories();
    Double getMaxPrice(String category);
    Double getMinPrice(String category);
    Double getTotalMaxPrice();
    Double getTotalMinPrice();
    List<String> getFirms();


    void editQuantity(long id, int quantity);
}
