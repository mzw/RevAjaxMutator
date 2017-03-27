package jp.mzw.ajaxmutator.mutator.generic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jp.mzw.ajaxmutator.generator.Mutation;
import jp.mzw.ajaxmutator.mutatable.generic.If;
import jp.mzw.ajaxmutator.mutator.AbstractMutator;

public class ReplaceRelationalOperatorsIfStatementMutator extends
		AbstractMutator<If> {

	public ReplaceRelationalOperatorsIfStatementMutator() {
		super(If.class);
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
		return ret;
	}
}
