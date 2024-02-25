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

import static com.tngtech.archunit.core.domain.properties.CanBeAnnotated.Predicates.annotatedWith;
import static com.tngtech.archunit.lang.conditions.ArchConditions.haveSimpleNameEndingWith;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.core.domain.properties.HasName;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

@AnalyzeClasses(packages = "ionutbalosin.training.ecommerce.shipping")
public class HexagonalNamingConventionsTest {

  @ArchTest
  private ArchRule configClassesAreNamedProperly =
      classes()
          .that()
          .areAnnotatedWith(Configuration.class)
          .should()
          .haveSimpleNameEndingWith("Config");

  @ArchTest
  private ArchRule restControllerAreNamedProperly =
      classes()
          .that()
          .areAnnotatedWith(RestController.class)
          .should()
          .haveSimpleNameEndingWith("Controller");

  @ArchTest
  private ArchRule serviceClassesAreNamedProperly =
      classes().that().areAnnotatedWith(Service.class).should().haveSimpleNameEndingWith("Service");

  @ArchTest
  private ArchRule propertiesClassesAreNamedProperly =
      classes()
          .that()
          .areAnnotatedWith(ConfigurationProperties.class)
          .should()
          .haveSimpleNameEndingWith("Properties");

  @ArchTest
  private ArchRule adapterClassesImplementingPortNamedProperly =
      classes()
          .that()
          .resideInAPackage("..adapter..")
          .and()
          .implement(HasName.Predicates.nameMatching(".*Port"))
          .should()
          .haveSimpleNameEndingWith("Adapter");

  @ArchTest
  private ArchRule adapterClassesInDomainShouldNotImplementSpringAnnotations =
      classes()
          .that()
          .resideInAPackage("ionutbalosin.training.ecommerce.shipping.adapter..")
          .and()
          .implement(HasName.Predicates.nameMatching(".*Port"))
          .should()
          .notBeAnnotatedWith(Repository.class)
          .andShould()
          .notBeAnnotatedWith(Service.class)
          .andShould()
          .notBeAnnotatedWith(Configuration.class);

  @ArchTest
  private ArchRule repositoryClassesAreNamedProperly =
      classes()
          .that()
          .areAnnotatedWith(Repository.class)
          .should()
          .haveSimpleNameEndingWith("Repository")
          .orShould(haveSimpleNameEndingWith("RepositoryDefault"));

  @ArchTest
  private ArchRule portInterfacesAreNamedProperly =
      classes()
          .that()
          .areInterfaces()
          .and()
          .resideInAPackage("..domain..port..")
          .should()
          .haveSimpleNameEndingWith("Port");

  @ArchTest
  private ArchRule portPrefixOnlyInPortPackage =
      classes()
          .that()
          .resideOutsideOfPackage("..domain..port..")
          .should()
          .haveSimpleNameNotEndingWith("Port");

  @ArchTest
  private ArchRule unitTestClassesAreNamedProperly =
      classes()
          .that()
          .containAnyMethodsThat(annotatedWith(Test.class))
          .and()
          .areNotAnnotatedWith(SpringBootTest.class)
          .and()
          .areNotMetaAnnotatedWith(SpringBootTest.class)
          .should()
          .haveSimpleNameEndingWith("Test");

  @ArchTest
  private ArchRule integrationTestClassesAreNamedProperly =
      classes()
          .that()
          .areAnnotatedWith(SpringBootTest.class)
          .should()
          .haveSimpleNameEndingWith("Test"); // or IT
}
