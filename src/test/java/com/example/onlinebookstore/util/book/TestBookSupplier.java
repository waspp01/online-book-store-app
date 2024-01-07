package com.example.onlinebookstore.util.book;

import static com.example.onlinebookstore.util.category.TestCategorySupplier.getTestCategory;

import com.example.onlinebookstore.dto.book.BookDto;
import com.example.onlinebookstore.dto.book.BookDtoWithoutCategoryIds;
import com.example.onlinebookstore.dto.book.CreateBookRequestDto;
import com.example.onlinebookstore.model.Book;
import com.example.onlinebookstore.model.Category;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class TestBookSupplier {
    public static CreateBookRequestDto getTestCreateBookRequestDto() {
        return new CreateBookRequestDto()
                .setTitle("TestBook1")
                .setAuthor("TestAuthor1")
                .setIsbn("TestIsbn100")
                .setPrice(BigDecimal.valueOf(9.99))
                .setDescription("Test Description1")
                .setCoverImage("TestCoverImage")
                .setCategoryIds(Set.of(1L));
    }

    public static BookDto getBookDtoFromCreateBookRequestDto(CreateBookRequestDto requestDto) {
        return new BookDto()
                .setIsbn(requestDto.getIsbn())
                .setDescription(requestDto.getDescription())
                .setAuthor(requestDto.getAuthor())
                .setPrice(requestDto.getPrice())
                .setCoverImage(requestDto.getCoverImage())
                .setCategoryIds(requestDto.getCategoryIds())
                .setTitle(requestDto.getTitle());
    }

    public static Book getBookFromCreateBookRequestDto(
            CreateBookRequestDto createBookRequestDto) {
        return new Book()
                .setTitle(createBookRequestDto.getTitle())
                .setAuthor(createBookRequestDto.getAuthor())
                .setIsbn(createBookRequestDto.getIsbn())
                .setPrice(createBookRequestDto.getPrice())
                .setDescription(createBookRequestDto.getDescription())
                .setCoverImage(createBookRequestDto.getCoverImage())
                .setCategories(createBookRequestDto.getCategoryIds().stream()
                .map(Category::new)
                        .collect(Collectors.toSet()));
    }

    public static BookDto getBookDtoFromBook(Book book) {
        return new BookDto()
               .setTitle(book.getTitle())
               .setAuthor(book.getAuthor())
               .setIsbn(book.getIsbn())
               .setPrice(book.getPrice())
               .setDescription(book.getDescription())
               .setCoverImage(book.getCoverImage())
               .setCategoryIds(book.getCategories().stream()
                       .map(Category::getId)
                       .collect(Collectors.toSet()));
    }

    public static Book getTestBook() {
        return new Book()
                .setId(1L)
                .setTitle("TestBook1")
                .setAuthor("TestAuthor1")
                .setIsbn("TestIsbn1")
                .setPrice(BigDecimal.valueOf(9.99))
                .setDescription("Test Description1")
                .setCoverImage("TestCoverImage")
                .setCategories(Set.of(getTestCategory()));
    }

    public static BookDto getTestBookDto() {
        return new BookDto()
                .setId(1L)
                .setTitle("TestBook1")
                .setAuthor("TestAuthor1")
                .setIsbn("TestIsbn1")
                .setPrice(BigDecimal.valueOf(9.99))
                .setDescription("Test Description1")
                .setCoverImage("TestCoverImage")
                .setCategoryIds(new HashSet<>());
    }

    public static BookDtoWithoutCategoryIds getBookDtoWithoutCategoryIdsFromBook(Book book) {
        return new BookDtoWithoutCategoryIds()
                .setId(book.getId())
                .setAuthor(book.getAuthor())
                .setIsbn(book.getIsbn())
                .setTitle(book.getTitle())
                .setDescription(book.getDescription())
                .setCoverImage(book.getCoverImage())
                .setPrice(book.getPrice());
    }
}
