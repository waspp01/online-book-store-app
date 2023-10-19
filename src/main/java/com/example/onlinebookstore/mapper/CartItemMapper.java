package com.example.onlinebookstore.mapper;

import com.example.onlinebookstore.config.MapperConfig;
import com.example.onlinebookstore.dto.cartitem.CartItemDto;
import com.example.onlinebookstore.dto.cartitem.CreateCartItemRequestDto;
import com.example.onlinebookstore.model.Book;
import com.example.onlinebookstore.model.CartItem;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface CartItemMapper {

    @Mapping(target = "book", ignore = true)
    CartItem toModel(CreateCartItemRequestDto requestDto);

    @Mapping(target = "bookId", source = "book.id")
    @Mapping(target = "bookTitle", source = "book.title")
    CartItemDto toDto(CartItem cartItem);

    @AfterMapping
    default void setBooks(@MappingTarget CartItem cartItem, CreateCartItemRequestDto cartItemDto) {
        Book book = new Book();
        book.setId(cartItemDto.getBookId());
        cartItem.setBook(book);
    }
}
