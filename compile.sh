#!/bin/bash
./gradlew build install --no-daemon --parallel -x test -x javadoc -x check --build-cache --configure-on-demand --continue
