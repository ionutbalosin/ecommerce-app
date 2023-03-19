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
package ionutbalosin.training.ecommerce.account.model;

import java.util.UUID;

public class Address {

  private UUID userId;
  private String country;
  private String county;
  private String city;
  private String street;
  private String streetNumber;
  private String building;
  private String floor;
  private String apartment;

  public UUID getUserId() {
    return userId;
  }

  public Address userId(UUID userId) {
    this.userId = userId;
    return this;
  }

  public String getCountry() {
    return country;
  }

  public Address country(String country) {
    this.country = country;
    return this;
  }

  public String getCounty() {
    return county;
  }

  public Address county(String county) {
    this.county = county;
    return this;
  }

  public String getCity() {
    return city;
  }

  public Address city(String city) {
    this.city = city;
    return this;
  }

  public String getStreet() {
    return street;
  }

  public Address street(String street) {
    this.street = street;
    return this;
  }

  public String getStreetNumber() {
    return streetNumber;
  }

  public Address streetNumber(String streetNumber) {
    this.streetNumber = streetNumber;
    return this;
  }

  public String getBuilding() {
    return building;
  }

  public Address building(String building) {
    this.building = building;
    return this;
  }

  public String getFloor() {
    return floor;
  }

  public Address floor(String floor) {
    this.floor = floor;
    return this;
  }

  public String getApartment() {
    return apartment;
  }

  public Address apartment(String apartment) {
    this.apartment = apartment;
    return this;
  }
}
