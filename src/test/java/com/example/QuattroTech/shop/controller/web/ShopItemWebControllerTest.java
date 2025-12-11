package com.example.QuattroTech.shop.controller.web;

import com.example.QuattroTech.shop.model.ShopItem;
import com.example.QuattroTech.shop.service.ShopItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceView;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ShopItemWebControllerTest {

    private MockMvc mockMvc;
    private ShopItemService shopItemService;

    @BeforeEach
    void setUp() {
        shopItemService = Mockito.mock(ShopItemService.class);
        ShopItemWebController controller = new ShopItemWebController(shopItemService);

        mockMvc = MockMvcBuilders.standaloneSetup(controller)

                .setViewResolvers((viewName, locale) ->
                        new InternalResourceView("/WEB-INF/" + viewName + ".jsp"))
                .build();
    }

    @Test
    void showItems_returnsItemsView() throws Exception {
        ShopItem item = new ShopItem("id1", "item1",
                new BigDecimal("10.00"), 5);
        given(shopItemService.getAllItems()).willReturn(List.of(item));

        mockMvc.perform(get("/shop/items"))
                .andExpect(status().isOk())
                .andExpect(view().name("items"))
                .andExpect(model().attributeExists("items"));
    }
}
