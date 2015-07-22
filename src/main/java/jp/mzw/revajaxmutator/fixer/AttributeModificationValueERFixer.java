package jp.mzw.revajaxmutator.fixer;

import java.util.Collection;

import org.mozilla.javascript.ast.AstNode;

import jp.gr.java_conf.daisy.ajax_mutator.mutatable.AttributeModification;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.replacing_among.AbstractReplacingAmongMutator;

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
