package ionutbalosin.training.ecommerce.order.util;

import static ionutbalosin.training.ecommerce.order.util.JsonUtil.JacksonObjectMapper.OBJECT_MAPPER;
import static java.util.Optional.ofNullable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.StringReader;
import java.io.StringWriter;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonWriter;

/*
 * (c) 2022 Ionut Balosin
 * Website: www.ionutbalosin.com
 * Twitter: @ionutbalosin
 *
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 */
public class JsonUtil {

  private static JsonObject EMPTY = Json.createObjectBuilder().build();

  private JsonUtil() {}

  public static String jsonObjectToString(JsonObject jsonObject) {
    final StringWriter stringWriter = new StringWriter();
    final JsonWriter writer = Json.createWriter(stringWriter);
    writer.writeObject(jsonObject);
    writer.close();

    return stringWriter.getBuffer().toString();
  }

  public static JsonObject stringToJsonObject(String stringObject) {
    return ofNullable(stringObject)
        .map(StringReader::new)
        .map(Json::createReader)
        .map(JsonReader::readObject)
        .orElseGet(() -> EMPTY);
  }

  public static JsonObject objectToJsonObject(Object object) {
    return ofNullable(object)
        .map(
            obj -> {
              try {
                return OBJECT_MAPPER.getObjectMapper().writeValueAsString(obj);
              } catch (JsonProcessingException e) {
                throw new IllegalArgumentException("Could not create Json Object", e);
              }
            })
        .map(JsonUtil::stringToJsonObject)
        .orElse(EMPTY);
  }

  public static String objectToString(Object object) {
    return ofNullable(object)
        .map(
            obj -> {
              try {
                return OBJECT_MAPPER.getObjectMapper().writeValueAsString(obj);
              } catch (JsonProcessingException e) {
                throw new IllegalArgumentException("Could not create Json Object", e);
              }
            })
        .orElse("");
  }

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

    JacksonObjectMapper() {
      objectMapper.registerModule(new Jdk8Module()).registerModule(new JavaTimeModule());
      objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
      objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
      // Note: Avro generates specific getter methods that clashes Jackson serialization
      // https://stackoverflow.com/questions/60703971/jsonmappingexception-not-a-map-not-an-array-or-not-an-enum
      objectMapper.addMixIn(
          org.apache.avro.specific.SpecificRecord.class, JacksonIgnoreAvroPropertiesMixIn.class);
    }

    public ObjectMapper getObjectMapper() {
      return objectMapper;
    }
  }

  public abstract class JacksonIgnoreAvroPropertiesMixIn {

    @JsonIgnore
    public abstract org.apache.avro.Schema getSchema();

    @JsonIgnore
    public abstract org.apache.avro.specific.SpecificData getSpecificData();
  }
}
