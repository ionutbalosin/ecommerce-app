package ionutbalosin.training.ecommerce.shopping.cart.util;

import static ionutbalosin.training.ecommerce.shopping.cart.util.JsonUtil.JacksonObjectMapper.OBJECT_MAPPER;

import com.fasterxml.jackson.databind.ObjectMapper;

/*
 * (c) 2022 Ionut Balosin
 * Website: www.ionutbalosin.com
 * Twitter: @ionutbalosin
 *
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 */
public class JsonUtil {

  public static String asJsonString(final Object obj) {
    try {
      return OBJECT_MAPPER.getObjectMapper().writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  enum JacksonObjectMapper {
    OBJECT_MAPPER;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ObjectMapper getObjectMapper() {
      return objectMapper;
    }
  }
}
