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
package ionutbalosin.training.ecommerce.shipping.adapter.gateway;

import static ionutbalosin.training.ecommerce.message.schema.shipping.ShippingStatus.COMPLETED;
import static ionutbalosin.training.ecommerce.message.schema.shipping.ShippingStatus.FAILED;

import ionutbalosin.training.ecommerce.message.schema.shipping.ShippingStatus;
import ionutbalosin.training.ecommerce.shipping.domain.model.Shipping;
import ionutbalosin.training.ecommerce.shipping.domain.port.ShippingGatewayPort;
import java.util.Random;
import org.springframework.stereotype.Component;

/*
 * The shipping gateway simulates both successful and failed shipping statuses based on a
 * randomly generated boolean value initialized with a seed equal to the number of products.
 */
@Component
public class ShippingGatewayAdapter implements ShippingGatewayPort {

  private static final Random RANDOM = new Random(16384);

  @Override
  public ShippingStatus ship(Shipping shipping) {
    final int seed = shipping.getProducts().size();
    RANDOM.setSeed(seed);

    final ShippingStatus shippingStatus = (RANDOM.nextBoolean()) ? COMPLETED : FAILED;
    return shippingStatus;
  }
}
