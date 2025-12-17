package com.example.QuattroTech.shop.service;

import com.example.QuattroTech.shop.model.ShopItem;
import com.example.QuattroTech.shop.repository.ShopItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ShopItemServiceImpl
 * Tests all business logic and validation
 * COMPLETE TEST SUITE - 100% COVERAGE
 */
class ShopItemServiceTest {

    @Mock
    private ShopItemRepository repository;

    @InjectMocks
    private ShopItemServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ========================================
    // EXISTING TESTS (KEEP ALL OF THESE)
    // ========================================

    @Test
    void getAllItems_returnsAllItems() {
        // Given
        ShopItem item1 = new ShopItem("1", "Laptop", "Gaming", new BigDecimal("1500"), 5);
        ShopItem item2 = new ShopItem("2", "Mouse", "Wireless", new BigDecimal("25"), 10);
        given(repository.findAll()).willReturn(Arrays.asList(item1, item2));

        // When
        List<ShopItem> result = service.getAllItems();

        // Then
        assertThat(result).hasSize(2);
        verify(repository).findAll();
    }

    @Test
    void getAllItems_emptyRepository_returnsEmptyList() {
        // Given
        given(repository.findAll()).willReturn(Collections.emptyList());

        // When
        List<ShopItem> result = service.getAllItems();

        // Then
        assertThat(result).isEmpty();
        verify(repository).findAll();
    }

    @Test
    void getItemById_existingId_returnsItem() {
        // Given
        ShopItem item = new ShopItem("123", "Laptop", "Gaming", new BigDecimal("1500"), 5);
        given(repository.findById("123")).willReturn(Optional.of(item));

        // When
        Optional<ShopItem> result = service.getItemById("123");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Laptop");
        verify(repository).findById("123");
    }

    @Test
    void getItemById_nonExistingId_returnsEmpty() {
        // Given
        given(repository.findById("999")).willReturn(Optional.empty());

        // When
        Optional<ShopItem> result = service.getItemById("999");

        // Then
        assertThat(result).isEmpty();
        verify(repository).findById("999");
    }

    @Test
    void insertNewItem_validItem_savesWithNullId() {
        // Given
        ShopItem item = new ShopItem("should-be-ignored", "Mouse", "Wireless", 
                                     new BigDecimal("25"), 10);
        ShopItem savedItem = new ShopItem("new-generated-id", "Mouse", "Wireless", 
                                          new BigDecimal("25"), 10);
        given(repository.save(any(ShopItem.class))).willReturn(savedItem);

        // When
        ShopItem result = service.insertNewItem(item);

        // Then
        assertThat(result.getId()).isEqualTo("new-generated-id");
        verify(repository).save(any(ShopItem.class));
    }

    @Test
    void updateItem_validItem_savesWithSpecifiedId() {
        // Given
        ShopItem item = new ShopItem("wrong-id", "Mouse", "Updated", 
                                     new BigDecimal("30"), 15);
        ShopItem updatedItem = new ShopItem("123", "Mouse", "Updated", 
                                            new BigDecimal("30"), 15);
        given(repository.save(any(ShopItem.class))).willReturn(updatedItem);

        // When
        ShopItem result = service.updateItem("123", item);

        // Then
        assertThat(result.getId()).isEqualTo("123");
        verify(repository).save(any(ShopItem.class));
    }

    @Test
    void deleteItem_callsRepository() {
        // When
        service.deleteItem("123");

        // Then
        verify(repository).deleteById("123");
    }

    @Test
    void searchByName_returnsMatchingItems() {
        // Given
        ShopItem item1 = new ShopItem("1", "Laptop", "Gaming", new BigDecimal("1500"), 5);
        ShopItem item2 = new ShopItem("2", "Laptop Stand", "Ergonomic", new BigDecimal("50"), 20);
        given(repository.findByNameContainingIgnoreCase("laptop"))
                .willReturn(Arrays.asList(item1, item2));

        // When
        List<ShopItem> result = service.searchByName("laptop");

        // Then
        assertThat(result).hasSize(2);
        verify(repository).findByNameContainingIgnoreCase("laptop");
    }

