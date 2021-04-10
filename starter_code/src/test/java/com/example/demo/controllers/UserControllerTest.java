package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.helpers.BuildObjects;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Assert;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;
    private BuildObjects buildObjects;
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setup() {
        userController = new UserController();
        buildObjects = new BuildObjects();
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);
    }

    @Test
    public void verifyCreateUser() throws Exception {

        // GIVEN
        CreateUserRequest createUserRequest = buildObjects.createUserRequest("test", "testPassword", "testPassword");


        // WHEN
        when(encoder.encode("testPassword")).thenReturn("thisIsHashed");

        final ResponseEntity<User> response = userController.createUser(createUserRequest);


        // THEN
        User user = response.getBody();

        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(200, response.getStatusCodeValue()),
                () -> Assert.assertNotNull(user),
                () -> Assert.assertEquals(0, user.getId()),
                () -> Assert.assertEquals("test", user.getUsername()),
                () -> Assert.assertEquals("thisIsHashed", user.getPassword())
        );
    }

    @Test
    public void verifyFindUserById() throws Exception {

        // GIVEN
        CreateUserRequest r = buildObjects.createUserRequest("test", "testPassword", "testPassword");


        // WHEN
        final ResponseEntity<User> response = userController.createUser(r);
        User user = response.getBody();
        when(encoder.encode("testPassword")).thenReturn("thisIsHashed");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));


        // THEN
        assertAll(
                () -> assertEquals(200, response.getStatusCodeValue()),
                () -> assertEquals(0, user.getId()),
                () -> assertEquals("test", user.getUsername())
        );
    }

    @Test
    public void verifyFindByUsername() throws Exception {

        // GIVEN
        CreateUserRequest createUserRequest = buildObjects.createUserRequest(
                "test", "testPassword", "testPassword"
        );


        // WHEN
        when(encoder.encode("testPassword")).thenReturn("thisIsHashed");
        final ResponseEntity<User> response = userController.createUser(createUserRequest);
        User user = response.getBody();


        // THEN
        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(200, response.getStatusCodeValue()),
                () -> assertEquals(0, user.getId()),
                () -> assertEquals("test", user.getUsername())
        );
    }

    @Test
    public void verifyUserNotCreated() {

        // GIVEN
        CreateUserRequest createUserRequest = buildObjects.createUserRequest(
                "test", "supersecret", "supersecrets"
        ); // passwords do not match


        // WHEN
        when(encoder.encode("supersecret")).thenReturn("thisIsHashed");
        final ResponseEntity<User> response = userController.createUser(createUserRequest);
        User user = response.getBody();


        // THEN
        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(400, response.getStatusCodeValue())
        );
    }

    @Test
    public void verifyFindByUsernameNotFound() {

        // GIVEN
        String name = "Lluvian";


        // WHEN
        final ResponseEntity<User> response = userController.findByUserName(name);


        // THEN
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void verifyFindByUserIDNotFound() {

        // GIVEN
        final Long ID = 1L;


        // WHEN
        final ResponseEntity<User> response = userController.findById(ID);


        // THEN
        assertEquals(404, response.getStatusCodeValue());
    }
}
