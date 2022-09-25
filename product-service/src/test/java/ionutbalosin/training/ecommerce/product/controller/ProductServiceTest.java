package ionutbalosin.training.ecommerce.product.controller;

import static ionutbalosin.training.ecommerce.product.service.ProductService.BULK_HEAD_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import ionutbalosin.training.ecommerce.product.dao.ProductJdbcDao;
import ionutbalosin.training.ecommerce.product.service.ProductService;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/*
 * (c) 2022 Ionut Balosin
 * Website: www.ionutbalosin.com
 * Twitter: @ionutbalosin
 *
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest()
public class ProductServiceTest {

  @Autowired private ProductService classUnderTest;
  @Autowired protected BulkheadRegistry bulkheadRegistry;
  @MockBean private ProductJdbcDao productJdbcDao;

  @Test
  public void getProducts() throws InterruptedException {
    final Bulkhead bulkhead = bulkheadRegistry.bulkhead(BULK_HEAD_NAME);
    final AtomicInteger finished = new AtomicInteger(0);
    final AtomicInteger permitted = new AtomicInteger(0);
    final AtomicInteger rejected = new AtomicInteger(0);

    bulkhead.getEventPublisher().onCallFinished(event -> finished.incrementAndGet());
    bulkhead.getEventPublisher().onCallPermitted(event -> permitted.incrementAndGet());
    bulkhead.getEventPublisher().onCallRejected(event -> rejected.incrementAndGet());

    final Thread thread = new Thread(() -> classUnderTest.getProducts(List.of()));
    thread.start();
    thread.join();

    assertEquals(1, finished.get());
    assertEquals(1, permitted.get());
    assertEquals(0, rejected.get());
  }
}
