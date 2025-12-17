package com.example.QuattroTech.shop;

import com.example.QuattroTech.shop.model.ShopItem;
import com.example.QuattroTech.shop.repository.ShopItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class ShopItemWebControllerIT {

    @SuppressWarnings("resource")
    @Container
    static MongoDBContainer mongoContainer = new MongoDBContainer("mongo:6.0")
            .withExposedPorts(27017);

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoContainer::getReplicaSetUrl);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ShopItemRepository repository;

    @BeforeEach
    void setup() {
        repository.deleteAll();
    }

    @Test
    void itemsPage_showsExistingItem() throws Exception {
        // Given: An item exists in database
        ShopItem item = new ShopItem(null, "test item", "description",
                new BigDecimal("10.00"), 5);
        repository.save(item);

        // When: We request the items page
        mockMvc.perform(get("/items"))  // Changed from /shop/items
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("test item")));
    }
}
