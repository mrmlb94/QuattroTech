package com.example.QuattroTech.shop.repository;

import com.example.QuattroTech.shop.model.ShopItem;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ShopItemRepository {

    private static final String TEMPORARY_IMPLEMENTATION = "Temporary implementation";

    public List<ShopItem> findAll() {
        throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
    }

    public Optional<ShopItem> findById(String id) {
        throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
    }

    public ShopItem save(ShopItem shopItem) {
        throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
    }
}
