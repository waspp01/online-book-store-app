package com.example.onlinebookstore.util.shoppingcart;

import com.example.onlinebookstore.dto.cartitem.CartItemDto;
import com.example.onlinebookstore.dto.shoppingcart.ShoppingCartDto;
import com.example.onlinebookstore.model.Book;
import com.example.onlinebookstore.model.CartItem;
import com.example.onlinebookstore.model.ShoppingCart;
import com.example.onlinebookstore.model.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TestShoppingCartSupplier {

    public static ShoppingCart getTestShoppingCart() {
        User user = new User().setId(1L);
        Book book = new Book().setId(1L);
        CartItem cartItem = new CartItem().setBook(book).setId(1L).setQuantity(1);
        ShoppingCart shoppingCart = new ShoppingCart().setUser(user).setId(user.getId());
        cartItem.setShoppingCart(shoppingCart);
        List<CartItem> cartItems = new ArrayList<>();
        cartItems.add(cartItem);
        shoppingCart.setCartItems(cartItems);
        return shoppingCart;
    }

    public static ShoppingCartDto getTestShoppingCartDto() {
        CartItemDto dto = new CartItemDto()
                .setId(1L).setBookId(1L).setBookTitle("TestBook1").setQuantity(1);
        return new ShoppingCartDto().setId(1L).setUserId(1L).setCartItems(Set.of(dto));
    }

    public static ShoppingCartDto getShoppingCartDtoFromShoppingCart(ShoppingCart shoppingCart) {
        return new ShoppingCartDto()
                .setId(shoppingCart.getId())
                .setUserId(shoppingCart.getUser().getId());
    }
}
