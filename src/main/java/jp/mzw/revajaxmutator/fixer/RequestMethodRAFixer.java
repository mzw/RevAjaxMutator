package jp.mzw.revajaxmutator.fixer;

import jp.mzw.ajaxmutator.detector.jquery.JQueryRequestDetector;
import jp.mzw.ajaxmutator.mutatable.Request;
import jp.mzw.ajaxmutator.util.StringToAst;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.StringLiteral;

import com.google.common.collect.Sets;

import java.util.Collection;

/**
 * @author Junto Nakaoka
 */
public class RequestMethodRAFixer extends AbstractReplacingAmongFixer<Request> {

	public RequestMethodRAFixer(final Collection<Request> mutationTargets) {
		super(Request.class, mutationTargets, Sets.newHashSet());
	}

	@Override
	protected AstNode getFocusedNode(final Request node) {
		return node.getRequestMethodNode();
	}

	@Override
	protected String formatAccordingTo(final AstNode mutatingNode, final AstNode mutatedNode) {
		if (mutatedNode instanceof StringLiteral && !(mutatingNode instanceof StringLiteral)) {
			// mutated: $.ajax(.., 'POST', ...)
			// mutating: $.get(...)
			return '"' + mutatingNode.toSource().toUpperCase() + '"';
		} else if (mutatingNode instanceof StringLiteral && JQueryRequestDetector.AJAX_SHORTCUT_METHODS.contains(mutatedNode.toSource().trim())) {
			// mutated: $.get(...)
			// mutating: $.ajax(.., 'POST', ...)
			return ((StringLiteral) mutatingNode).getValue();
		}
		return super.formatAccordingTo(mutatingNode, mutatedNode);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @param focusedNode AstNode currently focused
	 * @return get if focusedNode is post, vice versa.
	 */
	@Override
	public AstNode getDefaultReplacingNode(final AstNode focusedNode) {
		if (focusedNode.toSource().contains("get")) {
			return StringToAst.parseAsStringLiteral("'post'");
		} else if (focusedNode.toSource().contains("post")) {
			return StringToAst.parseAsStringLiteral("'get'");
		}
		return super.getDefaultReplacingNode(focusedNode);
	}
}
