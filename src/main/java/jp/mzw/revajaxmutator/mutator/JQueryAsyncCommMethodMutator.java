package jp.mzw.revajaxmutator.mutator;

import org.mozilla.javascript.ast.Name;

import jp.gr.java_conf.daisy.ajax_mutator.mutatable.Request;
import jp.gr.java_conf.daisy.ajax_mutator.mutation_generator.Mutation;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.AbstractMutator;

public class JQueryAsyncCommMethodMutator extends AbstractMutator<Request> {

	public JQueryAsyncCommMethodMutator() {
		super(Request.class);
	}

	@Override
	public Mutation generateMutation(Request originalNode) {
        Name requestMethodName = (Name) originalNode.getRequestMethodNode();
        String name = requestMethodName.getIdentifier();
        if ("post".equals(name)) {
            return new Mutation(requestMethodName, "get");
        } else if("get".equals(name)) {
            return new Mutation(requestMethodName, "post");
        } else {
            return new Mutation(requestMethodName, name);
        }
	}

}
