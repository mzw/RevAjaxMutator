package jp.mzw.ajaxmutator.mutator.generic;

import java.util.ArrayList;
import java.util.List;

import org.mozilla.javascript.ast.UnaryExpression;

import jp.mzw.ajaxmutator.generator.Mutation;
import jp.mzw.ajaxmutator.mutatable.generic.For;
import jp.mzw.ajaxmutator.mutator.AbstractMutator;

public class ReplacePlusMinusOperatorForLoopMutator extends
		AbstractMutator<For> {

	public ReplacePlusMinusOperatorForLoopMutator() {
		super(For.class);
	}

	@Override
	public List<Mutation> generateMutationList(For originalNode) {
		List<Mutation> ret = new ArrayList<>();
		String plus = "++";
		String minus = "--";

		if (originalNode.getIncrement().getType() == 106) {
			UnaryExpression unary = (UnaryExpression) originalNode
					.getIncrement();
			if (unary.isPostfix()) {
				ret.add(new Mutation(originalNode.getIncrement(), plus
						+ unary.getOperand().toSource()));
				return ret;
			} else if (unary.isPrefix()) {
				ret.add(new Mutation(originalNode.getIncrement(), unary
						.getOperand().toSource() + plus));
				return ret;
			}
		} else if (originalNode.getIncrement().getType() == 107) {
			UnaryExpression unary = (UnaryExpression) originalNode
					.getIncrement();
			if (unary.isPostfix()) {
				ret.add(new Mutation(originalNode.getIncrement(), minus
						+ unary.getOperand().toSource()));
				return ret;
			} else if (unary.isPrefix()) {
				ret.add(new Mutation(originalNode.getIncrement(), unary
						.getOperand().toSource() + minus));
				return ret;
			}
		}
		return null;
	}
}
