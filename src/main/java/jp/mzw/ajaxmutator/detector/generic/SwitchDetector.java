package jp.mzw.ajaxmutator.detector.generic;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.SwitchStatement;

import jp.mzw.ajaxmutator.detector.AbstractDetector;
import jp.mzw.ajaxmutator.mutatable.generic.Switch;

public class SwitchDetector extends AbstractDetector<Switch> {

	public Switch detect(AstNode node) {
		return detectFromBranch(node, true);
	}

	@Override
	protected Switch detectFromBranch(SwitchStatement switchstatement) {
		return new Switch(switchstatement, switchstatement.getExpression(),
				switchstatement.getCases());
	}
}
