package com.example.QuattroTech.shop.repository;

import com.example.QuattroTech.shop.model.ShopItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopItemRepository extends MongoRepository<ShopItem, String> {
}
