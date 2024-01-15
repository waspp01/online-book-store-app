package com.example.onlinebookstore.service;

import static com.example.onlinebookstore.util.shoppingcart.TestShoppingCartSupplier.getShoppingCartDtoFromShoppingCart;
import static com.example.onlinebookstore.util.shoppingcart.TestShoppingCartSupplier.getTestShoppingCart;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.example.onlinebookstore.dto.cartitem.CreateCartItemRequestDto;
import com.example.onlinebookstore.dto.cartitem.UpdateCartItemRequestDto;
import com.example.onlinebookstore.dto.shoppingcart.ShoppingCartDto;
import com.example.onlinebookstore.mapper.CartItemMapper;
import com.example.onlinebookstore.mapper.ShoppingCartMapper;
import com.example.onlinebookstore.model.Book;
import com.example.onlinebookstore.model.CartItem;
import com.example.onlinebookstore.model.ShoppingCart;
import com.example.onlinebookstore.model.User;
import com.example.onlinebookstore.repository.book.BookRepository;
import com.example.onlinebookstore.repository.cartitem.CartItemRepository;
import com.example.onlinebookstore.repository.shoppingcart.ShoppingCartRepository;
import com.example.onlinebookstore.service.impl.ShoppingCartServiceImpl;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ShoppingCartServiceImplTest {
    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private ShoppingCartMapper shoppingCartMapper;
    @Mock
    private CartItemMapper cartItemMapper;
    @Mock
    private BookRepository bookRepository;
    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;

    @Test
    @DisplayName("""
            Verify addCartItem method returns valid ShoppingCartDto
            """)
    void addCartItem_ValidInput_ReturnsShoppingCartDto() {
        //given
        Long id = 1L;
        ShoppingCart shoppingCart = getTestShoppingCart();
        CartItem cartItem = shoppingCart.getCartItems().get(0);
        Book book = cartItem.getBook();
        ShoppingCartDto expected = getShoppingCartDtoFromShoppingCart(shoppingCart);
        CreateCartItemRequestDto requestDto =
                new CreateCartItemRequestDto().setBookId(2L).setQuantity(1);
        when(shoppingCartRepository.findById(anyLong())).thenReturn(Optional.of(shoppingCart));
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        when(cartItemMapper.toModel(any())).thenReturn(cartItem);
        when(shoppingCartMapper.toDto(any())).thenReturn(expected);

        //when
        ShoppingCartDto actual = shoppingCartService.addCartItem(id, requestDto);

        //then
        assertEquals(expected, actual);
        verify(shoppingCartRepository, times(1)).findById(anyLong());
        verify(bookRepository, times(1)).findById(anyLong());
        verify(cartItemMapper, times(1)).toModel(any());
        verify(shoppingCartMapper, times(1)).toDto(any());
        verifyNoMoreInteractions(
                shoppingCartRepository, bookRepository, cartItemMapper, shoppingCartMapper);
    }

    @Test
    @DisplayName("""
            Verify getById method returns valid ShoppingCartDto
            """)
    void getById_ValidId_ReturnsShoppingCartDto() {
        //given
        Long id = 1L;
        User user = new User().setId(id);
        ShoppingCart shoppingCart = getTestShoppingCart();
        ShoppingCartDto expected = getShoppingCartDtoFromShoppingCart(shoppingCart);
        when(shoppingCartRepository.findById(anyLong())).thenReturn(Optional.of(shoppingCart));
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expected);
        //when
        ShoppingCartDto actual = shoppingCartService.getById(id);
        //then
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Verify updateById method returns valid ShoppingCartDto
            """)
    void updateById_ValidInput_ReturnsShoppingCartDto() {
        //given
        Long id = 1L;
        Long cartItemId = 1L;
        UpdateCartItemRequestDto requestDto = new UpdateCartItemRequestDto().setQuantity(10);
        CartItem cartItem = new CartItem().setId(cartItemId).setQuantity(1);
        ShoppingCartDto expected = new ShoppingCartDto().setId(id).setUserId(id);
        when(cartItemRepository.findById(id)).thenReturn(Optional.ofNullable(cartItem));
        when(shoppingCartRepository.findById(id)).thenReturn(Optional.of(new ShoppingCart()));
        when(shoppingCartMapper.toDto(any())).thenReturn(expected);
        //when
        ShoppingCartDto actual = shoppingCartService.updateById(id, cartItemId, requestDto);
        //then
        assertEquals(expected,actual);
        verify(cartItemRepository, times(1)).findById(anyLong());
        verify(cartItemRepository, times(1)).save(any());
        verify(shoppingCartRepository, times(1)).findById(anyLong());
        verify(shoppingCartMapper, times(1)).toDto(any());
        verifyNoMoreInteractions(cartItemRepository, shoppingCartRepository, shoppingCartMapper);
    }

    @Test
    @DisplayName("""
            Verify deleteById method returns ShoppingCartDto
            """)
    void deleteById_ValidId_ReturnsShoppingCartDto() {
        //given
        Long userId = 1L;
        Long cartItemId = 1L;
        ShoppingCart shoppingCart = getTestShoppingCart();
        ShoppingCartDto expected = getShoppingCartDtoFromShoppingCart(shoppingCart);
        when(shoppingCartRepository.findById(userId)).thenReturn(Optional.of(shoppingCart));
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expected);

        //when
        ShoppingCartDto actual = shoppingCartService.deleteById(userId, cartItemId);

        //then
        assertEquals(expected, actual);
        verify(shoppingCartRepository, times(1)).findById(anyLong());
        verify(shoppingCartMapper, times(1)).toDto(any());
        verifyNoMoreInteractions(shoppingCartRepository, shoppingCartMapper);
    }

    @Test
    @DisplayName("""
            Verify cleanShoppingCart method works
            """)
    void cleanShoppingCart_ValidUserId_DoesNotThrowException() {
        assertDoesNotThrow(() -> shoppingCartService.cleanShoppingCart(anyLong()));
    }
}
