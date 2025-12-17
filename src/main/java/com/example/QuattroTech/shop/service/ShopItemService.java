package com.example.QuattroTech.shop.service;

import com.example.QuattroTech.shop.model.ShopItem;
import java.util.List;
import java.util.Optional;

public interface ShopItemService {
    List<ShopItem> getAllItems();
    Optional<ShopItem> getItemById(String id);
    ShopItem insertNewItem(ShopItem item);
    ShopItem updateItem(String id, ShopItem item);
    void deleteItem(String id);
    List<ShopItem> searchByName(String namePart);
    List<ShopItem> findLowStockItems(Integer threshold);
}
