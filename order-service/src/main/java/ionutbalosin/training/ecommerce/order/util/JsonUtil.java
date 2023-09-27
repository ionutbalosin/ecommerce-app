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
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonWriter;
import java.io.StringReader;
import java.io.StringWriter;

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
                throw new RuntimeException(e);
              }
            })
        .map(JsonUtil::stringToJsonObject)
        .orElse(EMPTY);
  }

  public static Object jsonObjectToObject(JsonObject jsonObject) {
    return ofNullable(jsonObject)
        .map(
            json -> {
              try {
                return OBJECT_MAPPER
                    .getObjectMapper()
                    .readValue(jsonObjectToString(jsonObject), Object.class);
              } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
              }
            })
        .orElse(null);
  }

  public static <T> T jsonObjectToObject(Class<T> type, JsonObject jsonObject) {
    return ofNullable(jsonObject)
        .map(
            json -> {
              try {
                return OBJECT_MAPPER
                    .getObjectMapper()
                    .readValue(jsonObjectToString(jsonObject), type);
              } catch (JsonProcessingException e) {
                throw new IllegalArgumentException(e);
              }
            })
        .orElse(null);
  }

  public static Object stringToObject(String stringObject) {
    return ofNullable(stringObject)
        .map(
            json -> {
              try {
                return OBJECT_MAPPER.getObjectMapper().readValue(stringObject, Object.class);
              } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
              }
            })
        .orElse(null);
  }

  public static String objectToString(Object object) {
    return ofNullable(object)
        .map(
            obj -> {
              try {
                return OBJECT_MAPPER.getObjectMapper().writeValueAsString(obj);
              } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
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
