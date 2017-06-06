package jp.mzw.ajaxmutator.mutator.generic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.mozilla.javascript.ast.AstNode;

import jp.mzw.ajaxmutator.generator.Mutation;
import jp.mzw.ajaxmutator.mutatable.generic.VariableDec;
import jp.mzw.ajaxmutator.mutator.AbstractMutator;

public class ReplaceArithmeticOperatorsVariableDecMutator extends
		AbstractMutator<VariableDec> {

	private boolean random;

	public ReplaceArithmeticOperatorsVariableDecMutator() {
		super(VariableDec.class);
		this.random = true;
	}

	public ReplaceArithmeticOperatorsVariableDecMutator choose(final boolean random) {
		this.random = random;
		return this;
	}

	@Override
	public List<Mutation> generateMutationList(VariableDec originalNode) {
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
		AstNode initializer;

		for (int i = 0; i < originalNode.getValue().size(); i++) {
			if (originalNode.getValue().get(i).getInitializer() != null) {
				if ("InfixExpression".equals(originalNode.getValue().get(i)
						.getInitializer().shortName())) {
					initializer = originalNode.getValue().get(i)
							.getInitializer();
					str = initializer.toSource();
					// System.out.println(str);
					// System.out.println(initializer.shortName());
					if ((!str.contains("-") && !str.contains("/") && !str
							.contains("*"))
							&& (str.contains("\"") || str.contains("'"))) {
						return null;
					}

					for (int j = 0; j < 4; j++) {
						if (str.contains(operators[j])) {
							trueoperators.add(j);
						}
					}

					if (trueoperators.size() == 0) {
						continue;
					}

					if (this.random) {
						int k = x.nextInt(trueoperators.size());
						int op1 = trueoperators.get(k), op2 = -1;
						while (op1 == op2 || op2 == -1) {
							op2 = x.nextInt(4);
						}

						str = str.replace(operators[op1], interval);
						str = str.replace(operators[op2], operators[op1]);
						str = str.replace(interval, operators[op2]);

						ret.add(new Mutation(initializer, str));
						return ret;
					} else {
						for (final String op1 : operators) {
							for (final String op2 : operators) {
								if (op1.equals(op2)) {
									continue;
								}
								String cond = initializer.toSource();
								cond = cond.replace(op1, interval);
								cond = cond.replace(op2, op1);
								cond = cond.replace(interval, op2);
								if (initializer.toSource().equals(cond)) {
									continue;
								}
								ret.add(new Mutation(initializer, cond));
							}
						}
					}
				}
			}
		}
		if (0 < ret.size()) {
			return ret;
		}
		return null;
	}
}
