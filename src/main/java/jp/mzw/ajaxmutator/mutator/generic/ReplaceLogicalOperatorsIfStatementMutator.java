package jp.mzw.ajaxmutator.mutator.generic;

import java.util.ArrayList;
import java.util.List;

import jp.mzw.ajaxmutator.generator.Mutation;
import jp.mzw.ajaxmutator.mutatable.generic.If;
import jp.mzw.ajaxmutator.mutator.AbstractMutator;

public class ReplaceLogicalOperatorsIfStatementMutator extends
		AbstractMutator<If> {

	public ReplaceLogicalOperatorsIfStatementMutator() {
		super(If.class);
	}

	@Override
	public List<Mutation> generateMutationList(If originalNode) {
		List<Mutation> ret = new ArrayList<>();
		String operator = originalNode.getValue().toSource();
		if (!operator.contains("||") && !operator.contains("&&")) {
			return null;
		}
		String interval = "INTERVAL___OF___CHANGE";
		operator = operator.replace("||", interval);
		operator = operator.replace("&&", "||");
		operator = operator.replace(interval, "&&");
		ret.add(new Mutation(originalNode.getValue(), operator));
		return ret;
	}
}
