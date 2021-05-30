package com.ncgroup.marketplaceserver.goods.controller;


import com.ncgroup.marketplaceserver.goods.model.Good;
import com.ncgroup.marketplaceserver.goods.model.dto.GoodDto;
import com.ncgroup.marketplaceserver.goods.service.GoodsService;
import com.ncgroup.marketplaceserver.shopping.cart.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class GoodsController {

    private GoodsService service;

    @Autowired
    public GoodsController(GoodsService service) {
        this.service = service;
    }

    /**
     * show the list of all products created
     */

    /**
     * create a product and return it just in case we need id/creationTime
     * on the client side
     */
    @PostMapping
    public ResponseEntity<Good> createProduct(@RequestBody GoodDto goodDto) {
        return new ResponseEntity<>(service.create(goodDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Good> editProduct(
            @RequestBody GoodDto goodDto, @PathVariable("id") long id)
            throws NotFoundException {
        return new ResponseEntity<>(service.edit(goodDto, id), HttpStatus.OK);
    }

    /**
     * displaying products with respect to filter, sorting and page number if they are given,
     * otherwise just show the first page of all products (unsorted)
     */
    @GetMapping("/")
    public ResponseEntity<List<Good>> display(
            @RequestParam("name")
                    Optional<String> filter,
            @RequestParam("category")
                    Optional<String> category,
            @RequestParam("minPrice")
                    Optional<String> minPrice,
            @RequestParam("maxPrice")
                    Optional<String> maxPrice,
            @RequestParam("sort")
                    Optional<String> sortBy,
            @RequestParam("sortDirection") // ASC or DESC
                    Optional<String> sortDirection,
            @RequestParam("page")
                    Optional<Integer> page) {
        return new ResponseEntity<>(
                service.display
                        (filter, category,
                                minPrice, maxPrice, sortBy,
                                sortDirection, page), HttpStatus.OK);
    }
}
