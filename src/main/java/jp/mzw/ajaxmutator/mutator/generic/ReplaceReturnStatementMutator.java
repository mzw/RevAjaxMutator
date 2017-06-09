package jp.mzw.ajaxmutator.mutator.generic;

import java.util.ArrayList;
import java.util.List;

import org.mozilla.javascript.ast.AstNode;

import jp.mzw.ajaxmutator.generator.Mutation;
import jp.mzw.ajaxmutator.mutatable.generic.Return;
import jp.mzw.ajaxmutator.mutator.AbstractMutator;

public class ReplaceReturnStatementMutator extends AbstractMutator<Return> {

	public ReplaceReturnStatementMutator() {
		super(Return.class);
	}

	@Override
	public List<Mutation> generateMutationList(Return originalNode) {
		List<Mutation> ret = new ArrayList<>();
		if (originalNode.getValue() != null) {
			AstNode returnValue = originalNode.getValue();
			String name = returnValue.toSource();
			if ("true".equals(name)) {
				ret.add(new Mutation(returnValue, "false"));
				return ret;
			} else if ("false".equals(name)) {
				ret.add(new Mutation(returnValue, "true"));
				return ret;
			}
			return null;
		}
		return null;
	}
}
