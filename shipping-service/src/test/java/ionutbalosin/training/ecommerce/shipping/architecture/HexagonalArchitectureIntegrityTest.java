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
package ionutbalosin.training.ecommerce.shipping.architecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.Architectures;

@AnalyzeClasses(
    packages = {
      "ionutbalosin.training.ecommerce.shipping.application",
      "ionutbalosin.training.ecommerce.shipping.adapter",
      "ionutbalosin.training.ecommerce.shipping.domain"
    })
public class HexagonalArchitectureIntegrityTest {

  // Note: Defining each of these packages (e.g., application, adapter, domain) in its own (e.g.,
  // Maven/Gradle) module leads to better and more natural isolation compared to using the ArchUnit
  @ArchTest
  private ArchRule architectureStructureIsRespected =
      Architectures.onionArchitecture()
          .withOptionalLayers(true)
          .applicationServices("ionutbalosin.training.ecommerce.shipping.application..")
          .domainModels("ionutbalosin.training.ecommerce.shipping.domain.model..")
          .domainServices(
              "ionutbalosin.training.ecommerce.shipping.domain.service..",
              "ionutbalosin.training.ecommerce.shipping.domain.port..")
          .adapter("event-input", "ionutbalosin.training.ecommerce.shipping.adapter.listener..")
          .adapter("event-output", "ionutbalosin.training.ecommerce.shipping.adapter.sender..")
          .adapter(
              "shipping-gateway", "ionutbalosin.training.ecommerce.shipping.adapter.gateway..");
}
