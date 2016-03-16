package jp.mzw.ajaxmutator.mutatable;

import org.mozilla.javascript.ast.AstNode;

/**
 * Dom creation such as document.createElement
 * 
 * @author Kazuki Nishiura
 */
public class DOMCreation extends Mutatable {
    private final AstNode tagName;

    public DOMCreation(AstNode node, AstNode tagName) {
        super(node);
        this.tagName = tagName;
    }

    public AstNode getTagName() {
        return tagName;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(super.toString()).append('\n');
        builder.append("  DOM creation: [tag:");
        builder.append(tagName.toSource()).append("]");
        return builder.toString();
    }
}
