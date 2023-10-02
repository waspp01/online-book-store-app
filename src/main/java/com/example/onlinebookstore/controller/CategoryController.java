package com.example.onlinebookstore.controller;

import com.example.onlinebookstore.dto.book.BookDtoWithoutCategoryIds;
import com.example.onlinebookstore.dto.category.CategoryDto;
import com.example.onlinebookstore.dto.category.CreateCategoryRequestDto;
import com.example.onlinebookstore.service.BookService;
import com.example.onlinebookstore.service.CategoryService;
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

@Tag(name = "Category management", description = "Endpoints for managing categories")
@RestController
@RequestMapping(value = "/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final BookService bookService;

    @GetMapping
    @Operation(summary = "Get all categories", description = "Get a list of all categories")
    @ApiResponse(responseCode = "200", description = "All categories",
            content = {@Content(mediaType = "application/json")})
    public List<CategoryDto> getAll(Authentication authentication, Pageable pageable) {
        return categoryService.findAll();
    }

    @GetMapping("/{id}/books")
    @Operation(summary = "Get books by category", description = "Books by category")
    @ApiResponse(responseCode = "200", description = "Books by category",
            content = {@Content(mediaType = "application/json")})
    public List<BookDtoWithoutCategoryIds> getBookByCategory(Authentication authentication,
                                                             Pageable pageable,
                                                             @PathVariable Long id) {
        return bookService.findAllByCategoryId(id);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new category", description = "Create a new category")
    public CategoryDto createCategory(Authentication authentication,
                                      @RequestBody @Valid CreateCategoryRequestDto requestDto) {
        return categoryService.save(requestDto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a category by id", description = "Category by id")
    @ApiResponse(responseCode = "200", description = "Category by id",
            content = {@Content(mediaType = "application/json")})
    public CategoryDto getCategoryById(Authentication authentication, @PathVariable Long id) {
        return categoryService.getById(id);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Update a category by id", description = "Update a category by id")
    @ApiResponse(responseCode = "200", description = "Update a category",
            content = {@Content(mediaType = "application/json")})
    public CategoryDto updateCategory(Authentication authentication,
            @PathVariable Long id,
            @RequestBody CreateCategoryRequestDto categoryRequestDto) {
        return categoryService.update(id, categoryRequestDto);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a category by id", description = "Delete a category by id")
    public void deleteCategory(Authentication authentication,
                               @PathVariable Long id) {
        categoryService.deleteById(id);
    }
}
