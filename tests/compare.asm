
        LDA     a
        COMP    b
        JGT     grt
lwr     LDA     0xBB
grt     LDA     0xAA

b   WORD    0x000001
a   WORD    0xFFFFFF
