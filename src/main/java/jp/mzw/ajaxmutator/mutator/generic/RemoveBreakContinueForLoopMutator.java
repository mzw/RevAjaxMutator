package jp.mzw.ajaxmutator.mutator.generic;

import java.util.ArrayList;
import java.util.List;

import jp.mzw.ajaxmutator.generator.Mutation;
import jp.mzw.ajaxmutator.mutatable.generic.For;
import jp.mzw.ajaxmutator.mutator.AbstractMutator;

public class RemoveBreakContinueForLoopMutator extends AbstractMutator<For> {

	public RemoveBreakContinueForLoopMutator() {
		super(For.class);
	}

	@Override
	public List<Mutation> generateMutationList(For originalNode) {
		List<Mutation> ret = new ArrayList<>();

		String original = originalNode.getAstNode().toSource();
		int i = 0;
		if (original.contains("break") && original.contains("continue")) {
			i = 1;
		} else if (original.contains("break")) {
			i = 2;
		} else if (original.contains("continue")) {
			i = 3;
		}

		switch (i) {
		case 2:
			original = original.replace("break;", "");
			original = original.replace("break ;", "");
			ret.add(new Mutation(originalNode.getAstNode(), original));
			return ret;
		case 3:
			original = original.replace("continue;", "");
			original = original.replace("continue ;", "");
			ret.add(new Mutation(originalNode.getAstNode(), original));
			return ret;
		case 1:
			if (0.5 < Math.random()) {
				original = original.replace("break;", "");
				original = original.replace("break ;", "");
			} else {
				original = original.replace("continue;", "");
				original = original.replace("continue ;", "");
			}
			ret.add(new Mutation(originalNode.getAstNode(), original));
			return ret;
		}
		return null;

	}
}
