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
package ionutbalosin.training.ecommerce.order.controller;

import static ionutbalosin.training.ecommerce.order.PostgresqlSingletonContainer.INSTANCE;
import static ionutbalosin.training.ecommerce.order.api.model.OrderUpdateDto.StatusEnum.COMPLETED;
import static ionutbalosin.training.ecommerce.order.util.JsonUtil.asJsonString;
import static ionutbalosin.training.ecommerce.order.util.JsonUtil.stringToJsonObject;
import static java.util.UUID.fromString;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ionutbalosin.training.ecommerce.order.api.model.OrderDetailsDto;
import ionutbalosin.training.ecommerce.order.api.model.OrderDetailsDto.CurrencyEnum;
import ionutbalosin.training.ecommerce.order.api.model.OrderDetailsDto.StatusEnum;
import ionutbalosin.training.ecommerce.order.api.model.OrderUpdateDto;
import ionutbalosin.training.ecommerce.order.listener.OrderEventListener;
import java.util.UUID;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrderControllerITest {

  private final UUID PREFILLED_USER_ID = fromString("42424242-4242-4242-4242-424242424242");
  private final UUID PREFILLED_ORDER_ID_1 = fromString("307e086c-3900-11ed-a261-0242ac120002");
  private final UUID PREFILLED_ORDER_ID_2 = fromString("307e0ed4-3900-11ed-a261-0242ac120002");
  private final UUID PREFILLED_ORDER_ID_3 = fromString("307e0ab9-3900-11ed-a261-0242ac120002");

  @Container
  private static final PostgreSQLContainer POSTGRE_SQL_CONTAINER = INSTANCE.getContainer();

  @Autowired private MockMvc mockMvc;
  @MockBean private OrderEventListener orderEventListener;

  final OrderDetailsDto PREFILLED_ORDER_1 =
      new OrderDetailsDto()
          .orderId(PREFILLED_ORDER_ID_1)
          .userId(PREFILLED_USER_ID)
          .amount(11.0)
          .currency(CurrencyEnum.EUR)
          .status(StatusEnum.NEW)
          .details(stringToJsonObject("{}"));

  final OrderDetailsDto PREFILLED_ORDER_2 =
      new OrderDetailsDto()
          .orderId(PREFILLED_ORDER_ID_2)
          .userId(PREFILLED_USER_ID)
          .amount(22.0)
          .currency(CurrencyEnum.EUR)
          .status(StatusEnum.SHIPPING_IN_PROGRESS)
          .details(stringToJsonObject("{}"));

  final OrderDetailsDto PREFILLED_ORDER_3 =
      new OrderDetailsDto()
          .orderId(PREFILLED_ORDER_ID_3)
          .userId(PREFILLED_USER_ID)
          .amount(33.0)
          .currency(CurrencyEnum.EUR)
          .details(stringToJsonObject("{}"));

  final OrderUpdateDto ORDER_1_UPDATE = new OrderUpdateDto().status(COMPLETED);

  @Test
  @Order(1)
  public void ordersUserIdHistoryGet_isOk_prefilledData() throws Exception {
    // this test relies on the prefilled DB data
    mockMvc
        .perform(get("/orders/{userId}/history", PREFILLED_USER_ID).contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.*", hasSize(greaterThanOrEqualTo(3))))
        .andExpect(
            jsonPath(
                "$[*].orderId",
                hasItems(
                    PREFILLED_ORDER_1.getOrderId().toString(),
                    PREFILLED_ORDER_2.getOrderId().toString(),
                    PREFILLED_ORDER_3.getOrderId().toString())))
        .andExpect(
            jsonPath(
                "$[*].userId",
                hasItems(
                    PREFILLED_ORDER_1.getUserId().toString(),
                    PREFILLED_ORDER_2.getUserId().toString(),
                    PREFILLED_ORDER_3.getUserId().toString())))
        .andExpect(
            jsonPath(
                "$[*].amount",
                hasItems(
                    PREFILLED_ORDER_1.getAmount(),
                    PREFILLED_ORDER_2.getAmount(),
                    PREFILLED_ORDER_3.getAmount())))
        .andExpect(
            jsonPath(
                "$[*].currency",
                hasItems(
                    PREFILLED_ORDER_1.getCurrency().toString(),
                    PREFILLED_ORDER_2.getCurrency().toString(),
                    PREFILLED_ORDER_3.getCurrency().toString())))
        .andExpect(
            jsonPath(
                "$[*].status",
                hasItems(
                    PREFILLED_ORDER_1.getStatus().toString(),
                    PREFILLED_ORDER_2.getStatus().toString()
                    /* exclude Order 3 status on purpose - updated by Payment listener test */ )));
  }

  @Test
  @Order(2)
  public void ordersOrderIdPatch_isOk_prefilledData() throws Exception {
    mockMvc
        .perform(
            patch("/orders/{orderId}", PREFILLED_ORDER_1.getOrderId())
                .contentType(APPLICATION_JSON)
                .content(asJsonString(ORDER_1_UPDATE)))
        .andExpect(status().isOk());
  }

  @Test
  @Order(3)
  public void ordersOrderIdGet_isOk_prefilledData() throws Exception {
    mockMvc
        .perform(
            get("/orders/{orderId}", PREFILLED_ORDER_1.getOrderId())
                .contentType(APPLICATION_JSON)
                .content(asJsonString(ORDER_1_UPDATE)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.orderId", is(PREFILLED_ORDER_1.getOrderId().toString())))
        .andExpect(jsonPath("$.userId", is(PREFILLED_ORDER_1.getUserId().toString())))
        .andExpect(jsonPath("$.amount", is(PREFILLED_ORDER_1.getAmount())))
        .andExpect(jsonPath("$.currency", is(PREFILLED_ORDER_1.getCurrency().toString())))
        .andExpect(jsonPath("$.details", is(PREFILLED_ORDER_1.getDetails())))
        .andExpect(jsonPath("$.status", is(ORDER_1_UPDATE.getStatus().toString())));
  }
}
