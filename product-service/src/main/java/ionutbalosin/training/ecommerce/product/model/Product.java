package ionutbalosin.training.ecommerce.product.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Product {

  public static final String ID = "ID";
  public static final String NAME = "NAME";
  public static final String BRAND = "BRAND";
  public static final String CATEGORY = "CATEGORY";
  public static final String PRICE = "PRICE";
  public static final String CURRENCY = "CURRENCY";
  public static final String QUANTITY = "QUANTITY";
  public static final String DAT_INS = "DAT_INS";
  public static final String DAT_UPD = "DAT_UPD";
  public static final String USR_INS = "USR_INS";
  public static final String USR_UPD = "USR_UPD";
  public static final String STAT = "STAT";

  private UUID id;
  private String name;
  private String brand;
  private String category;
  private Float price;
  private String currency;
  private Integer quantity;
  private LocalDateTime dateIns;
  private LocalDateTime dateUpd;
  private String usrIns;
  private String usrUpd;
  private String stat;

  public UUID getId() {
    return id;
  }

  public Product id(UUID productId) {
    this.id = productId;
    return this;
  }

  public String getName() {
    return name;
  }

  public Product name(String name) {
    this.name = name;
    return this;
  }

  public String getBrand() {
    return brand;
  }

  public Product brand(String brand) {
    this.brand = brand;
    return this;
  }

  public String getCategory() {
    return category;
  }

  public Product category(String category) {
    this.category = category;
    return this;
  }

  public Float getPrice() {
    return price;
  }

  public Product price(Float price) {
    this.price = price;
    return this;
  }

  public String getCurrency() {
    return currency;
  }

  public Product currency(String currency) {
    this.currency = currency;
    return this;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public Product quantity(Integer quantity) {
    this.quantity = quantity;
    return this;
  }

  public LocalDateTime getDateIns() {
    return dateIns;
  }

  public Product dateIns(LocalDateTime dateIns) {
    this.dateIns = dateIns;
    return this;
  }

  public LocalDateTime getDateUpd() {
    return dateUpd;
  }

  public Product dateUpd(LocalDateTime dateUpd) {
    this.dateUpd = dateUpd;
    return this;
  }

  public String getUsrIns() {
    return usrIns;
  }

  public Product usrIns(String usrIns) {
    this.usrIns = usrIns;
    return this;
  }

  public String getUsrUpd() {
    return usrUpd;
  }

  public Product usrUpd(String usrUpd) {
    this.usrUpd = usrUpd;
    return this;
  }

  public String getStat() {
    return stat;
  }

  public Product stat(String stat) {
    this.stat = stat;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Product product)) return false;
    return name.equals(product.name)
        && brand.equals(product.brand)
        && category.equals(product.category);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, brand, category);
  }
}
