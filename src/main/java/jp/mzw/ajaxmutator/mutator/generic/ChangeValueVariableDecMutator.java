package jp.mzw.ajaxmutator.mutator.generic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.mozilla.javascript.Token;

import jp.mzw.ajaxmutator.generator.Mutation;
import jp.mzw.ajaxmutator.mutatable.generic.VariableDec;
import jp.mzw.ajaxmutator.mutator.AbstractMutator;

public class ChangeValueVariableDecMutator extends AbstractMutator<VariableDec> {

	public ChangeValueVariableDecMutator() {
		super(VariableDec.class);
	}

	@Override
	public List<Mutation> generateMutationList(VariableDec originalNode) {
		List<Mutation> ret = new ArrayList<>();
		if ("ForLoop".equals(originalNode.getAstNode().getParent().shortName())
				|| "ForInLoop".equals(originalNode.getAstNode().getParent()
						.shortName())) {
			return null;
		}

		for (int i = 0; i < originalNode.getValue().size(); i++) {
			if (originalNode.getValue().get(i).getInitializer() != null) {
				if (originalNode.getValue().get(i).getInitializer().getType() == Token.NUMBER) {
					Random x = new Random();
					ret.add(new Mutation(originalNode.getValue().get(i)
							.getInitializer(), "" + (5 + x.nextInt(100))));
					return ret;
				}
			}
		}
		return null;
	}
}
