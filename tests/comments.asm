. comments and empty lines

. comment at the beginning of the line
... comment with leading dots
       . comment at the middle
	... another comment with leading dots
    . next line contains whitespace
   	    

a	LDA 1. comment 1

b	LDA 2    . comment 2

c+LDA 3 . ERR: whitespace between label and mnemonic is mandatory
d	LDA#4 . ERR: whitespace between mnemonic and operand is mandatory
