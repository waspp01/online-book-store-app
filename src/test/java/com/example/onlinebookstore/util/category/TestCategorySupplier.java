package com.example.onlinebookstore.util.category;

import com.example.onlinebookstore.dto.category.CategoryDto;
import com.example.onlinebookstore.dto.category.CreateCategoryRequestDto;
import com.example.onlinebookstore.model.Category;

public class TestCategorySupplier {
    public static Category getTestCategory() {
        return new Category()
                .setId(1L)
                .setName("Category1")
                .setDescription("Description1");
    }

    public static CreateCategoryRequestDto getTestCreateCategoryRequestDto() {
        CreateCategoryRequestDto createCategoryRequestDto =
                new CreateCategoryRequestDto();
        createCategoryRequestDto.setName("TestName1");
        createCategoryRequestDto.setDescription("TestDescription1");
        return createCategoryRequestDto;
    }

    public static CategoryDto getCategoryDtoFromCategory(Category category) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(category.getId());
        categoryDto.setName(category.getName());
        categoryDto.setDescription(category.getDescription());
        return categoryDto;
    }

    public static Category getCategoryFromCreateCategoryDto(
            CreateCategoryRequestDto createCategoryRequestDto
    ) {
        Category category = new Category();
        category.setName(createCategoryRequestDto.getName());
        category.setDescription(createCategoryRequestDto.getDescription());
        return category;
    }

    public static CategoryDto getCategoryDtoFromCreateCategoryRequestDto(
            CreateCategoryRequestDto requestDto
    ) {
        return new CategoryDto()
                .setName(requestDto.getName())
                .setDescription(requestDto.getDescription());
    }
}
