package com.example.QuattroTech.shop.controller.rest;

import com.example.QuattroTech.shop.model.ShopItem;
import com.example.QuattroTech.shop.service.ShopItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ShopItemRestControllerTest {

    private MockMvc mockMvc;
    private ShopItemService shopItemService;

    @BeforeEach
    void setUp() {
        shopItemService = Mockito.mock(ShopItemService.class);
        ShopItemRestController controller = new ShopItemRestController(shopItemService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getAllItems_returnsOk() throws Exception {
        ShopItem item = new ShopItem("id1", "item1",
                new BigDecimal("10.00"), 5);
        given(shopItemService.getAllItems()).willReturn(List.of(item));

        mockMvc.perform(get("/api/items"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }
}
