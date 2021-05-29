package com.ncgroup.marketplaceserver.goods.controller;


import com.ncgroup.marketplaceserver.goods.model.Good;
import com.ncgroup.marketplaceserver.goods.model.dto.GoodDto;
import com.ncgroup.marketplaceserver.goods.service.GoodsService;
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
//    @GetMapping("/")
//    public ResponseEntity<List<Good>> readAll() {
//        return new ResponseEntity<>(service.readAll(), HttpStatus.OK);
//    }

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
            @RequestBody GoodDto goodDto, @PathVariable("id") long id) {
        return new ResponseEntity<>(service.edit(goodDto, id), HttpStatus.OK);
    }

    /**
     * displaying products with respect to filter, sorting and page number if they are given,
     * otherwise just show the first page of all products (unsorted)
     */
    @GetMapping("/")
    public ResponseEntity<List<Good>> display(
            @RequestParam("filterCategory")
                    Optional<String> filterCategory,
            @RequestParam("sortBy")
                    Optional<String> sortBy,
            @RequestParam("sortDirection") // ASC or DESC
                    Optional<String> sortDirection,
            @RequestParam("pageNumber")
                    Optional<Integer> pageNumber) {
        return new ResponseEntity<>(
                service.display
                        (filterCategory, sortBy, sortDirection, pageNumber), HttpStatus.OK);
    }
}
