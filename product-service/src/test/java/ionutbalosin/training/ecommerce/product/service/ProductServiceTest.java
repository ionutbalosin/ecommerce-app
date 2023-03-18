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
package ionutbalosin.training.ecommerce.product.service;

import static ionutbalosin.training.ecommerce.product.service.ProductService.BULK_HEAD_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import ionutbalosin.training.ecommerce.product.dao.ProductJdbcDao;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

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