    @Test
    void searchByName_noMatches_returnsEmptyList() {
        // Given
        given(repository.findByNameContainingIgnoreCase("nonexistent"))
                .willReturn(Collections.emptyList());

        // When
        List<ShopItem> result = service.searchByName("nonexistent");

        // Then
        assertThat(result).isEmpty();
        verify(repository).findByNameContainingIgnoreCase("nonexistent");
    }

    @Test
    void findLowStockItems_returnsItemsBelowThreshold() {
        // Given
        ShopItem item1 = new ShopItem("1", "Mouse", "Low stock", new BigDecimal("25"), 2);
        ShopItem item2 = new ShopItem("2", "Keyboard", "Very low", new BigDecimal("50"), 1);
        given(repository.findItemsWithLowStock(5)).willReturn(Arrays.asList(item1, item2));

        // When
        List<ShopItem> result = service.findLowStockItems(5);

        // Then
        assertThat(result).hasSize(2);
        verify(repository).findItemsWithLowStock(5);
    }

    // ========================================
    // NEW TESTS - VALIDATION BRANCH COVERAGE
    // ========================================

    /**
     * TEST 1: Validation - NULL name triggers exception
     * Covers branch: item.getName() == null → TRUE
     */
    @Test
    void insertNewItem_nullName_throwsException() {
        // Given
        ShopItem item = new ShopItem();
        item.setName(null);  // ← NULL name
        item.setDescription("Test");
        item.setPrice(new BigDecimal("100"));
        item.setQuantity(5);

        // When & Then
        assertThatThrownBy(() -> service.insertNewItem(item))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Name must not be blank");

        verify(repository, never()).save(any());
    }

    /**
     * TEST 2: Validation - BLANK name triggers exception
     * Covers branch: item.getName().isBlank() → TRUE
     */
    @Test
    void insertNewItem_blankName_throwsException() {
        // Given
        ShopItem item = new ShopItem();
        item.setName("   ");  // ← BLANK name (not null, but whitespace)
        item.setDescription("Test");
        item.setPrice(new BigDecimal("100"));
        item.setQuantity(5);

        // When & Then
        assertThatThrownBy(() -> service.insertNewItem(item))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Name must not be blank");

        verify(repository, never()).save(any());
    }

    /**
     * TEST 3: Validation - EMPTY string name triggers exception
     * Covers branch: item.getName().isBlank() → TRUE (empty string)
     */
    @Test
    void insertNewItem_emptyName_throwsException() {
        // Given
        ShopItem item = new ShopItem();
        item.setName("");  // ← EMPTY string
        item.setDescription("Test");
        item.setPrice(new BigDecimal("100"));
        item.setQuantity(5);

        // When & Then
        assertThatThrownBy(() -> service.insertNewItem(item))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Name must not be blank");

        verify(repository, never()).save(any());
    }

    /**
     * TEST 4: Validation - NULL price triggers exception
     * Covers branch: item.getPrice() == null → TRUE
     */
    @Test
    void insertNewItem_nullPrice_throwsException() {
        // Given
        ShopItem item = new ShopItem();
        item.setName("Laptop");
        item.setDescription("Test");
        item.setPrice(null);  // ← NULL price
        item.setQuantity(5);

        // When & Then
        assertThatThrownBy(() -> service.insertNewItem(item))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Price must be >= 0");

        verify(repository, never()).save(any());
    }

    /**
     * TEST 5: Validation - NEGATIVE price triggers exception
     * Covers branch: item.getPrice().compareTo(BigDecimal.ZERO) < 0 → TRUE
     */
    @Test
    void insertNewItem_negativePrice_throwsException() {
        // Given
        ShopItem item = new ShopItem();
        item.setName("Laptop");
        item.setDescription("Test");
        item.setPrice(new BigDecimal("-100"));  // ← NEGATIVE price
        item.setQuantity(5);

        // When & Then
        assertThatThrownBy(() -> service.insertNewItem(item))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Price must be >= 0");

        verify(repository, never()).save(any());
    }

    /**
     * TEST 6: Validation - NEGATIVE quantity triggers exception
     * Covers branch: item.getQuantity() < 0 → TRUE
     */
    @Test
    void insertNewItem_negativeQuantity_throwsException() {
        // Given
        ShopItem item = new ShopItem();
        item.setName("Laptop");
        item.setDescription("Test");
        item.setPrice(new BigDecimal("100"));
        item.setQuantity(-5);  // ← NEGATIVE quantity

        // When & Then
        assertThatThrownBy(() -> service.insertNewItem(item))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Quantity must be >= 0");

        verify(repository, never()).save(any());
    }

