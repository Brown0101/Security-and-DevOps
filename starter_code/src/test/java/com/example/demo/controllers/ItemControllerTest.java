package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.helpers.BuildObjects;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    private ItemController itemController;
    private BuildObjects buildObjects;
    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setUp() {
        itemController = new ItemController();
        buildObjects = new BuildObjects();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
    }

    @Test
    public void verifyGetItems() {

        // GIVEN
        Item newItem = buildObjects.createItem(
                1L, "Softball", new BigDecimal("12.99"), "Sporting Goods"
        );


        // WHEN
        when(itemRepository.findAll()).thenReturn(Collections.singletonList(newItem));
        final ResponseEntity<List<Item>> response = itemController.getItems();
        List<Item> item = response.getBody();


        // THEN
        assertAll(
                () -> assertEquals(200, response.getStatusCodeValue()),
                () -> assertNotNull(item),
                () -> assertEquals(1, item.size())
        );
    }

    @Test
    public void verifyGetItemById() {

        // GIVEN
        Item newItem = buildObjects.createItem(
                1L, "Softball", new BigDecimal("12.99"), "Sporting Goods"
        );


        // WHEN
        when(itemRepository.findById(newItem.getId())).thenReturn(Optional.of(newItem));
        ResponseEntity<Item> response = itemController.getItemById(1L);
        Item item = response.getBody();


        // THEN
        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(200, response.getStatusCodeValue()),
                () -> assertNotNull(item),
                () -> assertEquals(Long.valueOf(1L), item.getId()),
                () -> assertEquals("Softball", item.getName()),
                () -> assertEquals("Sporting Goods", item.getDescription()),
                () -> assertEquals(BigDecimal.valueOf(12.99), item.getPrice())
        );
    }

    @Test
    public void verifyGetItemByName() {

        // GIVEN
        Item newItem = buildObjects.createItem(
                1L, "Softball", new BigDecimal("12.99"), "Sporting Goods"
        );


        // WHEN
        when(itemRepository.findByName("Softball")).thenReturn(Collections.singletonList(newItem));
        ResponseEntity<List<Item>> response = itemController.getItemsByName(newItem.getName());
        List<Item> items = response.getBody();


        // THEN
        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(200, response.getStatusCodeValue()),
                () -> assertNotNull(items),
                () -> assertEquals(Long.valueOf(1L), items.get(0).getId()),
                () -> assertEquals("Softball", items.get(0).getName()),
                () -> assertEquals("Sporting Goods", items.get(0).getDescription()),
                () -> assertEquals(BigDecimal.valueOf(12.99), items.get(0).getPrice())
        );
    }
}