package jp.mzw.ajaxmutator.mutator.generic;

import java.util.ArrayList;
import java.util.List;

import jp.mzw.ajaxmutator.generator.Mutation;
import jp.mzw.ajaxmutator.mutatable.generic.Switch;
import jp.mzw.ajaxmutator.mutator.AbstractMutator;

public class ChangeConditionSwitchStatementMutator extends
		AbstractMutator<Switch> {

	public ChangeConditionSwitchStatementMutator() {
		super(Switch.class);
	}

	@Override
	public List<Mutation> generateMutationList(Switch originalNode) {
		List<Mutation> ret = new ArrayList<>();
		if (originalNode.getCases().size() > 1) {
			String a = originalNode.getCases().get(0).getExpression()
					.toSource();
			String b = originalNode.getCases().get(1).getExpression()
					.toSource();
			String interval = "INTERVAL___OF___CHANGE+++";
			String original = originalNode.getAstNode().toSource();
			original = original.replace("case " + a, interval);
			original = original.replace("case " + b, "case " + a);
			original = original.replace(interval, "case " + b);
			ret.add(new Mutation(originalNode.getAstNode(), original));
			return ret;
		}
		return null;
	}

}
