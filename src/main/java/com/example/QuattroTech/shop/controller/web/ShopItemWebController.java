package com.example.QuattroTech.shop.controller.web;

import com.example.QuattroTech.shop.model.ShopItem;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collections;
import java.util.List;

@Controller
public class ShopItemWebController {

    @GetMapping("/shop/items")
    public String showItems(Model model) {
        List<ShopItem> items = Collections.emptyList();
        model.addAttribute("items", items);
        return "items";
    }
}
