#!/bin/bash

cat <<EOF >macgyver
#!/bin/sh

exec java -jar \$0 "\$@"
EOF

cat ./build/libs/macgyver-cli-0.105.2-capsule.jar >>macgyver