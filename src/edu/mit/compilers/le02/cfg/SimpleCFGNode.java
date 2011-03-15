package edu.mit.compilers.le02.cfg;

import java.util.HashSet;
import java.util.Set;

import edu.mit.compilers.le02.cfg.BasicStatement.BasicStatementType;

public final class SimpleCFGNode implements CFGNode {
  private Set<CFGNode> predecessors;
  private SimpleCFGNode next;
  private SimpleCFGNode branchTarget;
  private BasicStatement statement;
  private Argument result;
  
  public SimpleCFGNode(BasicStatement statement) {
    this.statement = statement;
    this.predecessors = new HashSet<CFGNode>();
    
    if (this.statement.type == BasicStatementType.ARGUMENT) {
      this.result = ((ArgumentStatement) statement).getArgument();
    }
    else {
      this.result = Argument.makeArgument(statement.getResult());
    }
  }

  @Override
  public SimpleCFGNode getBranchTarget() {
    return branchTarget;
  }
  
  public void setBranchTarget(SimpleCFGNode node) {
    if (this.branchTarget != null) {
      this.branchTarget.predecessors.remove(this);
    }
    
    branchTarget = node;
    branchTarget.predecessors.add(this);
  }

  @Override
  public BasicStatement getConditional() {
    if (!isBranch()) {
      return null;
    }
    
    return statement;
  }

  @Override
  public SimpleCFGNode getNext() {
    return next;
  }
  
  public void setNext(SimpleCFGNode node) {
    if (next != null) {
      next.predecessors.remove(this);
    }
    next = node;
    node.predecessors.add(this);
  }

  @Override
  public boolean isBranch() {
    return branchTarget != null;
  }

  public BasicStatement getStatement() {
    return statement;
  }
  
  public Argument getResult() {
    return result;
  }
  
  public boolean hasMultipleEntrances() {
    return predecessors.size() > 1;
  }

}