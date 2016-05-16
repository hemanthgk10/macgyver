#!/bin/sh 

#this file must end with a newline

if [ -x /usr/libexec/java_home ]; then
	JAVA_HOME=$(/usr/libexec/java_home -v 1.8)	
fi

if [ -x ${JAVA_HOME}/bin/java ]; then
	JAVA_CMD=${JAVA_HOME}/bin/java
else 
	JAVA_CMD=java
fi


exec ${JAVA_CMD} -Dcli.exe="$0" -Dcli.launch=true -jar $0 "$@"
