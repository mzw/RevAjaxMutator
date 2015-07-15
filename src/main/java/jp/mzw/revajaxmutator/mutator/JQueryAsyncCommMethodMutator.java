package jp.mzw.revajaxmutator.mutator;

import java.util.ArrayList;
import java.util.List;

import org.mozilla.javascript.ast.Name;

import jp.gr.java_conf.daisy.ajax_mutator.mutatable.Request;
import jp.gr.java_conf.daisy.ajax_mutator.mutation_generator.Mutation;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.AbstractMutator;

public class JQueryAsyncCommMethodMutator extends AbstractMutator<Request> {

	public JQueryAsyncCommMethodMutator() {
		super(Request.class);
	}

	@Override
	public List<Mutation> generateMutationList(Request originalNode) {
        Name requestMethodName = (Name) originalNode.getRequestMethodNode();
        String name = requestMethodName.getIdentifier();
        List<Mutation> mutationList = new ArrayList<Mutation>();
        if ("post".equals(name)) {
            mutationList.add(new Mutation(requestMethodName, "get"));
        } else if("get".equals(name)) {
            mutationList.add(new Mutation(requestMethodName, "post"));
        } else {
            mutationList.add(new Mutation(requestMethodName, name));
        }
        return mutationList;
	}

}
