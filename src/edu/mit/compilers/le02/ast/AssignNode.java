package edu.mit.compilers.le02.ast;

import java.util.List;

import edu.mit.compilers.le02.SourceLocation;
import edu.mit.compilers.le02.Util;

public class AssignNode extends StatementNode {
  protected LocationNode loc;
  protected ExpressionNode value;

  public AssignNode(SourceLocation sl,
                    LocationNode loc, ExpressionNode value) {
    super(sl);
    this.loc = loc;
    this.value = value;
  }

  @Override
  public List<ASTNode> getChildren() {
    return Util.makeList(loc, value);
  }

  @Override
  public boolean replaceChild(ASTNode prev, ASTNode next) {
    if ((loc == prev) && (next instanceof LocationNode)) {
      loc = (LocationNode)next;
      loc.setParent(this);
      return true;
    } else if ((value == prev) && (next instanceof ExpressionNode)) {
      value = (ExpressionNode)next;
      value.setParent(this);
      return true;
    }
    return false;
  }

  public LocationNode getLoc() {
    return loc;
  }

  public void setLoc(LocationNode loc) {
    this.loc = loc;
  }

  public ExpressionNode getValue() {
    return value;
  }

  public void setValue(ExpressionNode value) {
    this.value = value;
  }

  @Override
  public <T> T accept(ASTNodeVisitor<T> v) { return v.visit(this); }
}
