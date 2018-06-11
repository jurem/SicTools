#!/bin/bash

PROG="../sicko/sicko -rf1000"
DIFF="diff -iEZBbw"
LIST="tester.list"

while read NAME START LEN
do
  echo -n "Testing on \"$NAME\" ... "
  if $PROG -m$START,$LEN obj/${NAME}.obj
  then
    if $DIFF ref/${NAME}.ref dump > /dev/null 2>&1
    then
      echo "PASSED!"
      rm dump
    else
      echo "FAILED (output mismatch)!"
      mv dump ${NAME}.app
    fi
  else
    echo "FAILED (simulator exited with error code)!"
      mv dump ${NAME}.app
  fi
done < $LIST

exit 0
