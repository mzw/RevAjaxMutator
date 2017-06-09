package jp.mzw.ajaxmutator.detector.generic;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.VariableDeclaration;

import jp.mzw.ajaxmutator.detector.AbstractDetector;
import jp.mzw.ajaxmutator.mutatable.generic.VariableDec;

public class VariableDeclarationDetector extends AbstractDetector<VariableDec> {

	public VariableDec detect(AstNode node) {
		return detectFromVarAndFuncParam(node, true);
	}

	@Override
	protected VariableDec detectFromVarAndFuncParam(
			VariableDeclaration variabledeclaration) {
		return new VariableDec(variabledeclaration,
				variabledeclaration.getVariables());
	}
}
