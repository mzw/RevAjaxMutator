package jp.mzw.ajaxmutator.mutator.generic;

import java.util.ArrayList;
import java.util.List;

import jp.mzw.ajaxmutator.generator.Mutation;
import jp.mzw.ajaxmutator.mutatable.generic.While;
import jp.mzw.ajaxmutator.mutator.AbstractMutator;

public class ReplaceLogicalOperatorsWhileLoopMutator extends
		AbstractMutator<While> {

	public ReplaceLogicalOperatorsWhileLoopMutator() {
		super(While.class);
	}

	@Override
	public List<Mutation> generateMutationList(While originalNode) {
		List<Mutation> ret = new ArrayList<>();
		String operator = originalNode.getCondition().toSource();
		if (!operator.contains("||") && !operator.contains("&&")) {
			return null;
		}
		String interval = "INTERVAL___OF___CHANGE";
		operator = operator.replace("||", interval);
		operator = operator.replace("&&", "||");
		operator = operator.replace(interval, "&&");
		ret.add(new Mutation(originalNode.getCondition(), operator));
		return ret;
	}
}
