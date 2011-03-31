package edu.mit.compilers.le02.dfa;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;
import edu.mit.compilers.le02.DecafType;
import edu.mit.compilers.le02.GlobalLocation;
import edu.mit.compilers.le02.StackLocation;
import edu.mit.compilers.le02.Util;
import edu.mit.compilers.le02.VariableLocation;
import edu.mit.compilers.le02.cfg.Argument;
import edu.mit.compilers.le02.cfg.BasicBlockNode;
import edu.mit.compilers.le02.cfg.BasicStatement;
import edu.mit.compilers.le02.cfg.CallStatement;
import edu.mit.compilers.le02.cfg.ConstantArgument;
import edu.mit.compilers.le02.cfg.OpStatement;
import edu.mit.compilers.le02.cfg.OpStatement.AsmOp;
import edu.mit.compilers.le02.dfa.AvailableExpressions.BlockItem;
import edu.mit.compilers.le02.symboltable.AnonymousDescriptor;
import edu.mit.compilers.le02.symboltable.FieldDescriptor;


public class AvailableExpressionsTest extends TestCase {

  private VariableLocation makeLoc(String name) {
    return new GlobalLocation(name);
  }

  private VariableLocation makeLocalLoc(int offset) {
    return new StackLocation(offset);
  }

  private OpStatement makeExpr(AsmOp op, String arg1, String arg2, String res) {
    GlobalLocation loc = new GlobalLocation(arg1);
    GlobalLocation loc2 = new GlobalLocation(arg2);
    return new OpStatement(null, op,
                           Argument.makeArgument(new AnonymousDescriptor(loc)),
                           Argument.makeArgument(new AnonymousDescriptor(loc2)),
                           new FieldDescriptor(null, res, DecafType.INT));
  }


  private OpStatement makeLocalDef(int offset, int value) {
    StackLocation loc = new StackLocation(offset);
    return new OpStatement(null, AsmOp.MOVE,
                           Argument.makeArgument(value),
                           Argument.makeArgument(new AnonymousDescriptor(loc)),
                           null);
  }

  /**
   * Test that the reaching definitions thing works for a single
   * block.
   */
  public void testSmoke() {

    BasicBlockNode node = new BasicBlockNode("main", "main");
    OpStatement expr = makeExpr(AsmOp.ADD, "var", "var2", "var3");

    node.addStatement(expr);

    AvailableExpressions exprs = new AvailableExpressions(node);
    BlockItem bi = exprs.getExpressions(node);
    assertNotNull(bi);
    assertTrue(bi.getInExpressions().isEmpty());

    List<BasicStatement> nodeDefs = bi.getOutExpressions();
    assertEquals(1, nodeDefs.size());
    assertSame(expr, nodeDefs.get(0));
  }

  /**
   * Test that expressions are available only if available on two branches
   */
  public void testOverride() {
    BasicBlockNode top = new BasicBlockNode("main", "main");
    BasicBlockNode left = new BasicBlockNode("block1", "main");
    BasicBlockNode right = new BasicBlockNode("block2", "main");
    BasicBlockNode end = new BasicBlockNode("block3", "main");

    top.setNext(left);
    top.setBranchTarget(right);
    left.setNext(end);
    right.setNext(end);

    top.addStatement(makeExpr(AsmOp.ADD, "var1", "var2", "res"));
    top.addStatement(makeExpr(AsmOp.SUBTRACT, "var3", "var4", "res2"));
    
    left.addStatement(makeExpr(AsmOp.MULTIPLY, "x", "y", "var1"));
    left.addStatement(makeExpr(AsmOp.MODULO, "var1", "z", "res3"));
    
    right.addStatement(makeExpr(AsmOp.DIVIDE, "x", "z", "res3"));
    right.addStatement(makeExpr(AsmOp.MODULO, "var1", "z", "res4"));

    AvailableExpressions exprs = new AvailableExpressions(top);

    BlockItem bi;
    List<BasicStatement> nodeDefs;

    // Check top block
    bi = exprs.getExpressions(top);
    assertNotNull(bi);
    nodeDefs = bi.getOutExpressions();
    assertEquals(2, nodeDefs.size());

    bi = exprs.getExpressions(left);
    assertNotNull(bi);
    nodeDefs = bi.getOutExpressions();
    System.out.println(nodeDefs);
    assertEquals(3, nodeDefs.size());
    assertTrue(isAvailable(bi, AsmOp.ADD, "var1", "var2"));
    assertTrue(isAvailable(bi, AsmOp.SUBTRACT, "var3", "var4"));

    bi = exprs.getExpressions(right);
    assertNotNull(bi);
    nodeDefs = bi.getOutExpressions();
    assertEquals(4, nodeDefs.size());
    assertTrue(isAvailable(bi, AsmOp.ADD, "var1", "var2"));
    assertTrue(isAvailable(bi, AsmOp.SUBTRACT, "var3", "var4"));
    
    bi = exprs.getExpressions(end);
    assertNotNull(bi);
    System.out.println(bi.getInExpressions());
    System.out.println(bi.gen());
    System.out.println(bi.kill());
    assertFalse(isAvailable(bi, AsmOp.ADD, "var1", "var2"));
    assertTrue(isAvailable(bi, AsmOp.SUBTRACT, "var3", "var4"));
    assertFalse(isAvailable(bi, AsmOp.MULTIPLY, "x", "y"));
    assertFalse(isAvailable(bi, AsmOp.DIVIDE, "y", "z"));
    assertTrue(isAvailable(bi, AsmOp.MODULO, "var1", "z"));
  }

  private boolean isAvailable(BlockItem bi, 
                              AsmOp op, String arg1, String arg2) {
    
    OpStatement expr = makeExpr(op, arg1, arg2, "dummy");

    return bi.expressionIsAvailable(expr);
  }

}