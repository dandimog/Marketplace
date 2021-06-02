package com.ncgroup.marketplaceserver.goods.service;

import com.ncgroup.marketplaceserver.goods.exceptions.GoodAlreadyExistsException;
import com.ncgroup.marketplaceserver.goods.model.Good;
import com.ncgroup.marketplaceserver.goods.model.GoodDto;
import com.ncgroup.marketplaceserver.goods.repository.GoodsRepository;
import com.ncgroup.marketplaceserver.shopping.cart.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Consumer;


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

    public List<Good> display(Optional<String> name, Optional<String> category,
                          Optional<String> minPrice, Optional<String> maxPrice,
                          Optional<String> sortBy, Optional<String> sortDirection,
                          Optional<Integer> page) {

        int numOfPages = countPages(repository.countGoods());
        int counter = 0;
        List<String> concatenator = new ArrayList<>();
        String query = "SELECT goods.id, product.name AS product_name, " +
                "firm.name AS firm_name, category.name AS category_name, goods.quantity, " +
                "goods.price, goods.discount, goods.in_stock, goods.description FROM goods INNER JOIN " +
                "product ON goods.prod_id = product.id INNER JOIN firm ON goods.firm_id = firm.id " +
                "INNER JOIN category ON category.id = product.category_id";

        //sort by: price, product.name, discount

        if (name.isPresent()) {
            concatenator.add(" product.name LIKE '%" + name.get() + "%'");
            counter++;
        }

        if (category.isPresent()) {
            concatenator.add(" category.name = " + "'" + category.get() + "'");
            counter++;
        }

        if (minPrice.isPresent()) {
            concatenator.add(" price >= " + minPrice.get());
            counter++;
        }

        if (maxPrice.isPresent()) {
            concatenator.add(" price <= " + maxPrice.get());
            counter++;
        }

        if (counter > 0) {
            query += " WHERE" + concatenator.get(0);
            for (int i = 1; i < counter; i++) {
                query += " AND" + concatenator.get(i);
            }
        }

        if (sortBy.isPresent()) {
           query += " ORDER BY " + sortBy.get();
        } else {
            query += " ORDER BY product.name";
        }

        if (sortDirection.isPresent()) {
            concatenator.add(" " + sortDirection.get().toUpperCase());
        } else {
            concatenator.add(" DESC");
        }

        return repository.display(query);
    }


    public int countPages(int numOfGoods) {
        if (numOfGoods % PAGE_CAPACITY == 0) {
            return numOfGoods / PAGE_CAPACITY;
        }
        return (numOfGoods / PAGE_CAPACITY) + 1;
    }
}
