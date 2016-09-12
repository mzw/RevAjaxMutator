package jp.mzw.ajaxmutator.mutator.generic;

import java.util.ArrayList;
import java.util.List;

import jp.mzw.ajaxmutator.generator.Mutation;
import jp.mzw.ajaxmutator.mutatable.generic.FuncNode;
import jp.mzw.ajaxmutator.mutator.AbstractMutator;

public class RemoveParamsFromFuncNodeMutator extends AbstractMutator<FuncNode> {

	public RemoveParamsFromFuncNodeMutator() {
		super(FuncNode.class);
	}

	@Override
	public List<Mutation> generateMutationList(FuncNode originalNode) {
		List<Mutation> ret = new ArrayList<>();
		if (originalNode.getParams().size() == 0
				|| originalNode.getParams().size() > 1) {
			return null;
		}
		ret.add(new Mutation(originalNode.getParams().get(0), ""));
		return ret;
	}
}
