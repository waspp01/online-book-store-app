package com.example.onlinebookstore.controller;

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
import java.util.Set;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
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
            @Autowired WebApplicationContext applicationContext
    ) throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/books/insert-test-books.sql")
            );
        }
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
                    new ClassPathResource("database/books/delete-all-test-books.sql")
            );

        }
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Sql(
            scripts = "classpath:database/books/delete-test-book.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @DisplayName("""
            Verify createBook method works
            """)
    void createBook_ValidCreateBookRequestDto_ReturnsBookDto() throws Exception {
        //given
        CreateBookRequestDto createBookRequestDto = new CreateBookRequestDto()
                .setTitle("TestTitle")
                .setAuthor("TestAuthor")
                .setIsbn("TestIsbn")
                .setCategoryIds(Set.of(1L))
                .setPrice(BigDecimal.TEN)
                .setDescription("Test Description")
                .setCoverImage("test cover");

        BookDto expected = new BookDto()
                .setIsbn(createBookRequestDto.getIsbn())
                .setDescription(createBookRequestDto.getDescription())
                .setAuthor(createBookRequestDto.getAuthor())
                .setPrice(createBookRequestDto.getPrice())
                .setCoverImage(createBookRequestDto.getCoverImage())
                .setCategoryIds(createBookRequestDto.getCategoryIds())
                .setTitle(createBookRequestDto.getTitle());
        String jsonRequest = objectMapper.writeValueAsString(createBookRequestDto);

        //when
        MvcResult result = mockMvc.perform(post("/books")
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isCreated())
                .andReturn();

        //then
        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class
        );
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
                new BookDto().setId(1L).setTitle("Title1").setAuthor("Author1")
                        .setIsbn("isbn1").setPrice(BigDecimal.valueOf(1.99))
                        .setDescription("Description1").setCoverImage("CoverImage1")
                        .setCategoryIds(new HashSet<>()),
                new BookDto().setId(2L).setTitle("Title2").setAuthor("Author2")
                        .setIsbn("isbn2").setPrice(BigDecimal.valueOf(10.99))
                        .setDescription("Description2").setCoverImage("CoverImage2")
                        .setCategoryIds(new HashSet<>()));
        //when
        MvcResult result = mockMvc.perform(get("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                )
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
        BookDto expected = new BookDto().setId(1L).setTitle("Title1").setAuthor("Author1")
                .setIsbn("isbn1").setPrice(BigDecimal.valueOf(1.99))
                .setDescription("Description1").setCoverImage("CoverImage1")
                .setCategoryIds(new HashSet<>());

        //when
        MvcResult result = mockMvc.perform(get("/books/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        //then
        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class
        );
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Sql(
            scripts = "classpath:database/books/insert-book-for-delete-method.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @DisplayName("""
            Verify deleteById method works
            """)
    void deleteById_ValidId_Success() throws Exception {
        //when
        mockMvc.perform(delete("/books/3")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent());

        //then
        MvcResult result = mockMvc.perform(get("/books/3")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is4xxClientError())
                .andReturn();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Sql(
            scripts = "classpath:database/books/insert-book-for-update-method.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/books/delete-book-after-update.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
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
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        MvcResult updatedAllBooksList = mockMvc.perform(get("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                )
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
                Arrays.stream(updatedList).toList().get(2),
                "id"
        );
    }

    @Test
    @WithMockUser
    @DisplayName("""
            Verify searchBooks method works
            """)
    void searchBooks_validInput_returnsListOfBookDto() throws Exception {
        //given
        List<BookDto> expected = List.of(
                new BookDto().setId(1L).setTitle("Title1").setAuthor("Author1")
                        .setIsbn("isbn1").setPrice(BigDecimal.valueOf(1.99))
                        .setDescription("Description1").setCoverImage("CoverImage1")
                        .setCategoryIds(new HashSet<>()));

        //when
        MvcResult result = mockMvc.perform(get("/books/search?authors=Author1")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        //then
        BookDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), BookDto[].class);
        Assertions.assertEquals(expected, Arrays.stream(actual).toList());
    }
}
