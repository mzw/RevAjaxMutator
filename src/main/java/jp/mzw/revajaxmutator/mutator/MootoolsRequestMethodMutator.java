package jp.mzw.revajaxmutator.mutator;

import org.mozilla.javascript.ast.Name;

import jp.gr.java_conf.daisy.ajax_mutator.mutatable.Request;
import jp.gr.java_conf.daisy.ajax_mutator.mutation_generator.Mutation;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.AbstractMutator;

public class MootoolsRequestMethodMutator extends AbstractMutator<Request> {
    public MootoolsRequestMethodMutator() {
        super(Request.class);
    }

    @Override
    public Mutation generateMutation(Request originalNode) {
        Name requestMethodName = (Name) originalNode.getRequestMethodNode();
        String name = requestMethodName.getIdentifier();
        if ("send".equals(name)) {
            return new Mutation(requestMethodName, "get");
        }
        else {
            return new Mutation(requestMethodName, "send");
        }
    }
}
