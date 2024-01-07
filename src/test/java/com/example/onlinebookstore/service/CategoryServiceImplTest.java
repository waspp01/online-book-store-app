package com.example.onlinebookstore.service;

import static com.example.onlinebookstore.util.category.TestCategorySupplier.getCategoryDtoFromCategory;
import static com.example.onlinebookstore.util.category.TestCategorySupplier.getCategoryFromCreateCategoryDto;
import static com.example.onlinebookstore.util.category.TestCategorySupplier.getTestCategory;
import static com.example.onlinebookstore.util.category.TestCategorySupplier.getTestCreateCategoryRequestDto;

import com.example.onlinebookstore.dto.category.CategoryDto;
import com.example.onlinebookstore.dto.category.CreateCategoryRequestDto;
import com.example.onlinebookstore.mapper.CategoryMapper;
import com.example.onlinebookstore.model.Category;
import com.example.onlinebookstore.repository.category.CategoryRepository;
import com.example.onlinebookstore.service.impl.CategoryServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;
    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    @DisplayName("""
            Verify save() method works
            """)
    void save_ValidCreateCategoryRequestDto_ReturnsCategoryDto() {
        //given
        CreateCategoryRequestDto createCategoryRequestDto =
                getTestCreateCategoryRequestDto();
        Category category = getTestCategory();
        CategoryDto expected = getCategoryDtoFromCategory(category);

        Mockito.when(categoryMapper.toModel(createCategoryRequestDto)).thenReturn(category);
        Mockito.when(categoryRepository.save(category)).thenReturn(category);
        Mockito.when(categoryMapper.toDto(category)).thenReturn(expected);

        //when
        CategoryDto actual = categoryService.save(createCategoryRequestDto);

        //then
        Assertions.assertEquals(actual, expected);
        Mockito.verify(categoryRepository, Mockito.times(1)).save(category);
        Mockito.verify(categoryMapper, Mockito.times(1)).toDto(category);
        Mockito.verify(categoryMapper, Mockito.times(1)).toModel(createCategoryRequestDto);
        Mockito.verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("""
            Verify findAll() method works
            """)
    void findAll_ValidPageable_ReturnsListOfAllCategories() {
        //given
        Category category = getTestCategory();
        CategoryDto expected = getCategoryDtoFromCategory(category);
        Pageable pageable = PageRequest.of(0, 10);
        List<Category> categories = List.of(category);
        Page<Category> page = new PageImpl<>(categories, pageable, categories.size());

        Mockito.when(categoryMapper.toDto(category)).thenReturn(expected);
        Mockito.when(categoryRepository.findAll(pageable)).thenReturn(page);

        //when
        List<CategoryDto> actual = categoryService.findAll(pageable);

        //then
        Assertions.assertEquals(1, actual.size());
        Assertions.assertEquals(expected, actual.get(0));
        Mockito.verify(categoryRepository, Mockito.times(1)).findAll(pageable);
        Mockito.verify(categoryMapper, Mockito.times(1)).toDto(category);
        Mockito.verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("""
            Verify getById method works
            """)
    void getById_ValidId_ReturnsCategoryDto() {
        //given
        Category category = getTestCategory();
        CategoryDto expected = getCategoryDtoFromCategory(category);
        Mockito.when(categoryRepository.findById(category.getId()))
                .thenReturn(Optional.of(category));
        Mockito.when(categoryMapper.toDto(category)).thenReturn(expected);

        //when
        CategoryDto actual = categoryService.getById(category.getId());

        //then
        Assertions.assertEquals(expected, actual);
        Mockito.verify(categoryRepository, Mockito.times(1)).findById(Mockito.anyLong());
        Mockito.verify(categoryMapper, Mockito.times(1)).toDto(Mockito.any());
        Mockito.verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("""
            Verify getById method throws an Exception if not exist
            """)
    void getById_InvalidId_ThrowsException() {
        //given
        Long id = 100L;
        Mockito.when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        //when
        Exception exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> categoryService.getById(id));

        //then
        String expected = "Can't find category by id " + id;
        String actual = exception.getMessage();
        Assertions.assertEquals(expected, actual);
        Mockito.verify(categoryRepository, Mockito.times(1)).findById(Mockito.anyLong());
        Mockito.verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("""
            Verify update method works
            """)
    void update_ValidInput_ReturnsCategoryDto() {
        //given
        Long id = 1L;
        CreateCategoryRequestDto createCategoryRequestDto =
                getTestCreateCategoryRequestDto();
        Category category = getCategoryFromCreateCategoryDto(createCategoryRequestDto);
        category.setId(id);

        CategoryDto expected = getCategoryDtoFromCategory(category);

        Mockito.when(categoryRepository.save(category)).thenReturn(category);
        Mockito.when(categoryMapper.toDto(category)).thenReturn(expected);
        Mockito.when(categoryMapper.toModel(createCategoryRequestDto)).thenReturn(category);

        //when
        CategoryDto actual = categoryService.update(id, createCategoryRequestDto);

        //then
        Assertions.assertEquals(expected, actual);
        Mockito.verify(categoryRepository, Mockito.times(1)).save(category);
        Mockito.verify(categoryMapper, Mockito.times(1)).toDto(category);
        Mockito.verify(categoryMapper, Mockito.times(1)).toModel(createCategoryRequestDto);
        Mockito.verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("""
            Verify deleteByID method doesn't throw an exception
            """)
    void deleteById_ValidId_DoesNotThrowException() {
        Assertions.assertDoesNotThrow(() -> categoryService.deleteById(Mockito.anyLong()));
    }
}
