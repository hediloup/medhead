La configuration ci‑dessous est à insérer dans votre `pom.xml` afin d’activer Cucumber‑JVM et JUnit 5 pour les tests BDD.

```xml
<dependencies>
  <!-- Cucumber core for Java -->
  <dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-java</artifactId>
    <version>7.16.1</version>
    <scope>test</scope>
  </dependency>
  <!-- Cucumber JUnit Platform Engine -->
  <dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-junit-platform-engine</artifactId>
    <version>7.16.1</version>
    <scope>test</scope>
  </dependency>
  <!-- JUnit Jupiter API for assertions -->
  <dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.10.2</version>
    <scope>test</scope>
  </dependency>
  <!-- AssertJ for fluent assertions -->
  <dependency>
    <groupId>org.assertj</groupId>
    <artifactId>assertj-core</artifactId>
    <version>3.25.3</version>
    <scope>test</scope>
  </dependency>
</dependencies>

<build>
  <plugins>
    <!-- Maven Surefire plugin to run Cucumber tests -->
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-surefire-plugin</artifactId>
      <version>3.2.5</version>
      <configuration>
        <includes>
          <!-- Exécute uniquement les runners qui se terminent par CucumberTest -->
          <include>**/*CucumberTest*.java</include>
        </includes>
      </configuration>
    </plugin>
  </plugins>
</build>
```