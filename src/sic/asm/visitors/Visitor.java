package sic.asm.visitors;

import sic.asm.AsmError;
import sic.asm.ErrorCatcher;
import sic.ast.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * TODO: write a short description
 *
 * @author jure
 */
public class Visitor {

    public final Program program;
    public final ErrorCatcher errorCatcher;

    public Visitor(Program program, ErrorCatcher errorCatcher) {
        this.program = program;
        this.errorCatcher = errorCatcher;
        begin();
    }

    protected void begin() {
        program.switchDefault();
        for (Section section : program.sections) {
            section.reset();
            for (Block block : section.blocks)
                block.reset();
        }
    }

    protected Method findVisitMethod(Node node) {
        Method method = null;
        Class visitorClass = getClass();
        Class nodeClass = node.getClass();
        do {
            try {
                method = visitorClass.getDeclaredMethod("visit", new Class[]{nodeClass});
            } catch (NoSuchMethodException e) {}
            nodeClass = nodeClass.getSuperclass();
        } while (nodeClass != null && method == null);
        return method;
    }

    protected void visit(Node node) throws AsmError {
        // BFS dispatcher for visit() methods
        Method method = findVisitMethod(node);
        if (method == null) return;
        try {
            method.invoke(this, node);
        } catch (IllegalAccessException e) {
            if (e.getCause() instanceof AsmError)
                throw (AsmError)e.getCause();
            e.printStackTrace();
            System.exit(1);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof AsmError)
                throw (AsmError)e.getCause();
            e.printStackTrace();
            System.exit(1);
        }
    }

    // catch errors: node.enter, this.visit(dynamic), node.leave
    protected void visitNode(Node node) {
        try {
            node.enter(program);
            visit(node);
            node.leave(program);
        } catch (AsmError err) {
            errorCatcher.add(err);
        }
    }

    protected void visitCommands(List<Command> commands) {
        for (Command cmd : commands) visitNode(cmd);
    }

    protected void visitBlocks(List<Block> blocks) {
        for (Block block : blocks) visitNode(block);
    }

    protected void visitSections(List<Section> sections) {
        for (Section section : sections) visitNode(section);
    }

    protected void visitSymbols(List<Symbol> symbols) {
        for (Symbol symbol : symbols) visitNode(symbol);
    }

    // visit entry points

    public void visitCommands() {
        visitCommands(program.commands);
    }

    public void visitByStructure() {
        try {
            program.enter(program);
            visit(program);
            program.leave(program);
        } catch (AsmError err) {
            errorCatcher.add(err);
        }
    }

}
