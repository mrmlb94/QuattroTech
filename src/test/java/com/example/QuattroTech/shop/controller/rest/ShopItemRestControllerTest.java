package com.example.QuattroTech.shop.controller.rest;

import com.example.QuattroTech.shop.model.ShopItem;
import com.example.QuattroTech.shop.service.ShopItemService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for ShopItemRestController
 * Tests all REST API endpoints with mock service layer
 */
class ShopItemRestControllerTest {

    private MockMvc mockMvc;
    private ShopItemService shopItemService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        shopItemService = Mockito.mock(ShopItemService.class);
        ShopItemRestController controller = new ShopItemRestController(shopItemService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
    }

    // ============ TEST 1: GET /api/items - Get all items ============
    @Test
    void getAllItems_returnsOkWithItems() throws Exception {
        // Given
        ShopItem item = new ShopItem("id1", "item1", "desc", new BigDecimal("10.00"), 5);
        given(shopItemService.getAllItems()).willReturn(Arrays.asList(item));

        // When & Then
        mockMvc.perform(get("/api/items"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value("id1"))
                .andExpect(jsonPath("$[0].name").value("item1"))
                .andExpect(jsonPath("$[0].description").value("desc"))
                .andExpect(jsonPath("$[0].price").value(10.00))
                .andExpect(jsonPath("$[0].quantity").value(5));
    }

    // ============ TEST 2: GET /api/items/{id} - Found ============
    @Test
    void getItemById_found_returnsOkWithItem() throws Exception {
        // Given
        ShopItem item = new ShopItem("123", "Laptop", "Gaming laptop", 
                                      new BigDecimal("1500.00"), 3);
        given(shopItemService.getItemById("123")).willReturn(Optional.of(item));

        // When & Then
        mockMvc.perform(get("/api/items/123"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("123"))
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.price").value(1500.00))
                .andExpect(jsonPath("$.quantity").value(3));

        verify(shopItemService).getItemById("123");
    }

    // ============ TEST 3: GET /api/items/{id} - Not Found ============
    @Test
    void getItemById_notFound_returns404() throws Exception {
        // Given
        given(shopItemService.getItemById("999")).willReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/items/999"))
                .andExpect(status().isNotFound());

        verify(shopItemService).getItemById("999");
    }

    // ============ TEST 4: POST /api/items - Create with valid data ============
    @Test
    void createItem_validData_returnsCreated() throws Exception {
        // Given
        ShopItem newItem = new ShopItem(null, "Mouse", "Wireless mouse", 
                                         new BigDecimal("25.00"), 10);
        ShopItem savedItem = new ShopItem("new-id", "Mouse", "Wireless mouse", 
                                           new BigDecimal("25.00"), 10);
        
        given(shopItemService.insertNewItem(any(ShopItem.class))).willReturn(savedItem);

        // When & Then
        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItem)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("new-id"))
                .andExpect(jsonPath("$.name").value("Mouse"))
                .andExpect(jsonPath("$.price").value(25.00));

        verify(shopItemService).insertNewItem(any(ShopItem.class));
    }

    // ============ TEST 5: POST /api/items - Invalid data (validation) ============
    @Test
    void createItem_invalidData_returns400() throws Exception {
        // Given - Item with blank name and negative price
        ShopItem invalidItem = new ShopItem(null, "", "desc", 
                                             new BigDecimal("-10.00"), -5);

        // When & Then
        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidItem)))
                .andExpect(status().isBadRequest());
    }

    // ============ TEST 6: PUT /api/items/{id} - Update existing ============
    @Test
    void updateItem_found_returnsOkWithUpdatedItem() throws Exception {
        // Given
        ShopItem updateRequest = new ShopItem("123", "Updated Mouse", "Updated desc", 
                                               new BigDecimal("30.00"), 15);
        ShopItem updatedItem = new ShopItem("123", "Updated Mouse", "Updated desc", 
                                             new BigDecimal("30.00"), 15);
        
        given(shopItemService.getItemById("123")).willReturn(Optional.of(updatedItem));
        given(shopItemService.updateItem(eq("123"), any(ShopItem.class))).willReturn(updatedItem);

        // When & Then
        mockMvc.perform(put("/api/items/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("123"))
                .andExpect(jsonPath("$.name").value("Updated Mouse"))
                .andExpect(jsonPath("$.price").value(30.00));

        verify(shopItemService).getItemById("123");
        verify(shopItemService).updateItem(eq("123"), any(ShopItem.class));
    }

    // ============ TEST 7: PUT /api/items/{id} - Not found ============
    @Test
    void updateItem_notFound_returns404() throws Exception {
        // Given
        ShopItem updateRequest = new ShopItem("999", "Item", "desc", 
                                               new BigDecimal("10.00"), 1);
        given(shopItemService.getItemById("999")).willReturn(Optional.empty());

        // When & Then
        mockMvc.perform(put("/api/items/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());

        verify(shopItemService).getItemById("999");
    }

    // ============ TEST 8: DELETE /api/items/{id} - Found ============
    @Test
    void deleteItem_found_returnsNoContent() throws Exception {
        // Given
        ShopItem existingItem = new ShopItem("123", "Item", "desc", 
                                              new BigDecimal("10.00"), 1);
        given(shopItemService.getItemById("123")).willReturn(Optional.of(existingItem));
        doNothing().when(shopItemService).deleteItem("123");

        // When & Then
        mockMvc.perform(delete("/api/items/123"))
                .andExpect(status().isNoContent());

        verify(shopItemService).getItemById("123");
        verify(shopItemService).deleteItem("123");
    }

    // ============ TEST 9: DELETE /api/items/{id} - Not found ============
    @Test
    void deleteItem_notFound_returns404() throws Exception {
        // Given
        given(shopItemService.getItemById("999")).willReturn(Optional.empty());

        // When & Then
        mockMvc.perform(delete("/api/items/999"))
                .andExpect(status().isNotFound());

        verify(shopItemService).getItemById("999");
    }

    // ============ BONUS TEST 10: GET /api/items/search?name={name} ============
    @Test
    void searchByName_returnsMatchingItems() throws Exception {
        // Given
        ShopItem item1 = new ShopItem("1", "Laptop", "desc", new BigDecimal("100"), 1);
        ShopItem item2 = new ShopItem("2", "Laptop Pro", "desc", new BigDecimal("200"), 2);
        given(shopItemService.searchByName("Laptop")).willReturn(Arrays.asList(item1, item2));

        // When & Then
        mockMvc.perform(get("/api/items/search").param("name", "Laptop"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Laptop"))
                .andExpect(jsonPath("$[1].name").value("Laptop Pro"));

        verify(shopItemService).searchByName("Laptop");
    }

    // ============ BONUS TEST 11: GET /api/items/low-stock ============
    @Test
    void getLowStockItems_returnsItemsBelowThreshold() throws Exception {
        // Given
        ShopItem item1 = new ShopItem("1", "Item1", "desc", new BigDecimal("10"), 2);
        ShopItem item2 = new ShopItem("2", "Item2", "desc", new BigDecimal("20"), 3);
        given(shopItemService.findLowStockItems(5)).willReturn(Arrays.asList(item1, item2));

        // When & Then
        mockMvc.perform(get("/api/items/low-stock").param("threshold", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].quantity").value(2))
                .andExpect(jsonPath("$[1].quantity").value(3));

        verify(shopItemService).findLowStockItems(5);
    }
}
