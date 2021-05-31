package com.ncgroup.marketplaceserver.goods.service;

import com.ncgroup.marketplaceserver.goods.model.Good;
import com.ncgroup.marketplaceserver.goods.model.dto.GoodDto;
import com.ncgroup.marketplaceserver.goods.repository.GoodsRepository;
import com.ncgroup.marketplaceserver.shopping.cart.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class GoodsServiceImpl implements GoodsService {

    static final Integer PAGE_CAPACITY = 18;

    private GoodsRepository repository;

    @Autowired
    public GoodsServiceImpl(GoodsRepository repository) {
        this.repository = repository;
    }

    @Override
    public Good create(GoodDto goodDto) {

        return repository.create(goodDto);
    }

    @Override
    public Good edit(GoodDto goodDto, long id) throws NotFoundException {
        Good good = this.findById(id);
        goodDto.mapTo(good);
        good.setId(id);
        repository.edit(good);
        return good;
    }

    @Override
    public Good findById(long id) throws NotFoundException {
        Optional<Good> goodOptional = repository.findById(id);
        return goodOptional.orElseThrow(() ->
                new NotFoundException("Product with " + id +" not found."));
    }


//    @Override
//    public List<Good> findAll() {
//        return repository.findAll();
//    }

//    @Override
//    public List<Good> display(Optional<String> filter, Optional<String> category,
//                          Optional<String> minPrice, Optional<String> maxPrice,
//                          Optional<String> sortBy, Optional<String> sortDirection,
//                          Optional<Integer> page) {
//        return repository.display(filter, category, minPrice, maxPrice, sortBy,
//                sortDirection, page);
//    }


    public int pageCount(List<Good> listOfGoods) {
        if (listOfGoods.size() % PAGE_CAPACITY == 0) {
            return listOfGoods.size() / PAGE_CAPACITY;
        }
        return (listOfGoods.size() / PAGE_CAPACITY) + 1;
    }
}
