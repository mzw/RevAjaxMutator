package jp.mzw.ajaxmutator.detector.generic;

import jp.mzw.ajaxmutator.detector.AbstractDetector;
import jp.mzw.ajaxmutator.mutatable.generic.AssignmentExpression;

import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstNode;

public class AssignmentDetector extends AbstractDetector<AssignmentExpression> {

	public AssignmentExpression detect(AstNode node) {
		return detectFromAssignment(node, true);
	}

	@Override
	protected AssignmentExpression detectFromAssignment(Assignment assignment,
			AstNode left, AstNode right) {
		return new AssignmentExpression(assignment, left, right);
	}
}
