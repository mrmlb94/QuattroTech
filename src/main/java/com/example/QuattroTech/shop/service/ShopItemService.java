package com.example.QuattroTech.shop.service;

import com.example.QuattroTech.shop.model.ShopItem;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ShopItemService {

    public List<ShopItem> getAllItems() {
        // Temporary implementation for REST tests
        return Collections.emptyList();
    }
}
