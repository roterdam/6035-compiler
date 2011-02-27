package edu.mit.compilers.le02.semanticchecks;

import edu.mit.compilers.le02.DecafType;
import edu.mit.compilers.le02.ErrorReporting;
import edu.mit.compilers.le02.ast.ASTNode;
import edu.mit.compilers.le02.ast.ASTNodeVisitor;
import edu.mit.compilers.le02.ast.BreakNode;
import edu.mit.compilers.le02.ast.ClassNode;
import edu.mit.compilers.le02.ast.ContinueNode;
import edu.mit.compilers.le02.ast.ForNode;
import edu.mit.compilers.le02.semanticchecks.SemanticException;
import edu.mit.compilers.le02.stgenerator.SymbolTableException;
import edu.mit.compilers.le02.symboltable.TypedDescriptor;
import edu.mit.compilers.le02.symboltable.MethodDescriptor;
import edu.mit.compilers.le02.symboltable.SymbolTable;

public class CheckBreakContinue extends ASTNodeVisitor<Boolean> {
    /** Holds the CheckBreakContinue singleton. */
    private static CheckBreakContinue instance;
    private static boolean inForLoop;

    /**
     * Retrieves the CheckBreakContinue singleton, creating if necessary.
     */
    public static CheckBreakContinue getInstance() {
        if (instance == null) {
            instance = new CheckBreakContinue();
        }
        return instance;
    }

    /**
     * Checks that every operation is performed on two expressions of the correct type,
     * that the types of assignments agree,
     * that for statements have integer bounds,
     * and that if statements have boolean conditionals,
     */
    public static void check(ASTNode root) {
        inForLoop = false;
        assert(root instanceof ClassNode);
        root.accept(getInstance());
    }

    @Override
    public Boolean visit(ForNode node) {
        boolean oldInForLoop = inForLoop;
        inForLoop = true;
        
        defaultBehavior(node);

        inForLoop = oldInForLoop;
        return true;
    }

    @Override
    public Boolean visit(BreakNode node) {
        if (!inForLoop) {
            ErrorReporting.reportError(
                new SemanticException(node.getSourceLoc(),
                "Break statement outside of for loop"));
        }
        
        defaultBehavior(node);
        return true;
    }

    @Override
    public Boolean visit(ContinueNode node) {
        if (!inForLoop) {
            ErrorReporting.reportError(
                new SemanticException(node.getSourceLoc(),
                "Continue statement outside of for loop"));
        }

        defaultBehavior(node);
        return true;
    }
}