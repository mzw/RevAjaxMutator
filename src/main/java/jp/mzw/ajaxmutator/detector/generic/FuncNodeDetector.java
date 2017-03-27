package jp.mzw.ajaxmutator.detector.generic;

import java.util.List;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionNode;

import jp.mzw.ajaxmutator.detector.AbstractDetector;
import jp.mzw.ajaxmutator.mutatable.generic.FuncNode;

public class FuncNodeDetector extends AbstractDetector<FuncNode> {

	public FuncNode detect(AstNode node) {
		return detectFromFunction(node, true);
	}

	@Override
	protected FuncNode detectFromFunction(FunctionNode functionnode,
			List<AstNode> params, AstNode body, String name, AstNode member) {
		return new FuncNode(functionnode, params, body, name, member);
	}
}
