package jp.mzw.revajaxmutator.fixer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jp.mzw.ajaxmutator.mutatable.Request;
import jp.mzw.ajaxmutator.util.StringToAst;

import org.mozilla.javascript.ast.AstNode;

/**
 * 
 * @author Junto Nakaoka
 *
 */
public class RequestURLVIFixer extends AbstractReplacingAmongFixer<Request> {

	public RequestURLVIFixer(Collection<Request> mutationTargets,
			List<RepairSource> repairSources) {
		super(Request.class, mutationTargets, repairSources);
	}

	public RequestURLVIFixer(Collection<Request> mutationTargets) {
		super(Request.class, mutationTargets, new ArrayList<RepairSource>());
	}

	@Override
	protected AstNode getFocusedNode(Request node) {
		return node.getUrl();
	}

	@Override
	public AstNode getDefaultReplacingNode() {
		return StringToAst.parseAsStringLiteral("'http://google.com'");
	}
}
