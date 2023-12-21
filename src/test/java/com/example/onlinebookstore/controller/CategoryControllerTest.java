package com.example.onlinebookstore.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import com.example.onlinebookstore.dto.book.BookDtoWithoutCategoryIds;
import com.example.onlinebookstore.dto.category.CategoryDto;
import com.example.onlinebookstore.dto.category.CreateCategoryRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CategoryControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext applicationContext
    ) throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        teardown(dataSource);
    }

    @BeforeEach
    void setUp(
            @Autowired DataSource dataSource
    ) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/categories/insert-general-test-categories.sql")
            );
        }
    }

    @AfterEach
    void tearDown(
            @Autowired DataSource dataSource
    ) {
        teardown(dataSource);
    }

    @AfterAll
    static void afterAll(
            @Autowired DataSource dataSource
    ) {
        teardown(dataSource);
    }

    @SneakyThrows
    private static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/categories/delete-all-test-categories.sql")
            );

        }
    }

    @Test
    @WithMockUser
    @DisplayName("""
            Verify getAll method works
            """)
    void getAll_ReturnsCategoryDtoList() throws Exception {
        //given
        List<CategoryDto> expected = List.of(
                new CategoryDto().setId(1L).setName("Category1").setDescription("Description1"),
                new CategoryDto().setId(2L).setName("Category2").setDescription("Description2")
        );

        //when
        MvcResult result = mockMvc.perform(get("/categories")
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        //then
        CategoryDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(),
                CategoryDto[].class
        );
        Assertions.assertEquals(expected, Arrays.stream(actual).toList());
    }

    @Test
    @WithMockUser
    @Sql(
            scripts = {
                    "classpath:database/books-categories/insert-books-categories.sql",
                    "classpath:database/books/insert-test-books.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @DisplayName("""
            Verify getBookByCategory method works
            """)
    void getBookByCategory_ReturnsBookDtoWithoutCategoryIdsList() throws Exception {
        //given
        Long id = 2L;
        List<BookDtoWithoutCategoryIds> expected = List.of(
                new BookDtoWithoutCategoryIds()
                        .setId(id)
                        .setTitle("Title2")
                        .setAuthor("Author2")
                        .setIsbn("isbn2")
                        .setPrice(BigDecimal.valueOf(10.99))
                        .setDescription("Description2")
                        .setCoverImage("CoverImage2")
        );

        //when
        MvcResult result = mockMvc.perform(get("/categories/" + id + "/books")
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        //then
        BookDtoWithoutCategoryIds[] actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(),
                BookDtoWithoutCategoryIds[].class
        );
        Assertions.assertEquals(expected, Arrays.stream(actual).toList());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("""
            Verify createCategory method works
            """)
    void createCategory_ValidCreateCategoryRequestDto_ReturnsCategoryDto() throws Exception {
        //given
        CreateCategoryRequestDto dto = new CreateCategoryRequestDto()
                .setName("NewCreatedCategory")
                .setDescription("NewDescription");
        CategoryDto expected = new CategoryDto()
                .setId(3L)
                .setName("NewCreatedCategory")
                .setDescription("NewDescription");
        String jsonRequest = objectMapper.writeValueAsString(dto);

        //when
        MvcResult result = mockMvc.perform(post("/categories")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        //then
        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CategoryDto.class
        );
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @WithMockUser
    @DisplayName("""
            Verify getCategoryById method works
            """)
    void getCategoryById_ValidId_ReturnsCategoryDto() throws Exception {
        //given
        Long id = 2L;
        CategoryDto expected = new CategoryDto()
                .setId(id).setName("Category2").setDescription("Description2");

        //when
        MvcResult result = mockMvc.perform(get("/categories/" + id)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        //then
        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CategoryDto.class
        );
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("""
            Verify updateCategory method works
            """)
    void updateCategory_ValidInput_ReturnsCategoryDto() throws Exception {
        //given
        Long id = 2L;
        CreateCategoryRequestDto dto = new CreateCategoryRequestDto()
                .setName("Updated Name")
                .setDescription("Updated Description");
        CategoryDto expected = new CategoryDto()
                .setId(id)
                .setName(dto.getName())
                .setDescription(dto.getDescription());
        String jsonRequest = objectMapper.writeValueAsString(dto);

        //when
        MvcResult result = mockMvc.perform(put("/categories/" + id)
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        //then
        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CategoryDto.class
        );
        Assertions.assertEquals(expected,actual);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("""
            Verify deleteCategory method works
            """)
    void deleteCategory_validId_Success() throws Exception {
        //given
        Long id = 2L;

        //when
        MvcResult deletion = mockMvc.perform(delete("/categories/" + id)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andReturn();

        //then
        MvcResult actual = mockMvc.perform(get("/categories/" + id)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andReturn();
    }
}
