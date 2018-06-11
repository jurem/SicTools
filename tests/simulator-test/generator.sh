#!/bin/bash

PROG="../sicko/sicko -rf1000"
LIST="tester.list"

rm -f $LIST

for i in $(ls src/*.asm | sort)
do
  BASE=$(basename $i .asm)
  read -r esc from len < $i
  echo "$BASE $from $len" >> $LIST
  echo -n "Generating ref/${BASE}.ref ($from, $len) ... "
  $PROG -m$from,$len obj/${BASE}.obj
  mv -f dump ref/${BASE}.ref
  echo "DONE"
done

exit 0
