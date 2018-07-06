#! /bin/bash

ARGS="ctags desktop:run"

function gradlew_do()
{
    echo "./gradlew $ARGS" && ./gradlew $ARGS || exit 1
}

case "$1" in
# clean
    "c")
        shift;
        ARGS="clean assets:build $ARGS"
        gradlew_do
        ;;
# build
    "b")
        shift;
        ARGS="desktop:build"
        gradlew_do
        ;;
# dist
    "d")
        shift;
        ARGS="desktop:dist $@"
        gradlew_do
        ;;
    *)
        ARGS="$ARGS $@"
        gradlew_do
        ;;
esac

