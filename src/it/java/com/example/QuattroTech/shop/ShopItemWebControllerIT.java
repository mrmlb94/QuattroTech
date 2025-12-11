package com.example.QuattroTech.shop;

import com.example.QuattroTech.shop.model.ShopItem;
import com.example.QuattroTech.shop.service.ShopItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {QuattroTechApplication.class, ShopItemWebControllerIT.TestConfig.class}
)
class ShopItemWebControllerIT {

    @Configuration
    static class TestConfig {
        @Bean
        ShopItemService shopItemService() {
            return Mockito.mock(ShopItemService.class);
        }
    }

    @LocalServerPort
    private int port;

    @Autowired
    private ShopItemService shopItemService;

    private WebDriver driver;

    @BeforeEach
    void setUp() {
        driver = new HtmlUnitDriver();
    }

    @Test
    void itemsPage_showsExistingItem() {
        ShopItem item = new ShopItem("id1", "test item",
                new BigDecimal("10.00"), 3);
        given(shopItemService.getAllItems()).willReturn(List.of(item));

        driver.get("http://localhost:" + port + "/shop/items");

        String pageText = driver.findElement(By.tagName("body")).getText();
        assertThat(pageText).contains("test item");
        assertThat(pageText).contains("10.00");
    }
}
