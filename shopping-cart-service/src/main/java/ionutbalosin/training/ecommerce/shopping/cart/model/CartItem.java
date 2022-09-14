package ionutbalosin.training.ecommerce.shopping.cart.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class CartItem {

  public static final String ID = "ID";

  public static final String CART_ID = "CART_ID";

  public static final String USER_ID = "USER_ID";
  public static final String PRODUCT_ID = "PRODUCT_ID";
  public static final String QUANTITY = "QUANTITY";
  public static final String DISCOUNT = "DISCOUNT";
  public static final String DAT_INS = "DAT_INS";
  public static final String DAT_UPD = "DAT_UPD";
  public static final String USR_INS = "USR_INS";
  public static final String USR_UPD = "USR_UPD";
  public static final String STAT = "STAT";

  private UUID id;
  private UUID cartId;
  private UUID userId;
  private UUID productId;
  private Integer quantity;

  private Float discount;
  private LocalDateTime dateIns;
  private LocalDateTime dateUpd;
  private String usrIns;
  private String usrUpd;
  private String stat;

  public UUID getId() {
    return id;
  }

  public CartItem id(UUID id) {
    this.id = id;
    return this;
  }

  public UUID getCartId() {
    return cartId;
  }

  public CartItem cartId(UUID cartId) {
    this.cartId = cartId;
    return this;
  }

  public UUID getUserId() {
    return userId;
  }

  public CartItem userId(UUID userId) {
    this.userId = userId;
    return this;
  }

  public UUID getProductId() {
    return productId;
  }

  public CartItem productId(UUID productId) {
    this.productId = productId;
    return this;
  }

  public Float getDiscount() {
    return discount;
  }

  public CartItem discount(Float discount) {
    this.discount = discount;
    return this;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public CartItem quantity(Integer quantity) {
    this.quantity = quantity;
    return this;
  }

  public LocalDateTime getDateIns() {
    return dateIns;
  }

  public CartItem dateIns(LocalDateTime dateIns) {
    this.dateIns = dateIns;
    return this;
  }

  public LocalDateTime getDateUpd() {
    return dateUpd;
  }

  public CartItem dateUpd(LocalDateTime dateUpd) {
    this.dateUpd = dateUpd;
    return this;
  }

  public String getUsrIns() {
    return usrIns;
  }

  public CartItem usrIns(String usrIns) {
    this.usrIns = usrIns;
    return this;
  }

  public String getUsrUpd() {
    return usrUpd;
  }

  public CartItem usrUpd(String usrUpd) {
    this.usrUpd = usrUpd;
    return this;
  }

  public String getStat() {
    return stat;
  }

  public CartItem stat(String stat) {
    this.stat = stat;
    return this;
  }
}
