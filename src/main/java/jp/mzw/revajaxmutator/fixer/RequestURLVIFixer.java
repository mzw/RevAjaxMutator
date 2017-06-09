package jp.mzw.revajaxmutator.fixer;

import java.util.Collection;

import jp.mzw.ajaxmutator.mutatable.Request;
import jp.mzw.ajaxmutator.util.StringToAst;
import jp.mzw.revajaxmutator.parser.RepairSource;

import org.mozilla.javascript.ast.AstNode;

import com.google.common.collect.Sets;

/**
 * 
 * @author Junto Nakaoka
 *
 */
public class RequestURLVIFixer extends AbstractReplacingAmongFixer<Request> {

	public RequestURLVIFixer(final Collection<Request> mutationTargets, Collection<? extends RepairSource> repairSources) {
		super(Request.class, mutationTargets, repairSources);
	}

	public RequestURLVIFixer(final Collection<Request> mutationTargets) {
		super(Request.class, mutationTargets, Sets.newHashSet());
	}

	@Override
	protected AstNode getFocusedNode(final Request node) {
		return node.getUrl();
	}

	@Override
	public AstNode getDefaultReplacingNode(final AstNode focusedNode) {
		return StringToAst.parseAsStringLiteral("'http://google.com'");
	}
}
