package edu.mit.compilers.le02.semanticchecks;

import edu.mit.compilers.le02.ErrorReporting;
import edu.mit.compilers.le02.ast.ArrayDeclNode;
import edu.mit.compilers.le02.ast.ASTNode;
import edu.mit.compilers.le02.ast.ASTNodeVisitor;
import edu.mit.compilers.le02.ast.ClassNode;
import edu.mit.compilers.le02.stgenerator.SymbolTableException;

public class CheckArrayLengths extends ASTNodeVisitor<Boolean> {
    /** Holds the CheckArrayLengths singleton. */
    private static CheckArrayLengths instance;

    /**
     * Retrieves the CheckArrayLengths singleton, creating if necessary.
     */
    public static CheckArrayLengths getInstance() {
        if (instance == null) {
            instance = new CheckArrayLengths();
        }
        return instance;
    }

    /**
     * Checks that every array has a positive length.
     */
    public static void check(ASTNode root) {
        assert(root instanceof ClassNode);
        root.accept(getInstance());
    }

    @Override
    public Boolean visit(ArrayDeclNode node) {
        if (node.getLength() <= 0) {
            ErrorReporting.reportError(new SymbolTableException(node.getSourceLoc(), "Non-positive array size"));
        }
        return true;
    }
}