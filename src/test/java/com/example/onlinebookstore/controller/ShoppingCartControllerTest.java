package com.example.onlinebookstore.controller;

import static com.example.onlinebookstore.util.cartitem.TestCartItemSupplier.getTestCreateCartItemRequestDto;
import static com.example.onlinebookstore.util.shoppingcart.TestShoppingCartSupplier.getTestShoppingCartDto;
import static com.example.onlinebookstore.util.user.TestUserSupplier.getTestUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.onlinebookstore.dto.cartitem.CartItemDto;
import com.example.onlinebookstore.dto.cartitem.CreateCartItemRequestDto;
import com.example.onlinebookstore.dto.cartitem.UpdateCartItemRequestDto;
import com.example.onlinebookstore.dto.shoppingcart.ShoppingCartDto;
import com.example.onlinebookstore.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ShoppingCartControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext applicationContext) throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        teardown(dataSource);
    }

    @BeforeEach
    void setUp(@Autowired DataSource dataSource) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "database/books/insert-test-books.sql"));
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "database/users/insert-test-users-with-shopping-carts.sql"));
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "database/cartitems/insert-test-cartitem.sql"));
        }
    }

    @AfterEach
    void tearDown(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @SneakyThrows
    private static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/cartitems/delete-all-cartitems.sql"));
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/users/delete-all-users.sql"));
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/books/delete-all-test-books.sql"));
        }
    }

    @Test
    @WithMockUser
    @DisplayName("""
            Verify getById method works
            """)
    void getById_ValidId_ReturnsShoppingCartDto() throws Exception {
        //given
        Long id = 1L;
        ShoppingCartDto expected = getTestShoppingCartDto();
        User user = getTestUser();

        //when
        MvcResult result = mockMvc.perform(get("/cart")
                        .with(user(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //then
        ShoppingCartDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                ShoppingCartDto.class);

        assertEquals(expected, actual);
    }

    @Test
    @WithMockUser
    @DisplayName("""
            Verify addCartItem method works
            """)
    void addCartItem_ValidInput_ReturnsShoppingCartDto() throws Exception {
        //given
        CreateCartItemRequestDto dto = getTestCreateCartItemRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(dto);
        User user = getTestUser();
        CartItemDto first =
                new CartItemDto().setId(1L).setBookId(1L).setQuantity(1).setBookTitle("TestBook1");
        CartItemDto second =
                new CartItemDto().setId(2L).setBookId(2L).setQuantity(2).setBookTitle("TestBook2");
        ShoppingCartDto expected =
                new ShoppingCartDto().setId(1L).setUserId(1L).setCartItems(Set.of(first, second));

        //when
        MvcResult result = mockMvc.perform(post("/cart")
                        .with(user(user))
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //then
        ShoppingCartDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), ShoppingCartDto.class);
        assertEquals(expected, actual);
    }

    @Test
    @WithMockUser
    @DisplayName("""
            Verify updateCartItem method works
            """)
    void updateCartItem_ValidInput_ReturnsShoppingCartDto() throws Exception {
        //given
        Long id = 1L;
        UpdateCartItemRequestDto dto = new UpdateCartItemRequestDto().setQuantity(10);
        User user = getTestUser();
        String jsonRequest = objectMapper.writeValueAsString(dto);
        CartItemDto cartItemDto =
                new CartItemDto().setId(1L).setBookId(1L).setBookTitle("TestBook1").setQuantity(10);
        ShoppingCartDto expected =
                new ShoppingCartDto().setId(1L).setUserId(1L).setCartItems(Set.of(cartItemDto));

        //when
        MvcResult result = mockMvc.perform(put("/cart/cart-items/" + id)
                        .with(user(user))
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //then
        ShoppingCartDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), ShoppingCartDto.class);
        assertEquals(expected, actual);
    }

    @Test
    @WithMockUser
    @DisplayName("""
            Verify deleteCartItem method works
            """)
    void deleteCartItem_ValidInput_ReturnsShoppingCartDto() throws Exception {
        //given
        Long id = 1L;
        User user = getTestUser();
        //when
        mockMvc.perform(delete("/cart/cart-items/" + id)
                .with(user(user))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();
    }
}
