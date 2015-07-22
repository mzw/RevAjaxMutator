package jp.mzw.revajaxmutator.fixer;

import java.util.Collection;

import jp.gr.java_conf.daisy.ajax_mutator.mutatable.AttributeModification;

import org.mozilla.javascript.ast.AstNode;

public class AttributeModificationTargetVIFixer extends AbstractReplacingAmongFixer<AttributeModification> {

	public AttributeModificationTargetVIFixer(
            Collection<AttributeModification> mutationTargets, String[] parseResult) {
        super(AttributeModification.class, mutationTargets, parseResult);
    }

	@Override
	protected AstNode getFocusedNode(AttributeModification node) {
		return node.getTargetAttribute();
	}

}
