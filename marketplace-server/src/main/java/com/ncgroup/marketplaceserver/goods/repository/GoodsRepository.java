package com.ncgroup.marketplaceserver.goods.repository;

import com.ncgroup.marketplaceserver.goods.exceptions.GoodAlreadyExistsException;
import com.ncgroup.marketplaceserver.goods.model.Good;
import com.ncgroup.marketplaceserver.goods.model.GoodDto;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface GoodsRepository {
    Long create(GoodDto goodDto) throws GoodAlreadyExistsException;

    void edit(GoodDto good, Long id);
    Optional<Good> findById(long id);

    List<Good> display(String query);
}
