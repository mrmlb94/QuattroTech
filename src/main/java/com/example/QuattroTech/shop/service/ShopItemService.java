package com.example.QuattroTech.shop.service;

import com.example.QuattroTech.shop.model.ShopItem;
import com.example.QuattroTech.shop.repository.ShopItemRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ShopItemService {

    private final ShopItemRepository shopItemRepository;

    public ShopItemService(ShopItemRepository shopItemRepository) {
        this.shopItemRepository = shopItemRepository;
    }

    public List<ShopItem> getAllItems() {
        return shopItemRepository.findAll();
    }

    public ShopItem getItemById(String id) {
        return shopItemRepository.findById(id).orElse(null);
    }

    public ShopItem insertNewItem(ShopItem item) {
        item.setId(null);
        validateItem(item);
        return shopItemRepository.save(item);
    }

    public ShopItem updateItemById(String id, ShopItem replacement) {
        replacement.setId(id);
        validateItem(replacement);
        return shopItemRepository.save(replacement);
    }

    private void validateItem(ShopItem item) {
        if (item.getName() == null || item.getName().isBlank()) {
            throw new IllegalArgumentException("Name must not be blank");
        }
        if (item.getPrice() == null || item.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price must be >= 0");
        }
        if (item.getQuantity() < 0) {
            throw new IllegalArgumentException("Quantity must be >= 0");
        }
    }
}
