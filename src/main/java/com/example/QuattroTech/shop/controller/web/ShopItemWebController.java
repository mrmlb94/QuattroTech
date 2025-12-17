package com.example.QuattroTech.shop.controller.web;

import com.example.QuattroTech.shop.model.ShopItem;
import com.example.QuattroTech.shop.service.ShopItemService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

/**
 * Web controller for ShopItem.
 * Provides Thymeleaf HTML views for CRUD operations.
 */
@Controller
@RequestMapping("/items")
public class ShopItemWebController {

    private final ShopItemService shopItemService;

    public ShopItemWebController(ShopItemService shopItemService) {
        this.shopItemService = shopItemService;
    }

    /**
     * GET /items - Show list of all items
     */
    @GetMapping
    public String listItems(Model model) {
        model.addAttribute("items", shopItemService.getAllItems());
        model.addAttribute("message", 
            shopItemService.getAllItems().isEmpty() ? "No items available" : "");
        return "items/list";
    }

    /**
     * GET /items/new - Show form for creating new item
     */
    @GetMapping("/new")
    public String newItemForm(Model model) {
        model.addAttribute("item", new ShopItem());
        model.addAttribute("formTitle", "Add New Item");
        model.addAttribute("isNew", true);
        return "items/form";
    }

    /**
     * GET /items/edit/{id} - Show form for editing existing item
     */
    @GetMapping("/edit/{id}")
    public String editItemForm(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        return shopItemService.getItemById(id)
                .map(item -> {
                    model.addAttribute("item", item);
                    model.addAttribute("formTitle", "Edit Item");
                    model.addAttribute("isNew", false);
                    return "items/form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Item not found with id: " + id);
                    return "redirect:/items";
                });
    }

    /**
     * GET /items/{id} - Show item details
     */
    @GetMapping("/{id}")
    public String viewItem(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        return shopItemService.getItemById(id)
                .map(item -> {
                    model.addAttribute("item", item);
                    return "items/detail";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Item not found with id: " + id);
                    return "redirect:/items";
                });
    }

    /**
     * POST /items/save - Save new or updated item
     */
    @PostMapping("/save")
    public String saveItem(
            @Valid @ModelAttribute("item") ShopItem item,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("formTitle", item.getId() == null ? "Add New Item" : "Edit Item");
            model.addAttribute("isNew", item.getId() == null);
            return "items/form";
        }

        try {
            if (item.getId() == null || item.getId().isEmpty()) {
                shopItemService.insertNewItem(item);
                redirectAttributes.addFlashAttribute("success", "Item created successfully!");
            } else {
                shopItemService.updateItem(item.getId(), item);
                redirectAttributes.addFlashAttribute("success", "Item updated successfully!");
            }
            return "redirect:/items";
        } catch (Exception e) {
            model.addAttribute("error", "Error saving item: " + e.getMessage());
            model.addAttribute("formTitle", item.getId() == null ? "Add New Item" : "Edit Item");
            model.addAttribute("isNew", item.getId() == null);
            return "items/form";
        }
    }

    /**
     * GET /items/delete/{id} - Delete item
     */
    @GetMapping("/delete/{id}")
    public String deleteItem(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            if (shopItemService.getItemById(id).isPresent()) {
                shopItemService.deleteItem(id);
                redirectAttributes.addFlashAttribute("success", "Item deleted successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Item not found with id: " + id);
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting item: " + e.getMessage());
        }
        return "redirect:/items";
    }
}
