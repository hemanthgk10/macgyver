#!/bin/sh

#this file must end with a newline
exec java -Dcli.exe="$0" -Dcli.launch=true -jar $0 "$@"
