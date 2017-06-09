package jp.mzw.revajaxmutator.fixer;

import java.util.Collection;

import jp.mzw.ajaxmutator.mutatable.AttributeModification;
import jp.mzw.revajaxmutator.parser.RepairSource;

import org.mozilla.javascript.ast.AstNode;

import com.google.common.collect.Sets;

public class AttributeModificationTargetVIFixer extends AbstractReplacingAmongFixer<AttributeModification> {

	public AttributeModificationTargetVIFixer(Collection<AttributeModification> mutationTargets, Collection<? extends RepairSource> repairSources) {
		super(AttributeModification.class, mutationTargets, repairSources);
	}

	public AttributeModificationTargetVIFixer(Collection<AttributeModification> mutationTargets) {
		super(AttributeModification.class, mutationTargets, Sets.newHashSet());
	}

	@Override
	protected AstNode getFocusedNode(AttributeModification node) {
		return node.getTargetAttribute();
	}

}
