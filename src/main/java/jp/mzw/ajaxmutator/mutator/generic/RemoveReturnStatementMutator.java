package jp.mzw.ajaxmutator.mutator.generic;

import java.util.ArrayList;
import java.util.List;

import jp.mzw.ajaxmutator.generator.Mutation;
import jp.mzw.ajaxmutator.mutatable.generic.Return;
import jp.mzw.ajaxmutator.mutator.AbstractMutator;

public class RemoveReturnStatementMutator extends AbstractMutator<Return> {

	public RemoveReturnStatementMutator() {
		super(Return.class);
	}

	@Override
	public List<Mutation> generateMutationList(Return originalNode) {
		List<Mutation> ret = new ArrayList<>();
		ret.add(new Mutation(originalNode.getAstNode(), ""));
		return ret;
	}
}
