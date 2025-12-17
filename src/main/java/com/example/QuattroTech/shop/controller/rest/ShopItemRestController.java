package com.example.QuattroTech.shop.controller.rest;

import com.example.QuattroTech.shop.model.ShopItem;
import com.example.QuattroTech.shop.service.ShopItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ShopItemRestController {

    private final ShopItemService shopItemService;

    public ShopItemRestController(ShopItemService shopItemService) {
        this.shopItemService = shopItemService;
    }

    /**
     * GET /api/items - Get all items
     */
    @GetMapping
    public List<ShopItem> getAllItems() {
        return shopItemService.getAllItems();
    }

    /**
     * GET /api/items/{id} - Get item by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ShopItem> getItemById(@PathVariable String id) {
        return shopItemService.getItemById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/items - Create new item
     */
    @PostMapping
    public ResponseEntity<ShopItem> createItem(@Valid @RequestBody ShopItem item) {
        ShopItem created = shopItemService.insertNewItem(item);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * PUT /api/items/{id} - Update existing item
     */
    @PutMapping("/{id}")
    public ResponseEntity<ShopItem> updateItem(
            @PathVariable String id,
            @Valid @RequestBody ShopItem item) {
        
        if (!shopItemService.getItemById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        ShopItem updated = shopItemService.updateItem(id, item);
        return ResponseEntity.ok(updated);
    }

    /**
     * DELETE /api/items/{id} - Delete item
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable String id) {
        if (!shopItemService.getItemById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        shopItemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/items/search?name={name} - Search items by name
     */
    @GetMapping("/search")
    public List<ShopItem> searchByName(@RequestParam String name) {
        return shopItemService.searchByName(name);
    }

    /**
     * GET /api/items/low-stock?threshold={threshold} - Get low stock items
     */
    @GetMapping("/low-stock")
    public List<ShopItem> getLowStockItems(@RequestParam(defaultValue = "10") Integer threshold) {
        return shopItemService.findLowStockItems(threshold);
    }
}
