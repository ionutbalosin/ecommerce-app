package ionutbalosin.training.ecommerce.order.model;

public enum OrderStatus {
  PAYMENT_INITIATED("PI"),
  PAYMENT_APPROVED("PA"),
  PAYMENT_FAILED("PF"),
  SHIPPING("S"),
  COMPLETED("CM"),
  CANCELLED("CN");

  private String value;

  OrderStatus(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public static OrderStatus fromValue(String value) {
    for (OrderStatus b : OrderStatus.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}
