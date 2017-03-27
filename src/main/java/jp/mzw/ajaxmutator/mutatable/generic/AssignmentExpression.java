package jp.mzw.ajaxmutator.mutatable.generic;

import jp.mzw.ajaxmutator.mutatable.Mutatable;

import org.mozilla.javascript.ast.AstNode;

public class AssignmentExpression extends Mutatable {

    private AstNode left;
    private AstNode right;

    public AssignmentExpression(AstNode node, AstNode left, AstNode right) {
        super(node);
        this.left = left;
        this.right = right;
    }

    public AstNode getLeft() {
        return left;
    }
    
    public AstNode getRight() {
    	return right;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append(super.toString()).append('\n')
                .append("  Assignment left: ").append(left).append("")
                .append("  Assignment right: ").append(right).append("")
                .toString();
    }
    
}