    /**
     * TEST 7: Validation in UPDATE - null name
     */
    @Test
    void updateItem_nullName_throwsException() {
        // Given
        ShopItem item = new ShopItem();
        item.setName(null);
        item.setPrice(new BigDecimal("100"));
        item.setQuantity(5);

        // When & Then
        assertThatThrownBy(() -> service.updateItem("123", item))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Name must not be blank");

        verify(repository, never()).save(any());
    }

    /**
     * TEST 8: Validation in UPDATE - blank name
     */
    @Test
    void updateItem_blankName_throwsException() {
        // Given
        ShopItem item = new ShopItem();
        item.setName("  ");  // blank
        item.setPrice(new BigDecimal("100"));
        item.setQuantity(5);

        // When & Then
        assertThatThrownBy(() -> service.updateItem("123", item))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Name must not be blank");

        verify(repository, never()).save(any());
    }

    /**
     * TEST 9: Validation in UPDATE - null price
     */
    @Test
    void updateItem_nullPrice_throwsException() {
        // Given
        ShopItem item = new ShopItem();
        item.setName("Laptop");
        item.setPrice(null);
        item.setQuantity(5);

        // When & Then
        assertThatThrownBy(() -> service.updateItem("123", item))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Price must be >= 0");

        verify(repository, never()).save(any());
    }

    /**
     * TEST 10: Validation in UPDATE - negative price
     */
    @Test
    void updateItem_negativePrice_throwsException() {
        // Given
        ShopItem item = new ShopItem();
        item.setName("Laptop");
        item.setPrice(new BigDecimal("-50"));
        item.setQuantity(5);

        // When & Then
        assertThatThrownBy(() -> service.updateItem("123", item))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Price must be >= 0");

        verify(repository, never()).save(any());
    }

    /**
     * TEST 11: Validation in UPDATE - negative quantity
     */
    @Test
    void updateItem_negativeQuantity_throwsException() {
        // Given
        ShopItem item = new ShopItem();
        item.setName("Laptop");
        item.setPrice(new BigDecimal("100"));
        item.setQuantity(-10);

        // When & Then
        assertThatThrownBy(() -> service.updateItem("123", item))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Quantity must be >= 0");

        verify(repository, never()).save(any());
    }

