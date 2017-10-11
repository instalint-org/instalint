#!/usr/bin/env bash

set -euo pipefail

cd "$(dirname "$0")/.."

analyzers=(java javascript php python)

baseurl=https://sonarsource.bintray.com/Distribution
daemonroot=instalint-http-daemon/null/work/Catalina/localhost/ROOT
pluginsdir=$daemonroot/plugins
settings=$daemonroot/settings.properties

usage() {
    echo "Usage: $0 [-h|--help] --all"
    echo
    echo "Download the latest or all plugins, and update settings.properties"
    exit
}

all=0

for arg; do
    case "$arg" in
        -h|--help) usage ;;
        --all) all=1 ;;
    esac
done

mkdir -p "$pluginsdir"

diag() {
    echo "* $@" >&2
}

sort_by_version() {
    # TODO make this more robust, this will not work well
    # when the 4th element (patch) is not steadily increasing
    sort -t. -k4n
}

latest() {
    sort_by_version | tail -n 1
}

jars() {
    local plugin=$1
    local url=$baseurl/$plugin
    diag fetching jar list from $url ...
    curl -sL "$url" | grep "href=\"$plugin-.*jar\"" | cut -d'"' -f2
}

latest_jar() {
    jars "$@" | latest
}

select_jars() {
    test "$all" = 1 && jars "$@" || latest_jar "$@"
}

download() {
    local url=$1
    local targetpath=$2
    diag downloading $url to $targetpath ...
    curl -sLo "$targetpath" "$url"
}

download_analyzers() {
    for analyzer in "${analyzers[@]}"; do
        plugin=sonar-$analyzer-plugin
        for jar in $(select_jars $plugin); do
            url=$baseurl/$plugin/$jar
            targetpath=$pluginsdir/$jar
            test -s "$targetpath" || download "$url" "$targetpath"
        done
    done
}

version() {
    sed -e 's/^[^0-9]*//' -e 's/[^0-9]*$//'
}

generate_settings_properties() {
    local plugin version
    for analyzer in "${analyzers[@]}"; do
        plugin=sonar-$analyzer-plugin

        ls "$pluginsdir" | grep "$plugin" | while read jar; do
            version=$(echo "$jar" | version)
            echo "$analyzer.plugin.$version = $jar"

            patchless=${version%.0.*}
            if test "$version" != "$patchless"; then
                echo "$analyzer.plugin.$patchless.0 = $jar"
                echo "$analyzer.plugin.$patchless = $jar"
            fi
        done

        version=$(ls "$pluginsdir" | grep "$plugin" | latest | version)
        patchless=${version%.0.*}

        echo "$analyzer.latestVersion = $patchless"
    done
}

update_settings_properties() {
    generate_settings_properties > "$settings"
}

download_analyzers

update_settings_properties
