package jp.mzw.ajaxmutator.mutator.generic;

import java.util.List;
import java.util.ArrayList;

import jp.mzw.ajaxmutator.generator.Mutation;
import jp.mzw.ajaxmutator.mutatable.generic.VariableDec;
import jp.mzw.ajaxmutator.mutator.AbstractMutator;

public class RemoveVariableDeclarationMutator extends
		AbstractMutator<VariableDec> {

	public RemoveVariableDeclarationMutator() {
		super(VariableDec.class);
	}

	@Override
	public List<Mutation> generateMutationList(VariableDec originalNode) {
		List<Mutation> ret = new ArrayList<>();
		if ("ForLoop".equals(originalNode.getAstNode().getParent().shortName())
				|| "ForInLoop".equals(originalNode.getAstNode().getParent()
						.shortName())) {
			return null;
		} else {
			ret.add(new Mutation(originalNode.getAstNode(), ""));
			return ret;
		}
	}
}
