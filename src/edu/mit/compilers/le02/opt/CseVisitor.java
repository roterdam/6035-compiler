package edu.mit.compilers.le02.opt;

import java.util.HashMap;
import java.util.Map;

import edu.mit.compilers.le02.cfg.BasicBlockNode;
import edu.mit.compilers.le02.cfg.BasicStatement;
import edu.mit.compilers.le02.cfg.OpStatement;
import edu.mit.compilers.le02.cfg.BasicStatement.BasicStatementType;
import edu.mit.compilers.le02.cfg.OpStatement.AsmOp;
import edu.mit.compilers.le02.symboltable.LocalDescriptor;
import edu.mit.compilers.le02.symboltable.TypedDescriptor;

public class CseVisitor extends BasicBlockVisitor {
  private static class Value {
    private int index;
    private Value(int idx) {
      index = idx;
    }
    public int getIndex() {
      return index;
    }
    @Override
    public String toString() {
      return "" + index;
    }
    @Override
    public boolean equals(Object o) {
      return (o instanceof Value && ((Value)o).index == index);
    }
    @Override
    public int hashCode() {
      return index;
    }

    private static int nextIndex = 0;
    public static Value nextIndex() {
      Value ret = new Value(nextIndex);
      nextIndex++;
      return ret;
    }
  }

  private class ValExp {
    private AsmOp op;
    private Value left;
    private Value right;
    public ValExp(AsmOp op, Value left, Value right) {
      this.op = op;
      this.left = left;
      this.right = right;
    }
    @Override
    public boolean equals(Object o) {
      if (!(o instanceof ValExp)) {
        return false;
      }
      ValExp other = (ValExp)o;
      return (other.op == op && other.left == left && other.right == right);
    }
    @Override
    public int hashCode() {
      return (left.hashCode() + right.hashCode() + op.hashCode());
    }
  }

  private Map<TypedDescriptor, Value> varToVal =
    new HashMap<TypedDescriptor, Value>();
  private Map<ValExp, Value> expToVal = new HashMap<ValExp, Value>();
  private Map<ValExp, LocalDescriptor> expToTmp =
    new HashMap<ValExp, LocalDescriptor>();

  @Override
  protected void processNode(BasicBlockNode node) {
    for (BasicStatement stmt : node.getStatements()) {
      if (stmt.getType() != BasicStatementType.OP) {
        continue;
      }
      OpStatement op = (OpStatement)stmt;
      switch (op.getOp()) {
       case MOVE:
        // Doesn't quite work yet. requires OpStatements to care not just
        // about VariableLocations but also about root TypedDescriptors.
        //varToVal.put(op.getArg2(), Value.nextIndex());
        break;
       case RETURN:
       case ENTER:
        continue;
       default:
        //varToVal.put(op.getResult(), Value.nextIndex());
      }
    }
  }
}