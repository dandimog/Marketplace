package com.ncgroup.marketplaceserver.goods.controller;


import com.ncgroup.marketplaceserver.goods.model.Good;
import com.ncgroup.marketplaceserver.goods.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

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
    @GetMapping("/")
    public ResponseEntity<Collection<Good>> readAll() {
        return new ResponseEntity<>(service.readAll(), HttpStatus.OK);
    }


}
