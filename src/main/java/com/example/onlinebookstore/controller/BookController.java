package com.example.onlinebookstore.controller;

import com.example.onlinebookstore.dto.book.BookDto;
import com.example.onlinebookstore.dto.book.BookSearchParameters;
import com.example.onlinebookstore.dto.book.CreateBookRequestDto;
import com.example.onlinebookstore.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Book management", description = "Endpoints for managing books")
@RestController
@RequestMapping(value = "/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Operation(summary = "Create a new book", description = "Create a new book")
    public BookDto createBook(Authentication authentication,
                              @RequestBody @Valid CreateBookRequestDto bookDto) {
        return bookService.save(bookDto);
    }

    @GetMapping
    @Operation(summary = "Get all books", description = "Get the catalogue of all books")
    @ApiResponse(responseCode = "200", description = "All books",
            content = {@Content(mediaType = "application/json")})
    public List<BookDto> getAll(Authentication authentication, Pageable pageable) {
        return bookService.findAll(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a book by id", description = "Get a book by id")
    @ApiResponse(responseCode = "200", description = "Book by id",
            content = {@Content(mediaType = "application/json")})
    public BookDto getBookById(Authentication authentication, @PathVariable Long id) {
        return bookService.findById(id);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a book by id", description = "Delete a book by id")
    public void deleteById(Authentication authentication, @PathVariable Long id) {
        bookService.deleteByID(id);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Update a book by id",
            description = "Update a book by id with given parameters")
    @ApiResponse(responseCode = "200", description = "Updated",
            content = {@Content(mediaType = "application/json")})
    public BookDto updateById(Authentication authentication, @PathVariable Long id,
                              @RequestBody @Valid CreateBookRequestDto bookDto) {
        return bookService.update(id, bookDto);
    }

    @GetMapping("/search")
    @Operation(summary = "Search a book by parameters",
            description = "Search a book by author or title")
    public List<BookDto> searchBooks(Authentication authentication,
                                     Pageable pageable,
                                     BookSearchParameters searchParameters) {
        return bookService.search(searchParameters);
    }
}
