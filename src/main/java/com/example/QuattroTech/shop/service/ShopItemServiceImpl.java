package com.example.QuattroTech.shop.service;

import com.example.QuattroTech.shop.model.ShopItem;
import com.example.QuattroTech.shop.repository.ShopItemRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ShopItemServiceImpl implements ShopItemService {
    
    private final ShopItemRepository repository;

    public ShopItemServiceImpl(ShopItemRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<ShopItem> getAllItems() {
        return repository.findAll();
    }

    @Override
    public Optional<ShopItem> getItemById(String id) {
        return repository.findById(id);
    }

    @Override
    public ShopItem insertNewItem(ShopItem item) {
        // Force ID to null to ensure new insert
        item.setId(null);
        // Validate before saving
        validateItem(item);
        return repository.save(item);
    }

    @Override
    public ShopItem updateItem(String id, ShopItem item) {
        // Force ID from parameter to ensure correct update
        item.setId(id);
        // Validate before saving
        validateItem(item);
        return repository.save(item);
    }

    @Override
    public void deleteItem(String id) {
        repository.deleteById(id);
    }

    @Override
    public List<ShopItem> searchByName(String namePart) {
        return repository.findByNameContainingIgnoreCase(namePart);
    }

    @Override
    public List<ShopItem> findLowStockItems(Integer threshold) {
        return repository.findItemsWithLowStock(threshold);
    }

    // Private validation method (keep from QuattroTech)
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
