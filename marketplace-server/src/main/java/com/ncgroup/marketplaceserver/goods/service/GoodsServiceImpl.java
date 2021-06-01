package com.ncgroup.marketplaceserver.goods.service;

import com.ncgroup.marketplaceserver.goods.exceptions.GoodAlreadyExistsException;
import com.ncgroup.marketplaceserver.goods.model.Good;
import com.ncgroup.marketplaceserver.goods.model.GoodDto;
import com.ncgroup.marketplaceserver.goods.repository.GoodsRepository;
import com.ncgroup.marketplaceserver.shopping.cart.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


@Service
public class GoodsServiceImpl implements GoodsService {

    static final Integer PAGE_CAPACITY = 10;

    private GoodsRepository repository;

    @Autowired
    public GoodsServiceImpl(GoodsRepository repository) {
        this.repository = repository;
    }


    public Collection<Good> getAll() {
        return repository.getAllGoods();
    }

    @Override
    public Good create(GoodDto goodDto) throws GoodAlreadyExistsException {
        Long goodId = repository.create(goodDto); // get the id of new good if it is new
        Good good = new Good();
        goodDto.mapTo(good);
        good.setId(goodId);
        return good;
    }

    @Override
    public Good edit(GoodDto goodDto, long id) throws NotFoundException {
        Good good = this.findById(id); // pull the good object if exists
        goodDto.mapTo(good); // make changes to the good object
        good.setId(id); // set id
        repository.edit(good); // push the changed good object
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
