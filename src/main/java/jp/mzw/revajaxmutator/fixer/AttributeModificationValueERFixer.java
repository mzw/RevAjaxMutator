package jp.mzw.revajaxmutator.fixer;

import java.util.Collection;

import org.mozilla.javascript.ast.AstNode;

import jp.mzw.ajaxmutator.mutatable.AttributeModification;

public class AttributeModificationValueERFixer extends AbstractReplacingAmongFixer<AttributeModification> {

	public AttributeModificationValueERFixer(
            Collection<AttributeModification> mutationTargets, String[] parseResult) {
        super(AttributeModification.class, mutationTargets, parseResult);
    }

	@Override
	protected AstNode getFocusedNode(AttributeModification node) {
		return node.getAttributeValue();
	}

}
