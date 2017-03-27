package jp.mzw.ajaxmutator.mutator.generic;

import java.util.ArrayList;
import java.util.List;

import jp.mzw.ajaxmutator.generator.Mutation;
import jp.mzw.ajaxmutator.mutatable.generic.If;
import jp.mzw.ajaxmutator.mutator.AbstractMutator;

public class IfStatementMutator extends AbstractMutator<If>{
	
	public IfStatementMutator() {
		super(If.class);
	}

	@Override
	public List<Mutation> generateMutationList(If originalNode) {
		List<Mutation> ret = new ArrayList<>();
		ret.add(new Mutation(originalNode.getElse(),"{}"));
		return ret;
	}
}
