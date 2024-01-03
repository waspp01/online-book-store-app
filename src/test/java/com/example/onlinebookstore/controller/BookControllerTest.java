package com.example.onlinebookstore.controller;

import static com.example.onlinebookstore.util.book.TestBookSupplier.getBookDtoFromCreateBookRequestDto;
import static com.example.onlinebookstore.util.book.TestBookSupplier.getTestBookDto;
import static com.example.onlinebookstore.util.book.TestBookSupplier.getTestCreateBookRequestDto;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.onlinebookstore.dto.book.BookDto;
import com.example.onlinebookstore.dto.book.CreateBookRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookControllerTest {
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
                    new ClassPathResource("database/books/insert-test-books.sql"));
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
                    new ClassPathResource("database/books/delete-all-test-books.sql"));
        }
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Sql(
            scripts = "classpath:database/books/delete-test-book.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("""
            Verify createBook method works
            """)
    void createBook_ValidCreateBookRequestDto_ReturnsBookDto() throws Exception {
        //given
        CreateBookRequestDto createBookRequestDto = getTestCreateBookRequestDto();
        BookDto expected = getBookDtoFromCreateBookRequestDto(createBookRequestDto);

        String jsonRequest = objectMapper.writeValueAsString(createBookRequestDto);

        //when
        MvcResult result = mockMvc.perform(post("/books")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        //then
        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.getId());
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @Test
    @WithMockUser
    @DisplayName("""
            Verify getAll method works
            """)
    void getAll_ReturnsAllBooks() throws Exception {
        //given
        List<BookDto> expected = List.of(
                new BookDto().setId(1L).setTitle("TestBook1").setAuthor("TestAuthor1")
                        .setIsbn("TestIsbn1").setPrice(BigDecimal.valueOf(9.99))
                        .setDescription("Test Description1").setCoverImage("TestCoverImage")
                        .setCategoryIds(new HashSet<>()),
                new BookDto().setId(2L).setTitle("TestBook2").setAuthor("TestAuthor2")
                        .setIsbn("TestIsbn2").setPrice(BigDecimal.valueOf(10.99))
                        .setDescription("Test Description2").setCoverImage("TestCoverImage")
                        .setCategoryIds(new HashSet<>()));
        //when
        MvcResult result = mockMvc.perform(get("/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //then
        BookDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), BookDto[].class);
        Assertions.assertEquals(2, actual.length);
        Assertions.assertEquals(expected, Arrays.stream(actual).toList());
    }

    @Test
    @WithMockUser
    @DisplayName("""
            Verify getBookById method works
            """)
    void getBookById_ValidId_ReturnsBookDto() throws Exception {
        //given
        Long id = 1L;
        BookDto expected = getTestBookDto();

        //when
        MvcResult result = mockMvc.perform(get("/books/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //then
        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Sql(
            scripts = "classpath:database/books/insert-book-for-delete-method.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @DisplayName("""
            Verify deleteById method works
            """)
    void deleteById_ValidId_Success() throws Exception {
        //when
        mockMvc.perform(delete("/books/3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        //then
        MvcResult result = mockMvc.perform(get("/books/3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Sql(
            scripts = "classpath:database/books/insert-book-for-update-method.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(
            scripts = "classpath:database/books/delete-book-after-update.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("""
            Verify updateById method works
            """)
    void updateById_ValidId_ReturnsBookDto() throws Exception {
        //given
        Long id = 5L;
        BookDto expected = new BookDto().setId(4L).setTitle("UpdatedTitle")
                .setAuthor("UpdatedAuthor1").setIsbn("UpdatedIsbn")
                .setPrice(BigDecimal.valueOf(1.99))
                .setDescription("Description1").setCoverImage("CoverImage1")
                .setCategoryIds(new HashSet<>());
        CreateBookRequestDto update = new CreateBookRequestDto()
                .setAuthor("UpdatedAuthor1").setTitle("UpdatedTitle")
                .setIsbn("UpdatedIsbn")
                .setPrice(BigDecimal.valueOf(1.99)).setDescription("Description1")
                .setCoverImage("CoverImage1").setCategoryIds(new HashSet<>());

        String jsonRequest = objectMapper.writeValueAsString(update);

        //when
        MvcResult result = mockMvc.perform(put("/books/" + id)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        MvcResult updatedAllBooksList = mockMvc.perform(get("/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //then
        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class);
        BookDto[] updatedList = objectMapper.readValue(
                updatedAllBooksList.getResponse().getContentAsByteArray(), BookDto[].class);

        EqualsBuilder.reflectionEquals(expected, actual, "id");
        EqualsBuilder.reflectionEquals(
                expected,
                Arrays.stream(updatedList).toList().get(2), "id");
    }

    @Test
    @WithMockUser
    @DisplayName("""
            Verify searchBooks method works
            """)
    void searchBooks_validInput_returnsListOfBookDto() throws Exception {
        //given
        List<BookDto> expected = List.of(getTestBookDto());

        //when
        MvcResult result = mockMvc.perform(get("/books/search?authors=TestAuthor1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //then
        BookDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), BookDto[].class);
        Assertions.assertEquals(expected, Arrays.stream(actual).toList());
    }
}