    /**
     * SURVIVOR FIX #1: Test that quantity = 0 is VALID (boundary test)
     * Kills ConditionalsBoundaryMutator on line: if (item.getQuantity() < 0)
     */
    @Test
    void insertNewItem_quantityZero_success() {
        // Given
        ShopItem item = new ShopItem();
        item.setName("Free Sample");
        item.setDescription("Giveaway item");
        item.setPrice(new BigDecimal("10.00"));
        item.setQuantity(0);  // ← Boundary: 0 should be VALID
        
        ShopItem savedItem = new ShopItem("123", "Free Sample", "Giveaway item", 
                                           new BigDecimal("10.00"), 0);
        given(repository.save(any(ShopItem.class))).willReturn(savedItem);

        // When
        ShopItem result = service.insertNewItem(item);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getQuantity()).isEqualTo(0);  // Verify 0 is accepted
        verify(repository).save(any(ShopItem.class));
    }

    /**
     * SURVIVOR FIX #2: Test that price = 0 is VALID (boundary test)
     * Kills ConditionalsBoundaryMutator on line: if (item.getPrice().compareTo(BigDecimal.ZERO) < 0)
     */
    @Test
    void insertNewItem_priceZero_success() {
        // Given
        ShopItem item = new ShopItem();
        item.setName("Free Item");
        item.setDescription("No cost");
        item.setPrice(BigDecimal.ZERO);  // ← Boundary: 0 should be VALID
        item.setQuantity(10);
        
        ShopItem savedItem = new ShopItem("123", "Free Item", "No cost", 
                                           BigDecimal.ZERO, 10);
        given(repository.save(any(ShopItem.class))).willReturn(savedItem);

        // When
        ShopItem result = service.insertNewItem(item);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getPrice()).isEqualByComparingTo(BigDecimal.ZERO);  // Verify 0 is accepted
        verify(repository).save(any(ShopItem.class));
    }

    /**
     * SURVIVOR FIX #3: Test that quantity = 0 is VALID in UPDATE (boundary test)
     * Kills ConditionalsBoundaryMutator on updateItem method
     */
    @Test
    void updateItem_quantityZero_success() {
        // Given
        ShopItem item = new ShopItem();
        item.setName("Laptop");
        item.setDescription("Gaming laptop");
        item.setPrice(new BigDecimal("1500.00"));
        item.setQuantity(0);  // ← Boundary: 0 should be VALID
        
        ShopItem updatedItem = new ShopItem("123", "Laptop", "Gaming laptop", 
                                             new BigDecimal("1500.00"), 0);
        given(repository.save(any(ShopItem.class))).willReturn(updatedItem);

        // When
        ShopItem result = service.updateItem("123", item);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getQuantity()).isEqualTo(0);
        verify(repository).save(any(ShopItem.class));
    }

    /**
     * SURVIVOR FIX #4: Test that price = 0 is VALID in UPDATE (boundary test)
     * Kills ConditionalsBoundaryMutator on updateItem method
     */
    @Test
    void updateItem_priceZero_success() {
        // Given
        ShopItem item = new ShopItem();
        item.setName("Free Sticker");
        item.setDescription("Promotional");
        item.setPrice(BigDecimal.ZERO);  // ← Boundary: 0 should be VALID
        item.setQuantity(100);
        
        ShopItem updatedItem = new ShopItem("123", "Free Sticker", "Promotional", 
                                             BigDecimal.ZERO, 100);
        given(repository.save(any(ShopItem.class))).willReturn(updatedItem);

        // When
        ShopItem result = service.updateItem("123", item);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getPrice()).isEqualByComparingTo(BigDecimal.ZERO);
        verify(repository).save(any(ShopItem.class));
    }
    /**
     * SURVIVOR FIX #1: Verify that insertNewItem() forces ID to null
     * Kills VoidMethodCallMutator on line 33 (item.setId(null))
     */
    @Test
    void insertNewItem_forcesIdToNull() {
        // Given - item with an existing ID
        ShopItem item = new ShopItem("existing-id-should-be-removed", "Laptop", "Gaming", 
                                     new BigDecimal("1500"), 5);
        
        // Capture the actual item passed to repository.save()
        ArgumentCaptor<ShopItem> itemCaptor = ArgumentCaptor.forClass(ShopItem.class);
        
        ShopItem savedItem = new ShopItem("new-generated-id", "Laptop", "Gaming", 
                                           new BigDecimal("1500"), 5);
        given(repository.save(any(ShopItem.class))).willReturn(savedItem);

        // When
        service.insertNewItem(item);

        // Then - verify that repository.save() was called with ID = null
        verify(repository).save(itemCaptor.capture());
        ShopItem capturedItem = itemCaptor.getValue();
        assertThat(capturedItem.getId()).isNull();  // ← This kills the mutant
    }

    /**
     * SURVIVOR FIX #2: Verify that updateItem() sets the correct ID
     * Kills VoidMethodCallMutator on line 42 (item.setId(id))
     */
    @Test
    void updateItem_forcesCorrectId() {
        // Given - item with wrong ID
        ShopItem item = new ShopItem("wrong-id", "Laptop", "Updated", 
                                     new BigDecimal("1600"), 10);
        
        // Capture the actual item passed to repository.save()
        ArgumentCaptor<ShopItem> itemCaptor = ArgumentCaptor.forClass(ShopItem.class);
        
        ShopItem updatedItem = new ShopItem("correct-id-123", "Laptop", "Updated", 
                                             new BigDecimal("1600"), 10);
        given(repository.save(any(ShopItem.class))).willReturn(updatedItem);

        // When - update with correct ID
        service.updateItem("correct-id-123", item);

        // Then - verify that repository.save() was called with correct ID
        verify(repository).save(itemCaptor.capture());
        ShopItem capturedItem = itemCaptor.getValue();
        assertThat(capturedItem.getId()).isEqualTo("correct-id-123");  // ← This kills the mutant
    }

}
