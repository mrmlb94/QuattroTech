package com.example.QuattroTech.shop.controller.web;

import com.example.QuattroTech.shop.model.ShopItem;
import com.example.QuattroTech.shop.service.ShopItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for ShopItemWebController
 * Tests all web MVC endpoints with Thymeleaf views
 * COMPLETE TEST SUITE - 100% COVERAGE
 */
class ShopItemWebControllerTest {

    private MockMvc mockMvc;
    private ShopItemService shopItemService;

    @BeforeEach
    void setUp() {
        shopItemService = Mockito.mock(ShopItemService.class);
        ShopItemWebController controller = new ShopItemWebController(shopItemService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    // ============ TEST 1: GET /items - List all items ============
    @Test
    void listItems_returnsItemsListView() throws Exception {
        // Given
        ShopItem item1 = new ShopItem("1", "Laptop", "Gaming laptop", 
                                       new BigDecimal("1500.00"), 5);
        ShopItem item2 = new ShopItem("2", "Mouse", "Wireless mouse", 
                                       new BigDecimal("25.00"), 10);
        given(shopItemService.getAllItems()).willReturn(Arrays.asList(item1, item2));

        // When & Then
        mockMvc.perform(get("/items"))
                .andExpect(status().isOk())
                .andExpect(view().name("items/list"))
                .andExpect(model().attributeExists("items"));

        verify(shopItemService, atLeastOnce()).getAllItems();
    }

    // ============ TEST 1B: GET /items - Empty list shows message ============
    @Test
    void listItems_emptyList_showsNoItemsMessage() throws Exception {
        // Given
        given(shopItemService.getAllItems()).willReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/items"))
                .andExpect(status().isOk())
                .andExpect(view().name("items/list"))
                .andExpect(model().attribute("message", "No items available"));

        verify(shopItemService, atLeastOnce()).getAllItems();
    }

    // ============ TEST 2: GET /items/new - Show create form ============
    @Test
    void newItemForm_returnsFormView() throws Exception {
        mockMvc.perform(get("/items/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("items/form"))
                .andExpect(model().attributeExists("item"))
                .andExpect(model().attribute("formTitle", "Add New Item"))  // ← ADD THIS
                .andExpect(model().attribute("isNew", true));               // ← ADD THIS
    }

    
    // ============ TEST 3: GET /items/edit/{id} - Show edit form (found) ============
    @Test
    void editItemForm_found_returnsFormView() throws Exception {
        // Given
        ShopItem item = new ShopItem("123", "Laptop", "Gaming laptop", 
                                      new BigDecimal("1500.00"), 5);
        given(shopItemService.getItemById("123")).willReturn(Optional.of(item));

        // When & Then
        mockMvc.perform(get("/items/edit/123"))
                .andExpect(status().isOk())
                .andExpect(view().name("items/form"))
                .andExpect(model().attributeExists("item"))
                .andExpect(model().attribute("item", item))
                .andExpect(model().attribute("formTitle", "Edit Item"))  // ← ADD THIS
                .andExpect(model().attribute("isNew", false));           // ← ADD THIS

        verify(shopItemService).getItemById("123");
    }

    // ============ TEST 4: GET /items/edit/{id} - Not found redirects ============
    @Test
    void editItemForm_notFound_redirectsToList() throws Exception {
        // Given
        given(shopItemService.getItemById("999")).willReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/items/edit/999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items"))
                .andExpect(flash().attribute("error", "Item not found with id: 999"));

        verify(shopItemService).getItemById("999");
    }

    // ============ TEST 5: GET /items/{id} - View item details (found) ============
    @Test
    void viewItem_found_returnsDetailView() throws Exception {
        // Given
        ShopItem item = new ShopItem("123", "Laptop", "Gaming laptop", 
                                      new BigDecimal("1500.00"), 5);
        given(shopItemService.getItemById("123")).willReturn(Optional.of(item));

        // When & Then
        mockMvc.perform(get("/items/123"))
                .andExpect(status().isOk())
                .andExpect(view().name("items/detail"))
                .andExpect(model().attributeExists("item"))
                .andExpect(model().attribute("item", item));

        verify(shopItemService).getItemById("123");
    }

    // ============ TEST 6: GET /items/{id} - Not found redirects ============
    @Test
    void viewItem_notFound_redirectsToList() throws Exception {
        // Given
        given(shopItemService.getItemById("999")).willReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/items/999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items"))
                .andExpect(flash().attribute("error", "Item not found with id: 999"));

        verify(shopItemService).getItemById("999");
    }

    // ============ TEST 7: POST /items/save - Create new item success (null ID) ============
    @Test
    void saveItem_newItem_redirectsToList() throws Exception {
        // Given
        ShopItem newItem = new ShopItem(null, "Mouse", "Wireless mouse", 
                                         new BigDecimal("25.00"), 10);
        ShopItem savedItem = new ShopItem("new-id", "Mouse", "Wireless mouse", 
                                           new BigDecimal("25.00"), 10);
        given(shopItemService.insertNewItem(any(ShopItem.class))).willReturn(savedItem);

        // When & Then
        mockMvc.perform(post("/items/save")
                        .param("name", "Mouse")
                        .param("description", "Wireless mouse")
                        .param("price", "25.00")
                        .param("quantity", "10"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items"))
                .andExpect(flash().attribute("success", "Item created successfully!"));

        verify(shopItemService).insertNewItem(any(ShopItem.class));
    }

    // ============ TEST 7B: POST /items/save - Create new item with EMPTY ID ============
    @Test
    void saveItem_newItemWithEmptyId_redirectsToList() throws Exception {
        // Given - empty string ID should be treated as new item
        ShopItem savedItem = new ShopItem("new-id", "Keyboard", "Mechanical", 
                                           new BigDecimal("150.00"), 3);
        given(shopItemService.insertNewItem(any(ShopItem.class))).willReturn(savedItem);

        // When & Then
        mockMvc.perform(post("/items/save")
                        .param("id", "")  // ← Empty string (not null)
                        .param("name", "Keyboard")
                        .param("description", "Mechanical")
                        .param("price", "150.00")
                        .param("quantity", "3"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items"))
                .andExpect(flash().attribute("success", "Item created successfully!"));

        verify(shopItemService).insertNewItem(any(ShopItem.class));
        verify(shopItemService, never()).updateItem(any(), any());
    }

    // ============ TEST 8: POST /items/save - Update existing item success ============
    @Test
    void saveItem_existingItem_redirectsToList() throws Exception {
        // Given
        ShopItem updatedItem = new ShopItem("123", "Updated Mouse", "Updated desc", 
                                             new BigDecimal("30.00"), 15);
        given(shopItemService.updateItem(eq("123"), any(ShopItem.class)))
                .willReturn(updatedItem);

        // When & Then
        mockMvc.perform(post("/items/save")
                        .param("id", "123")
                        .param("name", "Updated Mouse")
                        .param("description", "Updated desc")
                        .param("price", "30.00")
                        .param("quantity", "15"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items"))
                .andExpect(flash().attribute("success", "Item updated successfully!"));

        verify(shopItemService).updateItem(eq("123"), any(ShopItem.class));
    }

    // ============ TEST 9: POST /items/save - Validation errors (NEW item) ============
    @Test
    void saveItem_validationErrors_newItem_returnsFormView() throws Exception {
        // When & Then - blank name triggers validation error
        mockMvc.perform(post("/items/save")
                        .param("name", "")  // ← Invalid: blank
                        .param("description", "Test")
                        .param("price", "100.00")
                        .param("quantity", "5"))
                .andExpect(status().isOk())
                .andExpect(view().name("items/form"))
                .andExpect(model().attributeExists("item"))
                .andExpect(model().attribute("formTitle", "Add New Item"))
                .andExpect(model().attribute("isNew", true))
                .andExpect(model().attributeHasFieldErrors("item", "name"));

        verify(shopItemService, never()).insertNewItem(any());
        verify(shopItemService, never()).updateItem(any(), any());
    }

    // ============ TEST 10: POST /items/save - Validation errors (EXISTING item) ============
    @Test
    void saveItem_validationErrors_existingItem_returnsFormView() throws Exception {
        // When & Then - negative price triggers validation error
        mockMvc.perform(post("/items/save")
                        .param("id", "123")  // ← Existing item
                        .param("name", "Laptop")
                        .param("description", "Test")
                        .param("price", "-100.00")  // ← Invalid: negative
                        .param("quantity", "5"))
                .andExpect(status().isOk())
                .andExpect(view().name("items/form"))
                .andExpect(model().attributeExists("item"))
                .andExpect(model().attribute("formTitle", "Edit Item"))
                .andExpect(model().attribute("isNew", false))
                .andExpect(model().attributeHasFieldErrors("item", "price"));

        verify(shopItemService, never()).insertNewItem(any());
        verify(shopItemService, never()).updateItem(any(), any());
    }

    // ============ TEST 11: POST /items/save - Multiple validation errors ============
    @Test
    void saveItem_multipleValidationErrors_returnsFormView() throws Exception {
        // When & Then - multiple fields invalid
        mockMvc.perform(post("/items/save")
                        .param("name", "")  // ← Invalid: blank
                        .param("description", "Test")
                        .param("price", "-100.00")  // ← Invalid: negative
                        .param("quantity", "-5"))  // ← Invalid: negative
                .andExpect(status().isOk())
                .andExpect(view().name("items/form"))
                .andExpect(model().attributeExists("item"))
                .andExpect(model().attributeHasFieldErrors("item", "name", "price", "quantity"));

        verify(shopItemService, never()).insertNewItem(any());
    }

    // ============ TEST 12: POST /items/save - Exception during INSERT (null ID) ============
    @Test
    void saveItem_insertThrowsException_returnsFormWithError() throws Exception {
        // Given
        given(shopItemService.insertNewItem(any(ShopItem.class)))
                .willThrow(new RuntimeException("Database connection failed"));

        // When & Then
        mockMvc.perform(post("/items/save")
                        .param("name", "Mouse")
                        .param("description", "Test")
                        .param("price", "25.00")
                        .param("quantity", "10"))
                .andExpect(status().isOk())
                .andExpect(view().name("items/form"))
                .andExpect(model().attributeExists("item"))
                .andExpect(model().attribute("error", "Error saving item: Database connection failed"))
                .andExpect(model().attribute("formTitle", "Add New Item"))
                .andExpect(model().attribute("isNew", true));

        verify(shopItemService).insertNewItem(any(ShopItem.class));
    }

    // ============ TEST 13: POST /items/save - Exception during UPDATE (existing ID) ============
    @Test
    void saveItem_updateThrowsException_returnsFormWithError() throws Exception {
        // Given
        given(shopItemService.updateItem(eq("123"), any(ShopItem.class)))
                .willThrow(new RuntimeException("Item not found"));

        // When & Then
        mockMvc.perform(post("/items/save")
                        .param("id", "123")  // ← Existing item
                        .param("name", "Mouse")
                        .param("description", "Test")
                        .param("price", "25.00")
                        .param("quantity", "10"))
                .andExpect(status().isOk())
                .andExpect(view().name("items/form"))
                .andExpect(model().attributeExists("item"))
                .andExpect(model().attribute("error", "Error saving item: Item not found"))
                .andExpect(model().attribute("formTitle", "Edit Item"))
                .andExpect(model().attribute("isNew", false));

        verify(shopItemService).updateItem(eq("123"), any(ShopItem.class));
    }

    // ============ TEST 14: GET /items/delete/{id} - Delete item success ============
    @Test
    void deleteItem_found_redirectsToList() throws Exception {
        // Given
        ShopItem item = new ShopItem("123", "Laptop", "desc", 
                                      new BigDecimal("1500.00"), 5);
        given(shopItemService.getItemById("123")).willReturn(Optional.of(item));
        doNothing().when(shopItemService).deleteItem("123");

        // When & Then
        mockMvc.perform(get("/items/delete/123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items"))
                .andExpect(flash().attribute("success", "Item deleted successfully!"));

        verify(shopItemService).getItemById("123");
        verify(shopItemService).deleteItem("123");
    }

    // ============ TEST 15: GET /items/delete/{id} - Item not found ============
    @Test
    void deleteItem_notFound_redirectsWithError() throws Exception {
        // Given
        given(shopItemService.getItemById("999")).willReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/items/delete/999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items"))
                .andExpect(flash().attribute("error", "Item not found with id: 999"));

        verify(shopItemService).getItemById("999");
        verify(shopItemService, never()).deleteItem(any());
    }

    // ============ TEST 16: GET /items/delete/{id} - Exception during delete ============
    @Test
    void deleteItem_throwsException_redirectsWithError() throws Exception {
        // Given
        ShopItem item = new ShopItem("123", "Laptop", "desc", 
                                      new BigDecimal("1500.00"), 5);
        given(shopItemService.getItemById("123")).willReturn(Optional.of(item));
        doThrow(new RuntimeException("Database error"))
                .when(shopItemService).deleteItem("123");

        // When & Then
        mockMvc.perform(get("/items/delete/123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items"))
                .andExpect(flash().attribute("error", "Error deleting item: Database error"));

        verify(shopItemService).getItemById("123");
        verify(shopItemService).deleteItem("123");
    }
}
