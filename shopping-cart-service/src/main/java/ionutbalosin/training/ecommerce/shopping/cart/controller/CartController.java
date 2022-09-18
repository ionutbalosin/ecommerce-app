package ionutbalosin.training.ecommerce.shopping.cart.controller;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import ionutbalosin.training.ecommerce.shopping.cart.api.CartApi;
import ionutbalosin.training.ecommerce.shopping.cart.api.model.CartItemCreateDto;
import ionutbalosin.training.ecommerce.shopping.cart.api.model.CartItemDto;
import ionutbalosin.training.ecommerce.shopping.cart.api.model.CartItemUpdateDto;
import ionutbalosin.training.ecommerce.shopping.cart.dto.mapper.CartItemDtoMapper;
import ionutbalosin.training.ecommerce.shopping.cart.model.CartItem;
import ionutbalosin.training.ecommerce.shopping.cart.model.mapper.CartItemMapper;
import ionutbalosin.training.ecommerce.shopping.cart.service.CartService;
import ionutbalosin.training.ecommerce.shopping.cart.service.KafkaEventProducer;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
public class CartController implements CartApi {

  @Value("${max.cart.items.per.request:16}")
  private Integer maxCartItemsPerRequest;

  private final CartItemDtoMapper dtoMapper;
  private final CartItemMapper entityMapper;
  private final CartService cartService;
  private final KafkaEventProducer kafkaEventProducer;

  public CartController(
      CartItemDtoMapper dtoMapper,
      CartItemMapper entityMapper,
      CartService cartService,
      KafkaEventProducer kafkaEventProducer) {
    this.dtoMapper = dtoMapper;
    this.entityMapper = entityMapper;
    this.cartService = cartService;
    this.kafkaEventProducer = kafkaEventProducer;
  }

  @Override
  public ResponseEntity<Void> cartUserIdItemsPost(
      UUID userId, List<CartItemCreateDto> cartItemCreateDto) {
    if (cartItemCreateDto.size() > maxCartItemsPerRequest) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
    final Set<CartItem> cartItems =
        cartItemCreateDto.stream()
            .map(cartItem -> entityMapper.map(userId, cartItem))
            .collect(toSet());
    cartService.createCartItems(cartItems);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @Override
  public ResponseEntity<List<CartItemDto>> cartUserIdItemsGet(UUID userId) {
    final List<CartItem> cartItems = cartService.getCartItems(userId);
    final List<CartItemDto> cartItemsDto = cartItems.stream().map(dtoMapper::map).collect(toList());
    return new ResponseEntity<>(cartItemsDto, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<Void> cartUserIdItemsDelete(UUID userId) {
    cartService.deleteCartItems(userId);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Override
  public ResponseEntity<Void> cartUserIdItemsItemIdPut(
      UUID userId, UUID itemId, CartItemUpdateDto cartItemUpdateDto) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @Override
  public ResponseEntity<CartItemCreateDto> cartUserIdItemsItemIdGet(UUID userId, UUID itemId) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @Override
  public ResponseEntity<Void> cartUserIdItemsItemIdDelete(UUID userId, UUID itemId) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @Override
  public ResponseEntity<Void> cartUserIdCheckoutPost(UUID userId) {
    final List<CartItem> cartItems = cartService.getCartItems(userId);
    if (cartItems.isEmpty()) {
      // shopping cart is empty, nothing to check out
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    cartService.checkoutCartItems(userId, cartItems);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }
}