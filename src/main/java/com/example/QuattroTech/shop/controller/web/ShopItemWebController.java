package com.example.QuattroTech.shop.controller.web;

import com.example.QuattroTech.shop.model.ShopItem;
import com.example.QuattroTech.shop.service.ShopItemService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ShopItemWebController {

    private final ShopItemService shopItemService;

    public ShopItemWebController(ShopItemService shopItemService) {
        this.shopItemService = shopItemService;
    }

    @GetMapping("/shop/items")
    public String showItems(Model model) {
        List<ShopItem> items = shopItemService.getAllItems();
        model.addAttribute("items", items);
        return "items";
    }
}
