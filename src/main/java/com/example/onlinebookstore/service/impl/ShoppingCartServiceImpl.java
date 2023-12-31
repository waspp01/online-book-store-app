package com.example.onlinebookstore.service.impl;

import com.example.onlinebookstore.dto.cartitem.CreateCartItemRequestDto;
import com.example.onlinebookstore.dto.cartitem.UpdateCartItemRequestDto;
import com.example.onlinebookstore.dto.shoppingcart.ShoppingCartDto;
import com.example.onlinebookstore.mapper.CartItemMapper;
import com.example.onlinebookstore.mapper.ShoppingCartMapper;
import com.example.onlinebookstore.model.Book;
import com.example.onlinebookstore.model.CartItem;
import com.example.onlinebookstore.model.ShoppingCart;
import com.example.onlinebookstore.repository.book.BookRepository;
import com.example.onlinebookstore.repository.cartitem.CartItemRepository;
import com.example.onlinebookstore.repository.shoppingcart.ShoppingCartRepository;
import com.example.onlinebookstore.service.ShoppingCartService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository cartItemRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final CartItemMapper cartItemMapper;
    private final BookRepository bookRepository;

    @Override
    public ShoppingCartDto addCartItem(Long userId,
                                   CreateCartItemRequestDto requestDto) {
        ShoppingCart shoppingCart = getShoppingCartById(userId);

        duplicateCheck(shoppingCart, requestDto);

        Book book = bookRepository.findById(requestDto
                .getBookId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find the book by ID " + requestDto.getBookId()));

        CartItem cartItem = cartItemMapper.toModel(requestDto);
        cartItem.setBook(book);
        cartItem.setShoppingCart(shoppingCart);
        cartItemRepository.save(cartItem);

        List<CartItem> cartItems = shoppingCart.getCartItems();
        cartItems.add(cartItem);
        shoppingCart.setCartItems(cartItems);

        ShoppingCartDto shoppingCartDto = shoppingCartMapper.toDto(shoppingCart);
        return shoppingCartDto;
    }

    @Override
    public ShoppingCartDto getById(Long userId) {
        return shoppingCartMapper.toDto(getShoppingCartById(userId));
    }

    @Override
    public ShoppingCartDto updateById(Long userId,
                                      Long cartItemId,
                                      UpdateCartItemRequestDto requestDto) {
        CartItem cartItem = cartItemRepository
                .findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find a cart item by id " + cartItemId));
        cartItem.setQuantity(requestDto.getQuantity());
        cartItemRepository.save(cartItem);
        return shoppingCartMapper.toDto(getShoppingCartById(userId));
    }

    @Override
    public ShoppingCartDto deleteById(Long userId, Long caretItemId) {
        cartItemRepository.deleteById(caretItemId);
        return shoppingCartMapper.toDto(getShoppingCartById(userId));
    }

    @Override
    public void cleanShoppingCart(Long userId) {
        cartItemRepository.deleteAllByShoppingCartId(userId);
    }

    private ShoppingCart getShoppingCartById(Long userId) {
        return shoppingCartRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(
                        "Can't find shopping card by user userId " + userId));
    }

    private boolean duplicateCheck(ShoppingCart shoppingCart,
                                   CreateCartItemRequestDto requestDto) {
        List<CartItem> cartItems = shoppingCart.getCartItems();
        for (CartItem c : cartItems) {
            if (c.getBook().getId().equals(requestDto.getBookId())) {
                throw new RuntimeException(
                        "You already gave this book in your cart " + c.getBook().getTitle());
            }
        }
        return true;
    }
}
