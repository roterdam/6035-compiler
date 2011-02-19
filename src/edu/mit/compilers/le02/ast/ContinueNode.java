package edu.mit.compilers.le02.ast;

import java.util.Collections;
import java.util.List;

public final class ContinueNode extends StatementNode {

	public ContinueNode(SourceLocation sl) {
		super(sl);
	}

	@Override
	public List<ASTNode> getChildren() {
		return Collections.emptyList();
	}

}
