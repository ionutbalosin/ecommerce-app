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
package ionutbalosin.training.ecommerce.account.controller;

import static java.util.UUID.fromString;
import static java.util.stream.Collectors.toList;

import ionutbalosin.training.ecommerce.account.dto.AddressDto;
import ionutbalosin.training.ecommerce.account.dto.UserDto;
import ionutbalosin.training.ecommerce.account.dto.mapper.AddressDtoMapper;
import ionutbalosin.training.ecommerce.account.dto.mapper.UserDtoMapper;
import ionutbalosin.training.ecommerce.account.model.Address;
import ionutbalosin.training.ecommerce.account.model.User;
import ionutbalosin.training.ecommerce.account.service.AccountService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

@Controller
public class AccountController {

  private static final Logger LOGGER = LoggerFactory.getLogger(AccountController.class);

  private final AccountService accountService;
  private final UserDtoMapper userDtoMapper;
  private final AddressDtoMapper addressDtoMapper;

  public AccountController(
      AccountService accountService,
      UserDtoMapper userDtoMapper,
      AddressDtoMapper addressDtoMapper) {
    this.accountService = accountService;
    this.userDtoMapper = userDtoMapper;
    this.addressDtoMapper = addressDtoMapper;
  }

  @QueryMapping
  public UserDto userById(@Argument String id) {
    LOGGER.debug("userById(id = '{}')", id);
    final User user = accountService.getUser(fromString(id));
    final UserDto userDto = userDtoMapper.map(user);
    return userDto;
  }

  @SchemaMapping
  public List<AddressDto> addresses(UserDto userDto) {
    LOGGER.debug("addresses(userId = '{}')", userDto.getId());
    final List<Address> addresses = accountService.getAddresses(userDto.getId());
    final List<AddressDto> addressesDto =
        addresses.stream().map(addressDtoMapper::map).collect(toList());
    return addressesDto;
  }
}
