package com.ncgroup.marketplaceserver.goods.service;

import com.ncgroup.marketplaceserver.goods.exceptions.GoodAlreadyExistsException;
import com.ncgroup.marketplaceserver.goods.model.Good;
import com.ncgroup.marketplaceserver.goods.model.GoodDto;
import com.ncgroup.marketplaceserver.goods.repository.GoodsRepository;
import com.ncgroup.marketplaceserver.shopping.cart.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GoodsServiceImpl implements GoodsService {

    static final Integer PAGE_CAPACITY = 10;

    private GoodsRepository repository;

    @Autowired
    public GoodsServiceImpl(GoodsRepository repository) {
        this.repository = repository;
    }

    @Override
    public Good create(GoodDto goodDto) throws GoodAlreadyExistsException {
        Long goodId = repository.createGood(goodDto); // get the id of new good if it is new
        Good good = new Good();
        good.setProperties(goodDto, goodId);
        return good;
    }

    @Override
    public Good edit(GoodDto goodDto, long id) throws NotFoundException {
        Good good = this.findById(id); // pull the good object if exists
        good.setProperties(goodDto, id);
        repository.editGood(goodDto, id); // push the changed good object
        return good;
    }

    @Override
    public Good findById(long id) throws NotFoundException {
        Optional<Good> goodOptional = repository.findById(id);
        return goodOptional.orElseThrow(() ->
                new NotFoundException("Product with " + id + " not found."));
    }

    @Override
    public List<Good> display(Optional<String> name, Optional<String> category,
                              Optional<String> minPrice, Optional<String> maxPrice,
                              Optional<String> sortBy, Optional<String> sortDirection,
                              Optional<Integer> page) {

        int counter = 0;
        List<String> concatenator = new ArrayList<>();

        String flexibleQuery = "SELECT goods.id, product.name AS product_name, " +
                "firm.name AS firm_name, category.name AS category_name," +
                " goods.quantity, goods.price, goods.discount, goods.in_stock," +
                " goods.description FROM goods INNER JOIN " +
                "product ON goods.prod_id = product.id " +
                "INNER JOIN firm ON goods.firm_id = firm.id " +
                "INNER JOIN category ON category.id = product.category_id";

        // Sort can be by: price, product.name, discount.

        /**
         * 4 ways to filter goods
         */

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

        /**
         * constructing a query based on the filters we got
         */

        if (counter > 0) {
            flexibleQuery += " WHERE" + concatenator.get(0);
            for (int i = 1; i < counter; i++) {
                flexibleQuery += " AND" + concatenator.get(i);
            }
        }

        /**
         * give our query an ordering
         */

        if (sortBy.isPresent()) {
            flexibleQuery += " ORDER BY " + sortBy.get();
        } else {
            flexibleQuery += " ORDER BY product.name";
        }

        if (sortDirection.isPresent()) {
            flexibleQuery += " " + sortDirection.get().toUpperCase();
        } else {
            flexibleQuery += " DESC";
        }

        /**
         * pagination
         */

        if (page.isPresent()) {
            flexibleQuery += " LIMIT " + PAGE_CAPACITY + " OFFSET " + (page.get() - 1) * PAGE_CAPACITY;
        } else {
            flexibleQuery += " LIMIT " + PAGE_CAPACITY;
        }

        List<Good> res = repository.display(flexibleQuery);
        int numOfPages = countPages(res.size());
        return res;
    }

    public int countPages(int numOfGoods) {
        if (numOfGoods % PAGE_CAPACITY == 0) {
            return numOfGoods / PAGE_CAPACITY;
        }
        return (numOfGoods / PAGE_CAPACITY) + 1;
    }
}
