package jp.mzw.revajaxmutator.fixer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jp.mzw.ajaxmutator.mutatable.AttributeModification;
import jp.mzw.revajaxmutator.parser.RepairSource;

import org.mozilla.javascript.ast.AstNode;

public class AttributeModificationTargetVIFixer extends
		AbstractReplacingAmongFixer<AttributeModification> {

	public AttributeModificationTargetVIFixer(
			Collection<AttributeModification> mutationTargets,
			List<RepairSource> repairSources) {
		super(AttributeModification.class, mutationTargets, repairSources);
	}

	public AttributeModificationTargetVIFixer(
			Collection<AttributeModification> mutationTargets) {
		super(AttributeModification.class, mutationTargets,
				new ArrayList<RepairSource>());
	}

	@Override
	protected AstNode getFocusedNode(AttributeModification node) {
		return node.getTargetAttribute();
	}

}
