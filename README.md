instalint
=========

Lint some code instantly ;-)

Using the world-renowned analyzers of [SonarQube][].

Borrowing some code from [sonarlint-core][].

Building
--------

Using Maven:

    mvn clean install


Running instalint
-----------------

Download latest versions of analyzers:

    ./scripts/download-analyzers.sh

Optionally, you can download all versions of all analyzers with:

    ./scripts/download-analyzers.sh --all

In `instalint-http-server`, run:

    mvn org.mortbay.jetty:jetty-maven-plugin:run

Or, to be able to run simply with `mvn jetty:run`,
add this to `<settings/>` in your `~/.m2/settings.xml` file:

```xml
<pluginGroups>
  <pluginGroup>org.mortbay.jetty</pluginGroup>
</pluginGroups>
```

Open http://localhost:8080 in your browser, or:

    curl -X POST localhost:8080/analyze -d language=javascript -d code=yourcode
    curl -X POST localhost:8080/analyze -d language=javascript -d code="$(cat yourfile)"

### Using the web interface

1. Select the language
2. Copy-paste some code

The analysis results should appear within a second.

### Code samples that should report issues

Java:

```java
public class Hello {
  private int add(int a, int b) {
    return a + b;
  }
}
```

JavaScript:

```javascript
var arr = [1, 2, 3];
for (i in arr) {
    console.log(i);
}
```

Python:

```python
def indexOf:
    pass
```

PHP:

```php
<?php
// var a = [1, 2, 3];
?>
```

[SonarQube]: https://docs.sonarqube.org/
[sonarlint-core]: https://github.com/sonarsource/sonarlint-core
