package jp.mzw.revajaxmutator.fixer;

import java.util.Collection;

import org.mozilla.javascript.ast.AstNode;

import jp.mzw.ajaxmutator.mutatable.AttributeModification;
import jp.mzw.revajaxmutator.parser.RepairSource;

public class AttributeModificationValueERFixer extends AbstractReplacingAmongFixer<AttributeModification> {

	public AttributeModificationValueERFixer(Collection<AttributeModification> mutationTargets, Collection<? extends RepairSource> repairSources) {
		super(AttributeModification.class, mutationTargets, repairSources);
	}

	@Override
	protected AstNode getFocusedNode(AttributeModification node) {
		return node.getAttributeValue();
	}

}
