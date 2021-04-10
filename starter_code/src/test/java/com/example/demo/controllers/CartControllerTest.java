package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.helpers.BuildObjects;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Assert;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {

    private CartController cartController;
    private BuildObjects buildObjects;
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setup() {
        cartController = new CartController();
        buildObjects = new BuildObjects();
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);
    }

    @Test
    public void verifyAddItemsToCart() {

        // GIVEN
        Cart newCart = new Cart();
        User user = buildObjects.createUser(0l, "test", "testPassword", newCart);
        Item newItem = buildObjects.createItem(0L, "Avengers", new BigDecimal("12.99"), "Movie Ticket");
        ModifyCartRequest newCartRequest = buildObjects.createCartRequest(0L, 2, "Lluvian");

        ArrayList<Item> itemsList = new ArrayList<>();
        itemsList.add(newItem);
        newCart = buildObjects.createCart(0l, itemsList, user);


        // WHEN
        when(userRepository.findByUsername("Lluvian")).thenReturn(user);
        when(itemRepository.findById(0L)).thenReturn(Optional.of(newItem));

        final ResponseEntity<Cart> response = cartController.addTocart(newCartRequest);


        // THEN
        Cart cart = response.getBody();
        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(200, response.getStatusCodeValue()),
                () -> assertNotNull(cart),
                () -> assertEquals("Avengers", cart.getItems().get(0).getName()),
                () -> assertEquals("Movie Ticket", cart.getItems().get(0).getDescription()),
                () -> assertEquals(new BigDecimal(25.98).setScale(2, RoundingMode.FLOOR), cart.getTotal()),
                () -> assertEquals(2, cart.getItems().size())
        );
    }

    @Test
    public void verifyRemoveItemsFromCart() {

        // GIVEN
        Cart newCart = new Cart();
        User user = buildObjects.createUser(1l, "Test", "testPassword", newCart);
        Item newItem = buildObjects.createItem(1L, "Basketball", new BigDecimal("2"), "Sports");
        ModifyCartRequest modifyCartRequest = buildObjects.createCartRequest(1L, 5, "Lluvian");

        // Validating we have items in our cart with printout
        System.out.println("Item ID: " + modifyCartRequest.getItemId());
        System.out.println("Quantity: " + modifyCartRequest.getQuantity());
        System.out.println("Username: " + modifyCartRequest.getUsername());


        // WHEN
        when(userRepository.findByUsername("Lluvian")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(newItem));

        final ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);


        // THEN
        Cart cart = response.getBody();
        assertAll(
                () -> Assert.assertNotNull(response),
                () -> Assert.assertEquals(200, response.getStatusCodeValue()),
                () -> Assert.assertNotNull(cart),
                () -> Assert.assertEquals(0, cart.getItems().size())
        );
    }
}