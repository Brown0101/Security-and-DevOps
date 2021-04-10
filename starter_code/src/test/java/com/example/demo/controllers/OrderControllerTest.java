package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.helpers.BuildObjects;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private OrderController orderController;
    private BuildObjects buildObjects;
    private OrderRepository orderRepository = mock(OrderRepository.class);
    private UserRepository userRepository = mock(UserRepository.class);

    @Before
    public void setup() {
        orderController = new OrderController();
        buildObjects = new BuildObjects();
        TestUtils.injectObjects(orderController,"orderRepository", orderRepository);
        TestUtils.injectObjects(orderController,"userRepository", userRepository);
    }

    @Test
    public void verifySuccessfulSubmit() {
        // GIVEN
        Item newItem = buildObjects.createItem(
                1L, "Softball", new BigDecimal("12.99"), "Sporting Goods"
        );
        ArrayList<Item> items = new ArrayList<>();
        items.add(newItem);
        Cart cart = buildObjects.createCart(1L, items, null, BigDecimal.valueOf(12.99));
        User user = buildObjects.createUser(1L, "test", "testPassword", cart);
        cart.setUser(user);


        // WHEN
        when(userRepository.findByUsername("Lluvian")).thenReturn(user);
        final ResponseEntity<UserOrder> response = orderController.submit("Lluvian");
        UserOrder order = response.getBody();


        // THEN
        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(200, response.getStatusCodeValue()),
                () -> assertEquals(user, order.getUser()),
                () -> assertEquals(items.get(0).getName(), order.getItems().get(0).getName()),
                () -> assertEquals(items, order.getItems()),
                () -> assertEquals(BigDecimal.valueOf(12.99), order.getTotal())
        );
    }

    @Test
    public void verifyUnsuccessfulSubmitForUser() {

        // GIVEN
        String name = "Lluvian";


        // WHEN
        when(userRepository.findByUsername(name)).thenReturn(null);
        final ResponseEntity<UserOrder> response = orderController.submit(name);


        // THEN
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void verifyUnsuccessfulGetOrdersForUser() {

        // GIVEN
        String name = "Lluvian";


        // WHEN
        when(userRepository.findByUsername(name)).thenReturn(null);
        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(name);


        // THEN
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}