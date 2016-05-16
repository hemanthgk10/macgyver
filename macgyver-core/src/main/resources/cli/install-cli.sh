#!/bin/bash

DOT_HOME=${HOME}/.macgyver
DOT_BIN=${DOT_HOME}/bin
CONFIG_FILE=${DOT_HOME}/config
MACGYVER_EXE=${DOT_BIN}/macgyver

mkdir -p ${DOT_HOME}
mkdir -p ${DOT_BIN}

MACGYVER_URL="MACGYVER_TEMPLATE_URL"

if [ ! -f "${CONFIG_FILE}" ]; then
	
cat <<EOF >${CONFIG_FILE}
{
  "url" : "${MACGYVER_URL}",
  "username" : "${USER}"
}
EOF
	
fi




echo "Downloading macgyver CLI and installing it in: ${MACGYVER_EXE}"
curl -so ${MACGYVER_EXE} ${MACGYVER_URL}/cli/download 
chmod +x ${MACGYVER_EXE}

grep -q '.macgyver/bin' ${HOME}/.bash_profile
if [ $? -ne 0 ]; then
	echo
	echo Adding ${HOME}/.macgyver/bin to your PATH in .bash_profile.
	echo
	echo You will need to run: 
	echo 
	echo . ${HOME}/.bash_profile
	echo
	echo  or start a new shell
	echo
	echo "export PATH=\$PATH:$HOME/.macgyver/bin" >>${HOME}/.bash_profile
fi