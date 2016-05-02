#!/bin/bash 

THIS_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

MACGYVER_EXE=${THIS_DIR}/build/macgyver


cat ./src/main/resources/cli/cli-jar-header.sh >${MACGYVER_EXE}


CAPSULE_JAR=$(find build -name '*-capsule.jar' | head -1)
cat ${CAPSULE_JAR} >>${MACGYVER_EXE}
chmod +x ${MACGYVER_EXE}


echo binary: ${MACGYVER_EXE}


