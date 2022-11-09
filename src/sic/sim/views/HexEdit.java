package sic.sim.views;

import sic.common.Conversion;
import sic.common.SICXE;
import sic.sim.Colors;
import sic.sim.vm.Machine;
import sic.sim.vm.Memory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * TODO: write a short description
 *
 * @author jure
 */
public class HexEdit extends JPanel implements FocusListener, MouseListener, KeyListener {

    // move offsets
    static final int MOVE_MEM_SMALL  = 0x10;
    static final int MOVE_MEM_MEDIUM = 0x100;
    static final int MOVE_MEM_LARGE  = 0x1000;

    // columns and rows
    static final int COL_ADDR = 0;
    static final int COL_HEX = 7;
    static final int COL_HEX_LAST = 7 + 3*16 - 1;
    static final int COL_CHR = 56;
    static final int COL_CHR_LAST = 56 + 16 - 1;
    static final int COL_LAST = 72;
    static final int ROW_LAST = 16;


    private Machine machine;
    private Memory memory;

    private int startAddress;

    private int lineHeight;
    private int charWidth;
    private int cursorAddress;
    private boolean focusOnHex;         // hex or chr part focused
    private boolean firstNibble;        // first nibble in hex view active

    ActionListener onAddressChange;

    public HexEdit() {
        setForeground(Colors.fg);
        setBackground(Colors.bg);
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        addFocusListener(this);
        addMouseListener(this);
        addKeyListener(this);
        setFocusTraversalKeysEnabled(false);
        focusOnHex = true;
        firstNibble = true;

//        setFocusable(true);
//        setRequestFocusEnabled(true);
//        requestFocus();
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "left");
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "right");
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "up");
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "down");
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "tab");
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), "tab");
        getActionMap().put("left", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                moveCursor(-1, 0);
            }
        });
        getActionMap().put("right", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                moveCursor(1, 0);
            }
        });
        getActionMap().put("up", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                moveCursor(0, -1);
            }
        });
        getActionMap().put("down", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                moveCursor(0, 1);
            }
        });
        getActionMap().put("tab", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                focusOnHex = !focusOnHex;
                firstNibble = true;
                repaint();
            }
        });
    }

    private int fullRows() {
        return (getHeight() - getInsets().top - getInsets().bottom) / lineHeight;
    }

    public int getStartAddress() {
        return startAddress;
    }

    public int getEndAddress() {
        return Math.min(startAddress + fullRows() * 16 - 1, SICXE.MAX_ADDR);
    }

    public void setStartAddress(int addr) {
        if (addr < 0) return;
        int endAddress = addr + fullRows() * 16 - 1;
        if (endAddress > SICXE.MAX_ADDR + 15 && addr > startAddress) return;
        int diff = addr - startAddress;
        startAddress = addr;
        cursorAddress += diff;
        repaint();
        if (onAddressChange != null) onAddressChange.actionPerformed(null);
    }

    public void moveStartAddress(int rows) {
        setStartAddress(startAddress + rows * 16);
    }

    public int getCursorAddress() {
        return cursorAddress;
    }

    public void setCursorAddress(int addr) {
        if (addr < 0 || addr > SICXE.MAX_ADDR) return;
        if (addr < startAddress) moveStartAddress(-1);
        if (addr > getEndAddress()) moveStartAddress(1);
        cursorAddress = addr;
        repaint();
        // if cursor was hidden due to window resize
        int col = cursorAddress % 16;
        if (cursorAddress > getEndAddress()) cursorAddress = getEndAddress() - 15 + col;
        if (onAddressChange != null) onAddressChange.actionPerformed(null);
    }

    public void moveCursor(int dx, int dy) {
        setCursorAddress(cursorAddress + dy * 16 + dx);
    }

    private int maxRows() {
        int r1 = fullRows();
        int r2 = (SICXE.MAX_ADDR - startAddress + 16) / 16;
        return r1 < r2 ? r1 : r2;
    }

    private int xToCol(int x) {
        x -= getInsets().left;
        // click on chr
        int col = x / charWidth - COL_CHR;
        if (0 <= col && col <= 15) return col;
        // click on hex
        col = (x + charWidth/2) / charWidth - COL_HEX;    // shift for half a character width
        if (col < 0) return -1;
        if (0 <= col && col <= 3*15) return col / 3;
        return -1;
    }

    private int yToRow(int y) {
        y -= getInsets().top;
        if (y < 0) return -1;
        int row = y / lineHeight;
        if (0 <= row && row <= maxRows()) return row;
        return -1;
    }

    public void setMachine(Machine machine) {
        this.machine = machine;
        this.memory = machine.memory;
    }

    @Override
    public void setFont(Font font) {
        FontMetrics fm =  getFontMetrics(font);
        lineHeight = fm.getHeight();
        charWidth = fm.charWidth('0');
        super.setFont(font);
    }

    @Override
    public void focusGained(FocusEvent e) {
        repaint();
    }

    @Override
    public void focusLost(FocusEvent e) {
        repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int col = xToCol(e.getX());
        int row = yToRow(e.getY());
        if (col != -1 && row != -1) {
            setCursorAddress(startAddress + row * 16 + col);
            focusOnHex = (e.getX() - getInsets().left) / charWidth < COL_CHR;
            firstNibble = true;
            repaint();
        }
        if (!hasFocus()) requestFocus();
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    private int get(int addr) {
        if (memory == null) return 0;
        return memory.getByteRaw(addr);
    }

    private void set(int addr, int val) {
        if (memory != null)
            memory.setByteRaw(addr, val);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == KeyEvent.VK_TAB || e.getKeyChar() == KeyEvent.VK_ENTER) return;
        int addr = getCursorAddress();
        if (addr > SICXE.MAX_ADDR) return;
        if (focusOnHex) {
            int val = Conversion.hexToInt(String.valueOf(e.getKeyChar()), -1);
            if (val == -1) return;
            int b = get(addr);
            if (b < 0) return;
            if (firstNibble) b = (b & 0x0F) | ((val & 0x0F) << 4);
            else b = (b & 0xF0) | (val & 0x0F);
            set(addr, b);
            firstNibble = !firstNibble;
            if (firstNibble) moveCursor(1, 0);
        } else {
            set(addr, e.getKeyChar());
            moveCursor(1, 0);
        }
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        Insets i = getInsets();
        return new Dimension(COL_LAST * charWidth + i.left + i.right, ROW_LAST * lineHeight + i.top + i.bottom);
    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int addr = startAddress;
        if (addr > SICXE.MAX_ADDR) return;
        var readAddr = machine.getLastExecRead();
        var writeAddr = machine.getLastExecWrite();
        var pcAddr = machine.getLastExecAddr();
        int y = getInsets().top + lineHeight;
        int rows = (getHeight() - getInsets().top - getInsets().bottom) / lineHeight;
        for (int row = 0; row < rows; row++) {
            // address
            int x = getInsets().left + COL_ADDR * charWidth;
            g.setColor(Colors.fg);
            g.drawString(Conversion.addrToHex(addr), x, y);
            // hex and chr
            int x1 = getInsets().left + COL_HEX * charWidth;
            int x2 = getInsets().left + COL_CHR * charWidth;
            for (int col = 0; col < 16; col++) {
                Color hexViewFg;
                Color hexViewBg;
                Color asciiViewFg;
                Color asciiViewBg;
                // Select color
                if (addr == cursorAddress) {
                    hexViewBg = Colors.bg(true, focusOnHex && isFocusOwner());
                    hexViewFg = Colors.fg(true, focusOnHex && isFocusOwner());
                    asciiViewBg = Colors.bg(true, !focusOnHex && isFocusOwner());
                    asciiViewFg = Colors.fg(true, !focusOnHex && isFocusOwner());
                } else if (readAddr.addressWithinSpan(addr)) {
                    hexViewBg = Colors.lastReadBg;
                    hexViewFg = Colors.lastReadFg;
                    asciiViewBg = Colors.lastReadBg;
                    asciiViewFg = Colors.lastReadFg;
                } else if (writeAddr.addressWithinSpan(addr)) {
                    hexViewBg = Colors.lastWriteBg;
                    hexViewFg = Colors.lastWriteFg;
                    asciiViewBg = Colors.lastWriteBg;
                    asciiViewFg = Colors.lastWriteFg;
                } else if (pcAddr.addressWithinSpan(addr)) {
                    hexViewBg = Colors.currentPcBg;
                    hexViewFg = Colors.currentPcFg;
                    asciiViewBg = Colors.currentPcBg;
                    asciiViewFg = Colors.currentPcFg;
                } else {
                    hexViewBg = Colors.bg(false, focusOnHex && isFocusOwner());
                    hexViewFg = Colors.fg(false, focusOnHex && isFocusOwner());
                    asciiViewBg = Colors.bg(false, !focusOnHex && isFocusOwner());
                    asciiViewFg = Colors.fg(false, !focusOnHex && isFocusOwner());
                }
                
                // Paint background
                g.setColor(hexViewBg);
                g.fillRect(x1 - 2, y - lineHeight + 3, 2 * charWidth + 4, lineHeight);
                g.setColor(asciiViewBg);
                g.fillRect(x2 - 1, y - lineHeight + 3, charWidth + 2, lineHeight);


                // Paint foreground
                g.setColor(hexViewFg);
                int b = get(addr);
                if (b < 0) return;
                char[] ch = {'.'};
                if (b >= 0x20 && b < 0x7E) ch[0] = (char)b;
                g.drawString(Conversion.byteToHex(b), x1, y);
                x1 += 3 * charWidth;

                g.setColor(asciiViewFg);
                g.drawChars(ch, 0, 1, x2, y);
                x2 += charWidth;
                if (++addr > SICXE.MAX_ADDR) return;
            }
            y += lineHeight;
        }
    }

}
