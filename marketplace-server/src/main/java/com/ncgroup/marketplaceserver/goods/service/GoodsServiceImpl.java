package com.ncgroup.marketplaceserver.goods.service;

import com.ncgroup.marketplaceserver.goods.model.Good;
import com.ncgroup.marketplaceserver.goods.repository.GoodsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class GoodsServiceImpl implements GoodsService {

    private GoodsRepository repository;

    @Autowired
    public GoodsServiceImpl(GoodsRepository repository) {
        this.repository = repository;
    }

    @Override
    public Good create() {
        return null;
    }

    @Override
    public Good edit() {
        return null;
    }

    @Override
    public Good addDiscount() {
        return null;
    }

    @Override
    public Good editDiscount() {
        return null;
    }

    @Override
    public Good deactivate() {
        return null;
    }

    @Override
    public Collection<Good> readAll() {
        return repository.showAll();
    }
}
