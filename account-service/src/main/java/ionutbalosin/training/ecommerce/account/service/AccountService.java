/**
 *  eCommerce Application
 *
 *  Copyright (c) 2022 - 2024 Ionut Balosin
 *  Website: www.ionutbalosin.com
 *  X: @ionutbalosin | LinkedIn: ionutbalosin | Mastodon: ionutbalosin@mastodon.social
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
package ionutbalosin.training.ecommerce.account.service;

import static java.util.UUID.fromString;

import ionutbalosin.training.ecommerce.account.model.Address;
import ionutbalosin.training.ecommerce.account.model.User;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

  // TODO: Add persistence/caching for more users

  private final UUID USER_ID = fromString("42424242-4242-4242-4242-424242424242");
  private final Address ADDRESS =
      new Address()
          .userId(USER_ID)
          .country("Austria")
          .county("Lower Austria")
          .city("Vienna")
          .street("Landstrasse")
          .streetNumber("81-87")
          .building("2")
          .floor("4")
          .apartment("56");
  private final User USER =
      new User()
          .id(USER_ID)
          .firstName("John")
          .lastName("Doe")
          .email("john.doe@ecommerce.com")
          .dateOfBirth(LocalDate.of(1964, 12, 31))
          .addresses(List.of(ADDRESS));

  public User getUser(UUID userId) {
    return USER;
  }

  public List<Address> getAddresses(UUID userId) {
    return List.of(ADDRESS);
  }
}
