package com.example.QuattroTech.shop.controller.rest;

import com.example.QuattroTech.shop.model.ShopItem;
import com.example.QuattroTech.shop.service.ShopItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ShopItemRestController {

    private final ShopItemService shopItemService;

    public ShopItemRestController(ShopItemService shopItemService) {
        this.shopItemService = shopItemService;
    }

    @GetMapping
    public List<ShopItem> getAllItems() {
        return shopItemService.getAllItems();
    }

}
