#!/bin/bash



gradle clean install

JAR_FILE=$(find ./build/libs -name 'macgyver*-capsule.jar' | head -1)

echo JAR: ${JAR_FILE}
java -Dcli.launch=true -jar ${JAR_FILE} $@
