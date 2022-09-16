package ionutbalosin.training.ecommerce.order.controller;

import ionutbalosin.training.ecommerce.order.api.OrdersApi;
import ionutbalosin.training.ecommerce.order.api.model.OrderDto;
import ionutbalosin.training.ecommerce.order.service.producer.OrderProducer;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
public class OrderController implements OrdersApi {

  private OrderProducer orderProducer;

  public OrderController(OrderProducer orderProducer) {
    this.orderProducer = orderProducer;
  }

  @Override
  public ResponseEntity<List<OrderDto>> ordersGet() {
    orderProducer.produce();
    return new ResponseEntity<>(List.of(), HttpStatus.OK);
  }
}
