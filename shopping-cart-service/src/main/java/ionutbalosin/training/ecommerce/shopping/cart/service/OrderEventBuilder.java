package ionutbalosin.training.ecommerce.shopping.cart.service;

import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import ionutbalosin.training.ecommerce.message.schema.order.OrderCreatedEvent;
import ionutbalosin.training.ecommerce.message.schema.order.OrderCurrency;
import ionutbalosin.training.ecommerce.message.schema.order.ProductEvent;
import ionutbalosin.training.ecommerce.product.api.model.ProductDto;
import ionutbalosin.training.ecommerce.shopping.cart.dto.mapper.ProductEventMapper;
import ionutbalosin.training.ecommerce.shopping.cart.model.CartItem;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.stereotype.Service;

/*
 * (c) 2022 Ionut Balosin
 * Website: www.ionutbalosin.com
 * Twitter: @ionutbalosin
 *
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 */
@Service
public class OrderEventBuilder {

  private final ProductService productService;
  private final ProductEventMapper productEventMapper;

  public OrderEventBuilder(ProductService productService, ProductEventMapper productEventMapper) {
    this.productService = productService;
    this.productEventMapper = productEventMapper;
  }

  public OrderCreatedEvent createEvent(UUID userId, List<CartItem> cartItems) {
    final Map<UUID, CartItem> cartItemsMap = cartItemsAsMap(cartItems);
    final List<ProductDto> productDtos = productService.getProducts(cartItemsMap.keySet());
    final AtomicReference<Float> amountRef = new AtomicReference<>(0f);
    final AtomicReference<OrderCurrency> currencyRef = new AtomicReference<>();
    final List<ProductEvent> productEvents =
        createProductEvents(cartItemsMap, productDtos, amountRef, currencyRef);

    return createEvent(userId, productEvents, amountRef.get(), currencyRef.get());
  }

  private Map<UUID, CartItem> cartItemsAsMap(List<CartItem> cartItems) {
    return cartItems.stream().collect(toMap(CartItem::getProductId, cartItem -> cartItem));
  }

  private List<ProductEvent> createProductEvents(
      Map<UUID, CartItem> cartItems,
      List<ProductDto> productDtos,
      AtomicReference<Float> amountRef,
      AtomicReference<OrderCurrency> currencyRef) {

    return productDtos.stream()
        .map(
            productDto -> {
              final CartItem cartItem = cartItems.get(productDto.getProductId());
              final ProductEvent productEvent = productEventMapper.map(productDto, cartItem);
              float productAmount = amountRef.get() + getProductAmount(productEvent);
              amountRef.set(productAmount);
              currencyRef.compareAndSet(null, productEvent.getCurrency());
              return productEvent;
            })
        .collect(toList());
  }

  private float getProductAmount(ProductEvent productEvent) {
    return (productEvent.getDiscount() / 100)
        * productEvent.getPrice()
        * productEvent.getQuantity();
  }

  private OrderCreatedEvent createEvent(
      UUID userId, List<ProductEvent> productEvents, float amount, OrderCurrency currency) {
    final OrderCreatedEvent event = new OrderCreatedEvent();
    event.setId(randomUUID());
    event.setUserId(userId);
    event.setProducts(productEvents);
    event.setCurrency(currency);
    event.setAmount(amount);

    return event;
  }
}
