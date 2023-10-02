package com.example.onlinebookstore.service;

import com.example.onlinebookstore.dto.book.BookDto;
import com.example.onlinebookstore.dto.book.BookDtoWithoutCategoryIds;
import com.example.onlinebookstore.dto.book.BookSearchParameters;
import com.example.onlinebookstore.dto.book.CreateBookRequestDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookDto save(CreateBookRequestDto requestDto);

    List<BookDto> findAll(Pageable pageable);

    BookDto findById(Long id);

    void deleteByID(Long id);

    BookDto update(Long id, CreateBookRequestDto createBookRequestDto);

    List<BookDto> search(BookSearchParameters searchParameters);

    List<BookDtoWithoutCategoryIds> findAllByCategoryId(Long id);
}
