### Setup

Add this to your `~/.m2/settings.xml` (inside `<settings/>`):

```xml
<pluginGroups>
  <pluginGroup>org.mortbay.jetty</pluginGroup>
</pluginGroups>
```

### Get, build and run

Clone and build sonarlint-core:

    git clone git@github.com:bartfastiel/sonarlint-core.git
    cd sonarlint-core
    git checkout ugly-poc-sonarlint-online-httpservlet
    mvn clean install

Download analyzers:

    mkdir -pv http-daemon/null/work/Catalina/localhost/ROOT/plugins
    cp core/target/plugins/*.jar http-daemon/null/work/Catalina/localhost/ROOT/plugins/

Run:

    mvn jetty:run

Open http://localhost:8080 in your browser, or:

    curl -X POST localhost:8080/analyze -d language=javascript -d yourcode
    curl -X POST localhost:8080/analyze -d language=javascript -d "$(cat yourfile)"

### Using the web interface

1. Select the language
2. Copy-paste some code
3. Un-focus the text editor (press tab) to trigger analysis

The analysis results should appear within a second.

### Code samples that should report issues

JavaScript:

    var arr = [1, 2, 3];
    for (i in arr) {
        console.log(i);
    }

Python:

    def indexOf:
        pass

PHP:

    <?php
    // var a = [1, 2, 3];
    ?>
