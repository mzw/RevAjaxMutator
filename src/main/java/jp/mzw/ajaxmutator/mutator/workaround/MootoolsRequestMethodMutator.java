package jp.mzw.ajaxmutator.mutator.workaround;

import java.util.ArrayList;
import java.util.List;

import org.mozilla.javascript.ast.Name;

import jp.mzw.ajaxmutator.mutatable.Request;
import jp.mzw.ajaxmutator.generator.Mutation;
import jp.mzw.ajaxmutator.mutator.AbstractMutator;

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
