package com.example.QuattroTech.shop.repository;

import com.example.QuattroTech.shop.model.ShopItem;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
class ShopItemMongoIT {

    @Container
    static MongoDBContainer mongo =
            new MongoDBContainer(DockerImageName.parse("mongo:6.0"));

    @DynamicPropertySource
    static void mongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongo::getConnectionString);
    }

    @Test
    void testMongoMapping_andCrudOperations() {
        String uri = mongo.getConnectionString();

        try (MongoClient client = MongoClients.create(uri)) {
            MongoCollection<Document> collection =
                    client.getDatabase("test").getCollection("shop_items");

            collection.drop();

            ShopItem item = new ShopItem(
                    "Laptop",
                    "Gaming laptop",
                    new BigDecimal("1500.00"),
                    5
            );

            Document doc = new Document()
                    .append("name", item.getName())
                    .append("description", item.getDescription())
                    .append("price", item.getPrice())
                    .append("quantity", item.getQuantity());

            collection.insertOne(doc);

            long count = collection.countDocuments();
            assertThat(count).isEqualTo(1);
        }
    }
}
