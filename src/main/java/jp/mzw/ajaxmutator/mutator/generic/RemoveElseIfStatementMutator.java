package jp.mzw.ajaxmutator.mutator.generic;

import java.util.ArrayList;
import java.util.List;

import jp.mzw.ajaxmutator.generator.Mutation;
import jp.mzw.ajaxmutator.mutatable.generic.If;
import jp.mzw.ajaxmutator.mutator.AbstractMutator;

public class RemoveElseIfStatementMutator extends AbstractMutator<If> {

	public RemoveElseIfStatementMutator() {
		super(If.class);
	}

	@Override
	public List<Mutation> generateMutationList(If originalNode) {
		List<Mutation> ret = new ArrayList<>();
		if (originalNode.getElse() != null) {
			ret.add(new Mutation(originalNode.getElse(), "{}"));
			return ret;
		}
		return null;
	}
}
