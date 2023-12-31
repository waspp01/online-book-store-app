package com.example.onlinebookstore.service;

import static com.example.onlinebookstore.util.book.TestBookSupplier.getBookDtoFromBook;
import static com.example.onlinebookstore.util.book.TestBookSupplier.getBookDtoWithoutCategoryIdsFromBook;
import static com.example.onlinebookstore.util.book.TestBookSupplier.getBookFromCreateBookRequestDto;
import static com.example.onlinebookstore.util.book.TestBookSupplier.getTestBook;
import static com.example.onlinebookstore.util.book.TestBookSupplier.getTestCreateBookRequestDto;
import static com.example.onlinebookstore.util.category.TestCategorySupplier.getTestCategory;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.example.onlinebookstore.dto.book.BookDto;
import com.example.onlinebookstore.dto.book.BookDtoWithoutCategoryIds;
import com.example.onlinebookstore.dto.book.BookSearchParameters;
import com.example.onlinebookstore.dto.book.CreateBookRequestDto;
import com.example.onlinebookstore.mapper.BookMapper;
import com.example.onlinebookstore.model.Book;
import com.example.onlinebookstore.model.Category;
import com.example.onlinebookstore.repository.book.BookRepository;
import com.example.onlinebookstore.repository.book.BookSpecificationBuilder;
import com.example.onlinebookstore.service.impl.BookServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
public class BookServiceImplTest {
    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookMapper bookMapper;
    @Mock
    private BookSpecificationBuilder bookSpecificationBuilder;
    @InjectMocks
    private BookServiceImpl bookServiceImpl;

    @Test
    @DisplayName("""
            Verify save() method works
            """)
    void save_ValidCreateBookRequestDto_ReturnsBookDto() {
        //given
        CreateBookRequestDto createBookRequestDto = getTestCreateBookRequestDto();
        Book book = getBookFromCreateBookRequestDto(createBookRequestDto);
        BookDto expected = getBookDtoFromBook(book);
        expected.setId(1L);

        when(bookMapper.toModel(createBookRequestDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(expected);

        //when
        BookDto actual = bookServiceImpl.save(createBookRequestDto);

        //then
        assertEquals(expected, actual);
        verify(bookRepository, times(1)).save(book);
        verify(bookMapper, times(1)).toDto(book);
        verify(bookMapper, times(1)).toModel(createBookRequestDto);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("""
            Verify findAll() method works
            """)
    void findAll_ValidPageable_ReturnsListOfAllBooks() {
        //given
        Book book = getTestBook();
        BookDto expected = getBookDtoFromBook(book);
        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = List.of(book);
        Page<Book> bookPage = new PageImpl<>(books, pageable, books.size());
        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(book)).thenReturn(expected);

        //when
        List<BookDto> actual = bookServiceImpl.findAll(pageable);

        //then
        assertEquals(actual.get(0), expected);
        assertEquals(1, actual.size());
        verify(bookRepository, times(1)).findAll(pageable);
        verify(bookMapper, times(1)).toDto(book);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("""
            Verify findByIdl() method returns needed BookDTO if exists
            """)
    void findById_ValidId_ReturnsBookDto() {
        //given
        Book book = getTestBook();
        BookDto expected = getBookDtoFromBook(book);
        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(expected);

        //when
        BookDto actual = bookServiceImpl.findById(book.getId());

        //then
        assertEquals(expected, actual);
        verify(bookRepository, times(1)).findById(anyLong());
        verify(bookMapper, times(1)).toDto(book);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("""
            Verify findByIdl() method throws exception if id don't exist
            """)
    void findById_InvalidId_ThrowsException() {
        //given
        Long id = 100L;
        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        //when
        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> bookServiceImpl.findById(id));

        //then
        String expected = "Can't find the book by id " + id;
        String actual = exception.getMessage();
        assertEquals(expected, actual);
        verify(bookRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("""
            Verify deleteByID method doesn't throw an exception
            """)
    void deleteByID_ValidId_DoesNotThrowException() {
        assertDoesNotThrow(() -> bookServiceImpl.deleteByID(anyLong()));
    }

    @Test
    @DisplayName("""
            Verify update method returns valid BookDto
            """)
    void update_ValidInput_ReturnsBookDto() {
        //given
        Long id = 1L;
        CreateBookRequestDto createBookRequestDto = getTestCreateBookRequestDto();
        Book book = getBookFromCreateBookRequestDto(createBookRequestDto);
        book.setId(id);

        BookDto expected = getBookDtoFromBook(book);

        when(bookMapper.toModel(createBookRequestDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(expected);

        //when
        BookDto actual = bookServiceImpl.update(id, createBookRequestDto);

        //then
        assertEquals(expected, actual);
        verify(bookRepository, times(1)).save(book);
        verify(bookMapper, times(1)).toDto(book);
        verify(bookMapper, times(1)).toModel(createBookRequestDto);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("""
            Verify search method works
            """)
    void search_ValidInput_ReturnsListOfBooks() {
        //given
        Book book = getTestBook();
        BookDto expected = getBookDtoFromBook(book);
        Specification<Book> specification = Specification.where(null);
        BookSearchParameters bookSearchParameters =
                new BookSearchParameters(new String[0], new String[0]);

        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = List.of(book);
        Page<Book> bookPage = new PageImpl<>(books, pageable, books.size());

        when(bookSpecificationBuilder.build(bookSearchParameters)).thenReturn(specification);
        when(bookRepository.findAll(specification, pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(book)).thenReturn(expected);

        //when
        List<BookDto> actual = bookServiceImpl.search(bookSearchParameters, pageable);

        //then
        assertEquals(1, actual.size());
        assertEquals(expected, actual.get(0));
        verify(bookRepository, times(1)).findAll(specification, pageable);
        verify(bookMapper, times(1)).toDto(book);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("""
            Verify findAllByCategoriesId method works
            """)
    void findAllByCategoriesId_ValidInput_ReturnsListBookDtoWithoutCategoryIds() {
        //given
        Category category = getTestCategory();
        Book book = getTestBook();
        BookDtoWithoutCategoryIds expected = getBookDtoWithoutCategoryIdsFromBook(book);
        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = List.of(book);
        Page<Book> bookPage = new PageImpl<>(books, pageable, books.size());

        when(bookRepository.findAllByCategoriesId(book.getId(), pageable)).thenReturn(books);
        when(bookMapper.toDtoWithoutCategories(book)).thenReturn(expected);

        //when
        List<BookDtoWithoutCategoryIds> actual =
                bookServiceImpl.findAllByCategoriesId(book.getId(), pageable);

        //then
        assertEquals(1, actual.size());
        assertEquals(expected, actual.get(0));
        verify(bookRepository, times(1))
                .findAllByCategoriesId(book.getId(), pageable);
        verify(bookMapper, times(1))
                .toDtoWithoutCategories(book);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }
}
