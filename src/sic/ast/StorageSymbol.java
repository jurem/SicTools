package sic.ast;

import sic.asm.Location;
import sic.ast.storage.StorageData;
import sic.ast.storage.StorageRes;

import static sic.common.Opcode.*;

public class StorageSymbol extends Symbol {

    public enum DataType {
        BYTE, WORD, FLOAT
    }

    private DataType dataType;
    private Integer elementSize; // Size of single element in watch; B=1, W=3, F=6
    private Integer elementCount;

    public StorageSymbol(String name, Location loc, int value, Command command) {
        super(name, loc, value, true);
        bindCommand(command);
    }

    /**
     * Represents the size of single element (single byte, word, or float)
     */
    public Integer getElementSize() {
        return elementSize;
    }

    /**
     * Represents the amount of elements; RESW 20 -> count = 20
     */
    public Integer getElementCount() {
        return elementCount;
    }

    /**
     * The type that this symbol represents (byte, float, word...)
     */
    public DataType getDataType() {
        return dataType;
    }

    /**
     * Extract the information from the command and update the state.
     * @param command The command after the label
     * @return Whether given command was related to data
     */
    private boolean bindCommand(Command command) {
        if (!(command instanceof StorageRes || command instanceof StorageData)) {
            return false;
        }

        switch (command.mnemonic.opcode) {
            case BYTE:
            case RESB:
                dataType = DataType.BYTE;
                elementSize = 1;
                break;
            case WORD:
            case RESW:
                dataType = DataType.WORD;
                elementSize = 3;
                break;
            case FLOT:
            case RESF:
                dataType = DataType.FLOAT;
                elementSize = 6;
                break;
        }

        elementCount = command.size() / elementSize;

        return true;
    }

    /**
     * Represent only as name so it's nicer in the TreeTable view.
     */
    @Override
    public String toString() {
        return super.name;
    }

}
