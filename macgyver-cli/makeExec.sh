#!/bin/bash


MACGYVER_EXE=./build/macgyver
cat <<EOF >${MACGYVER_EXE}
#!/bin/sh

exec java -jar \$0 "\$@"
EOF

CAPSULE_JAR=$(find build -name '*-capsule.jar' | head -1)
cat ${CAPSULE_JAR} >>${MACGYVER_EXE}
chmod +x ${MACGYVER_EXE}

echo binary: ${MACGYVER_EXE}
