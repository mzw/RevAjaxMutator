package jp.mzw.ajaxmutator.mutator.generic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.mozilla.javascript.ast.AstNode;

import jp.mzw.ajaxmutator.generator.Mutation;
import jp.mzw.ajaxmutator.mutatable.generic.FuncNode;
import jp.mzw.ajaxmutator.mutator.AbstractMutator;

public class SwapFuncParamsMutator extends AbstractMutator<FuncNode> {

	public SwapFuncParamsMutator() {
		super(FuncNode.class);
	}

	@Override
	public List<Mutation> generateMutationList(FuncNode originalNode) {
		List<Mutation> ret = new ArrayList<>();

		List<AstNode> params = originalNode.getParams();
		String function = "";

		if (params.size() < 2) {
			return null;
		}

		Random x = new Random();
		int a = -1, b = -1;
		a = x.nextInt(params.size());
		while (a == b || b == -1) {
			b = x.nextInt(params.size());
		}

		function += "function " + originalNode.getName() + "(";
		for (int i = 0; i < params.size(); i++) {
			if (i != params.size() - 1) {
				if (i == a) {
					function += params.get(b).toSource() + ",";
				} else if (i == b) {
					function += params.get(a).toSource() + ",";
				} else {
					function += params.get(i).toSource() + ",";
				}
			} else {
				if (i == a) {
					function += params.get(b).toSource() + ")";
				} else if (i == b) {
					function += params.get(a).toSource() + ")";
				} else {
					function += params.get(i).toSource() + ")";
				}
			}
		}
		function += originalNode.getBody().toSource();

		ret.add(new Mutation(originalNode.getAstNode(), function));
		return ret;
	}
}
