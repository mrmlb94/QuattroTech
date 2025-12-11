package com.example.QuattroTech.shop.service;

import com.example.QuattroTech.shop.model.ShopItem;
import com.example.QuattroTech.shop.repository.ShopItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShopItemServiceTest {

    @Mock
    private ShopItemRepository shopItemRepository;

    @InjectMocks
    private ShopItemService shopItemService;

    @Test
    void getAllItems_returnsAllFromRepository() {
        ShopItem item1 = new ShopItem("1", "Laptop",
                "Gaming laptop", new BigDecimal("1500.00"), 5);
        ShopItem item2 = new ShopItem("2", "Mouse",
                "Wireless mouse", new BigDecimal("25.00"), 10);

        when(shopItemRepository.findAll()).thenReturn(Arrays.asList(item1, item2));

        List<ShopItem> result = shopItemService.getAllItems();

        assertThat(result).containsExactly(item1, item2);
    }

    @Test
    void getItemById_found_returnsItem() {
        ShopItem item = new ShopItem("1", "Keyboard",
                "Mechanical keyboard", new BigDecimal("80.00"), 3);

        when(shopItemRepository.findById("1")).thenReturn(Optional.of(item));

        ShopItem result = shopItemService.getItemById("1");

        assertThat(result).isSameAs(item);
    }

    @Test
    void getItemById_notFound_returnsNull() {
        when(shopItemRepository.findById("99")).thenReturn(Optional.empty());

        ShopItem result = shopItemService.getItemById("99");

        assertThat(result).isNull();
    }

    @Test
    void insertNewItem_setsIdToNull_andSaves() {
        ShopItem toSave = new ShopItem("temp-id", "Headphones",
                "Noise cancelling", new BigDecimal("200.00"), 2);
        ShopItem saved = new ShopItem("1", "Headphones",
                "Noise cancelling", new BigDecimal("200.00"), 2);

        when(shopItemRepository.save(any(ShopItem.class))).thenReturn(saved);

        ShopItem result = shopItemService.insertNewItem(toSave);

        assertThat(result).isSameAs(saved);

        InOrder inOrder = inOrder(shopItemRepository);
        verify(shopItemRepository).save(toSave);
        assertThat(toSave.getId()).isNull();
    }

    @Test
    void updateItemById_setsIdAndSaves() {
        ShopItem replacement = new ShopItem(null, "Monitor",
                "4K monitor", new BigDecimal("400.00"), 1);
        ShopItem saved = new ShopItem("5", "Monitor",
                "4K monitor", new BigDecimal("400.00"), 1);

        when(shopItemRepository.save(replacement)).thenReturn(saved);

        ShopItem result = shopItemService.updateItemById("5", replacement);

        assertThat(result).isSameAs(saved);
        assertThat(replacement.getId()).isEqualTo("5");

        verify(shopItemRepository).save(replacement);
    }

    @Test
    void insertNewItem_invalidName_throwsException() {
        ShopItem invalid = new ShopItem(null, "  ",
                "desc", new BigDecimal("10.00"), 1);

        assertThatThrownBy(() -> shopItemService.insertNewItem(invalid))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void insertNewItem_negativePrice_throwsException() {
        ShopItem invalid = new ShopItem(null, "Item",
                "desc", new BigDecimal("-1.00"), 1);

        assertThatThrownBy(() -> shopItemService.insertNewItem(invalid))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void insertNewItem_negativeQuantity_throwsException() {
        ShopItem invalid = new ShopItem(null, "Item",
                "desc", new BigDecimal("10.00"), -1);

        assertThatThrownBy(() -> shopItemService.insertNewItem(invalid))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
