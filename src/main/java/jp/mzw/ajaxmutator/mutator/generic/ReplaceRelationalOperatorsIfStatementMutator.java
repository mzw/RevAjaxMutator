package jp.mzw.ajaxmutator.mutator.generic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jp.mzw.ajaxmutator.generator.Mutation;
import jp.mzw.ajaxmutator.mutatable.generic.If;
import jp.mzw.ajaxmutator.mutator.AbstractMutator;

public class ReplaceRelationalOperatorsIfStatementMutator extends
		AbstractMutator<If> {

	private boolean random;

	public ReplaceRelationalOperatorsIfStatementMutator() {
		super(If.class);
		this.random = true;
	}

	public ReplaceRelationalOperatorsIfStatementMutator choose(final boolean random) {
		this.random = random;
		return this;
	}

	@Override
	public List<Mutation> generateMutationList(If originalNode) {
		List<Mutation> ret = new ArrayList<>();

		String[] operators = { ">", "<", ">=", "<=", "==", "!=", "===", "!==" };
		List<Integer> trueoperators = new ArrayList<Integer>();
		String condition = originalNode.getValue().toSource();
		String interval = "INTERVAL___OF___CHANGE";
		Random x = new Random();

		for (int i = 0; i < 8; i++) {
			if (condition.contains(operators[i])) {
				trueoperators.add(i);
			}
		}

		if (trueoperators.size() == 0) {
			return null;
		}

		if (this.random) {
			int i = x.nextInt(trueoperators.size());
			int op1 = trueoperators.get(i), op2 = -1;
			while (op1 == op2 || op2 == -1) {
				if (op1 > 5) {
					op2 = x.nextInt(2) + 4;
				} else if (op1 > 3) {
					op2 = x.nextInt(2) + 6;
				} else {
					op2 = x.nextInt(8);
				}
			}

			condition = condition.replace(operators[op1], interval);
			condition = condition.replace(operators[op2], operators[op1]);
			condition = condition.replace(interval, operators[op2]);

			ret.add(new Mutation(originalNode.getValue(), condition));
		} else {
			for (final String op1 : operators) {
				for (final String op2 : operators) {
					if (op1.equals(op2)) {
						continue;
					}
					String cond = originalNode.getValue().toSource();
					cond = cond.replace(op1, interval);
					cond = cond.replace(op2, op1);
					cond = cond.replace(interval, op2);
					if (originalNode.getValue().toSource().equals(cond)) {
						continue;
					}
					ret.add(new Mutation(originalNode.getValue(), cond));
				}
			}
		}
		return ret;
	}
}
