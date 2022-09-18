package ionutbalosin.training.ecommerce.shopping.cart.service;

import static java.util.stream.Collectors.toList;

import ionutbalosin.training.ecommerce.event.schema.order.OrderCreatedEvent;
import ionutbalosin.training.ecommerce.event.schema.order.ProductEvent;
import ionutbalosin.training.ecommerce.product.api.model.ProductDto;
import ionutbalosin.training.ecommerce.shopping.cart.dao.CartItemJdbcDao;
import ionutbalosin.training.ecommerce.shopping.cart.dto.mapper.ProductEventMapper;
import ionutbalosin.training.ecommerce.shopping.cart.model.CartItem;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {

  private final CartItemJdbcDao cartItemJdbcDao;
  private final ProductService productService;
  private final ProductEventMapper productEventMapper;
  private final KafkaEventProducer kafkaEventProducer;

  public CartService(
      CartItemJdbcDao cartItemJdbcDao,
      ProductService productService,
      ProductEventMapper productEventMapper,
      KafkaEventProducer kafkaEventProducer) {
    this.cartItemJdbcDao = cartItemJdbcDao;
    this.productService = productService;
    this.productEventMapper = productEventMapper;
    this.kafkaEventProducer = kafkaEventProducer;
  }

  @Transactional
  public void createCartItems(Collection<CartItem> cartItem) {
    cartItemJdbcDao.saveAll(cartItem);
  }

  public int deleteCartItems(UUID userId) {
    return cartItemJdbcDao.deleteAll(userId);
  }

  public List<CartItem> getCartItems(UUID userId) {
    return cartItemJdbcDao.getAll(userId);
  }

  @Async
  public void checkoutCartItems(UUID userId, List<CartItem> cartItems) {
    final Map<UUID, CartItem> cartItemsAsMap =
        cartItems.stream().collect(Collectors.toMap(CartItem::getProductId, cartItem -> cartItem));

    final List<ProductDto> productDtos = productService.getProducts(cartItemsAsMap.keySet());
    final List<ProductEvent> productEvents =
        productDtos.stream()
            .map(
                productDto -> {
                  final CartItem cartItem = cartItemsAsMap.get(productDto.getProductId());
                  return productEventMapper.map(productDto, cartItem);
                })
            .collect(toList());

    final OrderCreatedEvent event = new OrderCreatedEvent();
    event.setUserId(userId);
    event.setProducts(productEvents);

    kafkaEventProducer.sendEvent(event);
  }
}
