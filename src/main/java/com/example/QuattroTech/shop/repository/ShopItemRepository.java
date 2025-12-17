package com.example.QuattroTech.shop.repository;

import com.example.QuattroTech.shop.model.ShopItem;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopItemRepository extends MongoRepository<ShopItem, String> {


    ShopItem findByName(String name);
    List<ShopItem> findByNameContainingIgnoreCase(String namePart);
    List<ShopItem> findByPriceLessThanEqual(BigDecimal maxPrice);
    List<ShopItem> findByQuantityGreaterThanOrderByPrice(Integer threshold);
    
    @Query("{ 'quantity': { $lt: ?0 } }")
    List<ShopItem> findItemsWithLowStock(Integer threshold);
}





