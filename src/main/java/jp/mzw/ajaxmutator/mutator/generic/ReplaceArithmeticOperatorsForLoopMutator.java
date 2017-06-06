package jp.mzw.ajaxmutator.mutator.generic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstNode;

import jp.mzw.ajaxmutator.generator.Mutation;
import jp.mzw.ajaxmutator.mutatable.generic.AssignmentExpression;
import jp.mzw.ajaxmutator.mutatable.generic.For;
import jp.mzw.ajaxmutator.mutator.AbstractMutator;

public class ReplaceArithmeticOperatorsForLoopMutator extends
		AbstractMutator<For> {

	private boolean _random;

	public ReplaceArithmeticOperatorsForLoopMutator() {
		super(For.class);
		this._random = true;
	}

	public ReplaceArithmeticOperatorsForLoopMutator choose(final boolean random) {
		this._random = random;
		return this;
	}

	@Override
	public List<Mutation> generateMutationList(For originalNode) {
		List<Mutation> ret = new ArrayList<>();

		String[] operators = { "++", "−−", "+=", "−=" };
		List<Integer> trueoperators = new ArrayList<Integer>();
		String condition = originalNode.getIncrement().toSource();
		String interval = "INTERVAL___OF___CHANGE";
		Random x = new Random();
		int op1, op2, random;

		if (originalNode.getIncrement() instanceof Assignment) {
			Assignment assign = (Assignment) originalNode.getIncrement();
			AssignmentExpression assignmentexpression = new AssignmentExpression(
					assign, assign.getLeft(), assign.getRight());

			String[] operators2 = { "+", "−", "/", "*" };
			List<Integer> trueoperators2 = new ArrayList<Integer>();
			String str2;
			AstNode right2 = assignmentexpression.getRight();

			// Naive Implement (if call ReplaceArithAssignment from here, return
			// null due to branch(if the ast parent is in forloop, return null))
			if ("InfixExpression".equals(right2.shortName())) {

				str2 = right2.toSource();
				if ((!str2.contains("-") && !str2.contains("/") && !str2
						.contains("*"))
						&& (str2.contains("\"") || str2.contains("'"))) {
					return null;
				}

				for (int j = 0; j < 4; j++) {
					if (str2.contains(operators2[j])) {
						trueoperators2.add(j);
					}
				}
				if (trueoperators2.size() == 0) {
					return null;
				}

				if (this._random) {
					random = x.nextInt(trueoperators2.size());
					op1 = trueoperators2.get(random);
					op2 = -1;
					while (op1 == op2 || op2 == -1) {
						op2 = x.nextInt(4);
					}

					str2 = str2.replace(operators2[op1], interval);
					str2 = str2.replace(operators2[op2], operators2[op1]);
					str2 = str2.replace(interval, operators2[op2]);

					ret.add(new Mutation(right2, str2));
				} else {
					for (final String _op1 : operators2) {
						for (final String _op2 : operators2) {
							if (_op1.equals(_op2)) {
								continue;
							}
							String cond = right2.toSource();
							cond = cond.replace(_op1, interval);
							cond = cond.replace(_op2, _op1);
							cond = cond.replace(interval, _op2);
							if (right2.toSource().equals(cond)) {
								continue;
							}
							ret.add(new Mutation(right2, cond));
						}
					}
				}
				return ret;
			}

			return null;
		} else {
			// ///// Not Assignment Increment
			for (int i = 0; i < 4; i++) {
				if (condition.contains(operators[i])) {
					trueoperators.add(i);
				}
			}
			if (trueoperators.size() == 0) {
				return null;
			}

			if (this._random) {
				random = x.nextInt(trueoperators.size());
				op1 = trueoperators.get(random);
				op2 = -1;
				int ram_number = x.nextInt(100);
				String ram_number_str = "";
				while (op1 == op2 || op2 == -1) {
					op2 = x.nextInt(4);
				}
				if ((op1 == 0 || op1 == 1) && (op2 == 2 || op2 == 3)) {
					ram_number_str = "" + ram_number;
					condition = condition.replace(operators[op1], operators[op2]
							+ ram_number_str);
				} else if ((op1 == 2 || op1 == 3) && (op2 == 0 || op2 == 1)) {
					if (condition.indexOf(operators[op1]) != -1) {
						condition = condition.substring(0,
								condition.indexOf(operators[op1]) + 2);
						condition = condition.replace(operators[op1],
								operators[op2]);
					} else {
						return null;
					}
				} else {
					condition = condition.replace(operators[op1], operators[op2]);
				}

				ret.add(new Mutation(originalNode.getIncrement(), condition));
			} else {
				for (final String _op1 : new String[] {"++", "--"}) {
					for (final String _op2 : new String[] {"++", "--"}) {
						if (_op1.equals(_op2)) {
							continue;
						}
						String cond = originalNode.getIncrement().toSource();
						cond = cond.replace(_op1, interval);
						cond = cond.replace(_op2, _op1);
						cond = cond.replace(interval, _op2);
						if (originalNode.getIncrement().toSource().equals(cond)) {
							continue;
						}
						ret.add(new Mutation(originalNode.getIncrement(), cond));
					}
				}
				for (final String _op1 : new String[] {"+=", "-="}) {
					for (final String _op2 : new String[] {"+=", "-="}) {
						if (_op1.equals(_op2)) {
							continue;
						}
						String cond = originalNode.getIncrement().toSource();
						cond = cond.replace(_op1, interval);
						cond = cond.replace(_op2, _op1);
						cond = cond.replace(interval, _op2);
						if (originalNode.getIncrement().toSource().equals(cond)) {
							continue;
						}
						ret.add(new Mutation(originalNode.getIncrement(), cond));
					}
				}
			}
			return ret;
		}
	}
}
