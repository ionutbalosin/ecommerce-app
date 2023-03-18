/**
 *  eCommerce Application
 *
 *  Copyright (c) 2022 - 2023 Ionut Balosin
 *  Website: www.ionutbalosin.com
 *  Twitter: @ionutbalosin / Mastodon: ionutbalosin@mastodon.socia
 *
 *
 *  MIT License
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 *
 */
package ionutbalosin.training.ecommerce.shopping.cart.controller;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NOT_IMPLEMENTED;
import static org.springframework.http.HttpStatus.OK;

import ionutbalosin.training.ecommerce.shopping.cart.api.CartApi;
import ionutbalosin.training.ecommerce.shopping.cart.api.model.CartItemCreateDto;
import ionutbalosin.training.ecommerce.shopping.cart.api.model.CartItemDto;
import ionutbalosin.training.ecommerce.shopping.cart.api.model.CartItemUpdateDto;
import ionutbalosin.training.ecommerce.shopping.cart.dto.mapper.CartItemDtoMapper;
import ionutbalosin.training.ecommerce.shopping.cart.model.CartItem;
import ionutbalosin.training.ecommerce.shopping.cart.model.mapper.CartItemMapper;
import ionutbalosin.training.ecommerce.shopping.cart.service.CartService;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
public class CartController implements CartApi {

  private static final Logger LOGGER = LoggerFactory.getLogger(CartController.class);

  @Value("${max.cart.items.per.request:16}")
  private Integer maxCartItemsPerRequest;

  private final CartItemDtoMapper dtoMapper;
  private final CartItemMapper entityMapper;
  private final CartService cartService;

  public CartController(
      CartItemDtoMapper dtoMapper, CartItemMapper entityMapper, CartService cartService) {
    this.dtoMapper = dtoMapper;
    this.entityMapper = entityMapper;
    this.cartService = cartService;
  }

  @Override
  public ResponseEntity<Void> cartUserIdItemsPost(
      UUID userId, List<CartItemCreateDto> cartItemCreate) {
    LOGGER.debug(
        "cartUserIdItemsPost(userId = '{}', number of items = '{}')",
        userId,
        cartItemCreate.size());
    if (cartItemCreate.size() > maxCartItemsPerRequest) {
      LOGGER.warn(
          "User '{}' cannot add more than '{}' items per request", userId, maxCartItemsPerRequest);
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
    final Set<CartItem> cartItems =
        cartItemCreate.stream()
            .map(cartItem -> entityMapper.map(userId, cartItem))
            .collect(toSet());
    cartService.createCartItems(cartItems);
    return new ResponseEntity<>(CREATED);
  }

  @Override
  public ResponseEntity<List<CartItemDto>> cartUserIdItemsGet(UUID userId) {
    LOGGER.debug("cartUserIdItemsGet(userId = '{}')", userId);
    final List<CartItem> cartItems = cartService.getCartItems(userId);
    final List<CartItemDto> cartItemsDto = cartItems.stream().map(dtoMapper::map).collect(toList());
    return new ResponseEntity<>(cartItemsDto, OK);
  }

  @Override
  public ResponseEntity<Void> cartUserIdItemsDelete(UUID userId) {
    LOGGER.debug("cartUserIdItemsDelete(userId = '{}')", userId);
    cartService.deleteCartItems(userId);
    return new ResponseEntity<>(OK);
  }

  @Override
  public ResponseEntity<Void> cartUserIdItemsItemIdPut(
      UUID userId, UUID itemId, CartItemUpdateDto cartItemUpdate) {
    return new ResponseEntity<>(NOT_IMPLEMENTED);
  }

  @Override
  public ResponseEntity<CartItemCreateDto> cartUserIdItemsItemIdGet(UUID userId, UUID itemId) {
    return new ResponseEntity<>(NOT_IMPLEMENTED);
  }

  @Override
  public ResponseEntity<Void> cartUserIdItemsItemIdDelete(UUID userId, UUID itemId) {
    return new ResponseEntity<>(NOT_IMPLEMENTED);
  }

  @Override
  public ResponseEntity<Void> cartUserIdCheckoutPost(UUID userId) {
    LOGGER.debug("cartUserIdCheckoutPost(userId = '{}')", userId);
    final List<CartItem> cartItems = cartService.getCartItems(userId);
    if (cartItems.isEmpty()) {
      // shopping cart is empty, nothing to check out
      return new ResponseEntity<>(NOT_FOUND);
    }
    cartService.checkoutCartItems(userId, cartItems);
    return new ResponseEntity<>(ACCEPTED);
  }
}
