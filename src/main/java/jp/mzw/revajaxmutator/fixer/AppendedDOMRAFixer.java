package jp.mzw.revajaxmutator.fixer;

import java.util.Collection;

import jp.mzw.ajaxmutator.mutatable.DOMAppending;
import jp.mzw.revajaxmutator.parser.RepairSource;

import org.mozilla.javascript.ast.AstNode;

import com.google.common.collect.Sets;

/**
 * @Junto Nakaoka
 */
public class AppendedDOMRAFixer extends AbstractReplacingAmongFixer<DOMAppending> {
	public AppendedDOMRAFixer(Collection<DOMAppending> mutationTargets, Collection<? extends RepairSource> repairSources) {
		super(DOMAppending.class, mutationTargets, repairSources);
	}

	public AppendedDOMRAFixer(Collection<DOMAppending> mutationTargets) {
		super(DOMAppending.class, mutationTargets, Sets.newHashSet());
	}

	@Override
	protected AstNode getFocusedNode(DOMAppending node) {
		return node.getAppendedDom();
	}
}
