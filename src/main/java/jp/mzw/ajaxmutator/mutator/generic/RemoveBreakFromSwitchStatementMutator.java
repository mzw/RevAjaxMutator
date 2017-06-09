package jp.mzw.ajaxmutator.mutator.generic;

import java.util.ArrayList;
import java.util.List;

import jp.mzw.ajaxmutator.generator.Mutation;
import jp.mzw.ajaxmutator.mutatable.generic.Switch;
import jp.mzw.ajaxmutator.mutator.AbstractMutator;

public class RemoveBreakFromSwitchStatementMutator extends AbstractMutator<Switch>{
	
	public RemoveBreakFromSwitchStatementMutator() {
		super(Switch.class);
	}

	@Override
	public List<Mutation> generateMutationList(Switch originalNode) {
		List<Mutation> ret = new ArrayList<>();
		
		String original = originalNode.getAstNode().toSource();
		
		// should change "break;" to "break[*;]"
		original = original.replace("break;", "");
		original = original.replace("break ;", "");
		
		ret.add(new Mutation(originalNode.getAstNode(),original));
		return ret;
	}
}
