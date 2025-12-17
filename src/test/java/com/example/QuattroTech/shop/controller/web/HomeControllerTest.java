package com.example.QuattroTech.shop.controller.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit test for HomeController.
 * Tests that the home page renders correctly.
 */
@WebMvcTest(HomeController.class)
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void home_rendersIndexView() throws Exception {
        mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(view().name("index"))
            .andExpect(content().contentTypeCompatibleWith("text/html"))
            .andExpect(content().string(containsString("QuattroTech Shop")))
            .andExpect(content().string(containsString("Welcome to QuattroTech")));
    }
    
    @Test
    void home_containsNavigationLinks() throws Exception {
        mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("/items")))
            .andExpect(content().string(containsString("/items/new")))
            .andExpect(content().string(containsString("/api/items")));
    }
}
