package jp.mzw.ajaxmutator.mutator.generic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.mozilla.javascript.ast.AstNode;

import jp.mzw.ajaxmutator.generator.Mutation;
import jp.mzw.ajaxmutator.mutatable.generic.AssignmentExpression;
import jp.mzw.ajaxmutator.mutator.AbstractMutator;

public class ReplaceArithmeticOperatorsAssignmentMutator extends
		AbstractMutator<AssignmentExpression> {

	public ReplaceArithmeticOperatorsAssignmentMutator() {
		super(AssignmentExpression.class);
	}

	@Override
	public List<Mutation> generateMutationList(AssignmentExpression originalNode) {
		List<Mutation> ret = new ArrayList<>();

		if ("ForLoop".equals(originalNode.getAstNode().getParent().shortName())
				|| "ForInLoop".equals(originalNode.getAstNode().getParent()
						.shortName())) {
			return null;
		}

		String[] operators = { "+", "−", "/", "*" };
		List<Integer> trueoperators = new ArrayList<Integer>();
		String interval = "INTERVAL___OF___CHANGE";
		Random x = new Random();
		String str;

		AstNode right = originalNode.getRight();

		if ("InfixExpression".equals(right.shortName())) {

			str = right.toSource();
			if ((!str.contains("-") && !str.contains("/") && !str.contains("*"))
					&& (str.contains("\"") || str.contains("'"))) {
				return null;
			}

			for (int j = 0; j < 4; j++) {
				if (str.contains(operators[j])) {
					trueoperators.add(j);
				}
			}
			if (trueoperators.size() == 0) {
				return null;
			}

			int k = x.nextInt(trueoperators.size());
			int op1 = trueoperators.get(k), op2 = -1;
			while (op1 == op2 || op2 == -1) {
				op2 = x.nextInt(4);
			}

			str = str.replace(operators[op1], interval);
			str = str.replace(operators[op2], operators[op1]);
			str = str.replace(interval, operators[op2]);

			ret.add(new Mutation(right, str));
			return ret;
		}

		return null;
	}
}
