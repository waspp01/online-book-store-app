package com.example.onlinebookstore.service;

import com.example.onlinebookstore.dto.BookDto;
import com.example.onlinebookstore.dto.BookSearchParameters;
import com.example.onlinebookstore.dto.CreateBookRequestDto;
import java.util.List;

public interface BookService {
    BookDto save(CreateBookRequestDto requestDto);

    List<BookDto> findAll();

    BookDto findById(Long id);

    void deleteByID(Long id);

    BookDto update(Long id, CreateBookRequestDto createBookRequestDto);

    List<BookDto> search(BookSearchParameters searchParameters);
}
