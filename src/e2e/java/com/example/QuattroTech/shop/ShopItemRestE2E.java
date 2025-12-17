package com.example.QuattroTech.shop;

import com.example.QuattroTech.shop.model.ShopItem;
import com.example.QuattroTech.shop.repository.ShopItemRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * End-to-End tests for REST API
 * Tests the full stack: REST Controller → Service → Repository → MongoDB
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class ShopItemRestE2E {

    @LocalServerPort
    private int port;

    @Container
    static MongoDBContainer mongoContainer = new MongoDBContainer("mongo:6.0")
            .withExposedPorts(27017);

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoContainer::getReplicaSetUrl);
    }

    @Autowired
    private ShopItemRepository repository;

    @BeforeEach
    void setup() {
        RestAssured.port = port;
        repository.deleteAll();
    }

    // ============ E2E TEST 1: GET /api/items - Empty list ============
    @Test
    void getAllItems_emptyDatabase_returnsEmptyArray() {
        given()
            .when()
                .get("/api/items")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", hasSize(0));
    }

    // ============ E2E TEST 2: POST /api/items - Create item ============
    @Test
    void createItem_validData_returnsCreatedItem() {
        ShopItem newItem = new ShopItem(null, "Laptop", "Gaming laptop", 
                                         new BigDecimal("1500.00"), 5);

        given()
            .contentType(ContentType.JSON)
            .body(newItem)
        .when()
            .post("/api/items")
        .then()
            .statusCode(201)
            .body("id", notNullValue())
            .body("name", equalTo("Laptop"))
            .body("description", equalTo("Gaming laptop"))
            .body("price", equalTo(1500.00f))
            .body("quantity", equalTo(5));
    }

    // ============ E2E TEST 3: GET /api/items/{id} - Get existing item ============
    @Test
    void getItemById_existingItem_returnsItem() {
        // Given - Insert item directly to database
        ShopItem item = repository.save(
            new ShopItem(null, "Mouse", "Wireless mouse", new BigDecimal("25.00"), 10)
        );

        // When & Then
        given()
            .when()
                .get("/api/items/" + item.getId())
            .then()
                .statusCode(200)
                .body("id", equalTo(item.getId()))
                .body("name", equalTo("Mouse"))
                .body("price", equalTo(25.00f));
    }

    // ============ E2E TEST 4: PUT /api/items/{id} - Update item ============
    @Test
    void updateItem_existingItem_returnsUpdatedItem() {
        // Given - Insert item
        ShopItem item = repository.save(
            new ShopItem(null, "Keyboard", "Mechanical", new BigDecimal("80.00"), 3)
        );

        ShopItem updateRequest = new ShopItem(item.getId(), "Keyboard Pro", 
                                               "RGB Mechanical", new BigDecimal("120.00"), 5);

        // When & Then
        given()
            .contentType(ContentType.JSON)
            .body(updateRequest)
        .when()
            .put("/api/items/" + item.getId())
        .then()
            .statusCode(200)
            .body("id", equalTo(item.getId()))
            .body("name", equalTo("Keyboard Pro"))
            .body("price", equalTo(120.00f))
            .body("quantity", equalTo(5));
    }

    // ============ E2E TEST 5: DELETE /api/items/{id} - Delete item ============
    @Test
    void deleteItem_existingItem_returnsNoContent() {
        // Given - Insert item
        ShopItem item = repository.save(
            new ShopItem(null, "Headset", "Gaming headset", new BigDecimal("60.00"), 2)
        );

        // When & Then - Delete
        given()
            .when()
                .delete("/api/items/" + item.getId())
            .then()
                .statusCode(204);

        // Verify - Item no longer exists
        given()
            .when()
                .get("/api/items/" + item.getId())
            .then()
                .statusCode(404);
    }

    // ============ E2E TEST 6: GET /api/items/search?name={name} ============
    @Test
    void searchByName_multipleMatches_returnsMatchingItems() {
        // Given - Insert multiple items
        repository.save(new ShopItem(null, "Laptop", "desc1", new BigDecimal("100"), 1));
        repository.save(new ShopItem(null, "Laptop Pro", "desc2", new BigDecimal("200"), 2));
        repository.save(new ShopItem(null, "Mouse", "desc3", new BigDecimal("30"), 3));

        // When & Then
        given()
            .queryParam("name", "Laptop")
        .when()
            .get("/api/items/search")
        .then()
            .statusCode(200)
            .body("$", hasSize(2))
            .body("[0].name", containsString("Laptop"))
            .body("[1].name", containsString("Laptop"));
    }

    // ============ E2E TEST 7: GET /api/items/low-stock?threshold={n} ============
    @Test
    void getLowStockItems_belowThreshold_returnsLowStockItems() {
        // Given - Insert items with varying stock
        repository.save(new ShopItem(null, "Item1", "desc", new BigDecimal("10"), 2));
        repository.save(new ShopItem(null, "Item2", "desc", new BigDecimal("20"), 3));
        repository.save(new ShopItem(null, "Item3", "desc", new BigDecimal("30"), 15));

        // When & Then - Get items with stock < 5
        given()
            .queryParam("threshold", 5)
        .when()
            .get("/api/items/low-stock")
        .then()
            .statusCode(200)
            .body("$", hasSize(2))
            .body("[0].quantity", lessThan(5))
            .body("[1].quantity", lessThan(5));
    }

    // ============ E2E TEST 8: Full CRUD workflow ============
    @Test
    void fullCrudWorkflow_createReadUpdateDelete_worksEndToEnd() {
        // 1. CREATE
        String createdId = given()
            .contentType(ContentType.JSON)
            .body(new ShopItem(null, "Test Item", "Test desc", new BigDecimal("50.00"), 10))
        .when()
            .post("/api/items")
        .then()
            .statusCode(201)
            .extract().path("id");

        // 2. READ
        given()
            .when()
                .get("/api/items/" + createdId)
            .then()
                .statusCode(200)
                .body("name", equalTo("Test Item"));

        // 3. UPDATE
        given()
            .contentType(ContentType.JSON)
            .body(new ShopItem(createdId, "Updated Item", "Updated", new BigDecimal("60.00"), 15))
        .when()
            .put("/api/items/" + createdId)
        .then()
            .statusCode(200)
            .body("name", equalTo("Updated Item"));

        // 4. DELETE
        given()
            .when()
                .delete("/api/items/" + createdId)
            .then()
                .statusCode(204);

        // 5. VERIFY DELETED
        given()
            .when()
                .get("/api/items/" + createdId)
            .then()
                .statusCode(404);
    }
}
