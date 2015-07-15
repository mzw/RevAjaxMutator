package jp.mzw.revajaxmutator.mutator;

import java.util.ArrayList;
import java.util.List;

import org.mozilla.javascript.ast.Name;

import jp.gr.java_conf.daisy.ajax_mutator.mutatable.Request;
import jp.gr.java_conf.daisy.ajax_mutator.mutation_generator.Mutation;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.AbstractMutator;

public class MootoolsRequestMethodMutator extends AbstractMutator<Request> {
    public MootoolsRequestMethodMutator() {
        super(Request.class);
    }

    @Override
    public List<Mutation> generateMutationList(Request originalNode) {
        Name requestMethodName = (Name) originalNode.getRequestMethodNode();
        String name = requestMethodName.getIdentifier();
        List<Mutation> mutationList = new ArrayList<Mutation>();
        if ("send".equals(name)) {
            mutationList.add(new Mutation(requestMethodName, "get"));
        }
        else {
            mutationList.add(new Mutation(requestMethodName, "send"));
        }
        return mutationList;
    }
}
