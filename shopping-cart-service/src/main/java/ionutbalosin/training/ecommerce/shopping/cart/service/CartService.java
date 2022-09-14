package ionutbalosin.training.ecommerce.shopping.cart.service;

import ionutbalosin.training.ecommerce.shopping.cart.dao.CartItemJdbcDao;
import ionutbalosin.training.ecommerce.shopping.cart.model.CartItem;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {

  private CartItemJdbcDao cartItemJdbcDao;

  public CartService(CartItemJdbcDao cartItemJdbcDao) {
    this.cartItemJdbcDao = cartItemJdbcDao;
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
}
