package jp.mzw.revajaxmutator.fixer;

import java.util.Collection;
import java.util.List;

import org.mozilla.javascript.ast.AstNode;

import jp.mzw.ajaxmutator.mutatable.AttributeModification;

public class AttributeModificationValueERFixer extends
		AbstractReplacingAmongFixer<AttributeModification> {

	public AttributeModificationValueERFixer(
			Collection<AttributeModification> mutationTargets,
			List<RepairSource> repairSources) {
		super(AttributeModification.class, mutationTargets, repairSources);
	}

	@Override
	protected AstNode getFocusedNode(AttributeModification node) {
		return node.getAttributeValue();
	}

}
