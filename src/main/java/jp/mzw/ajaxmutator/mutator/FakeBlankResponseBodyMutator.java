package jp.mzw.ajaxmutator.mutator;

import java.util.ArrayList;
import java.util.List;

import jp.mzw.ajaxmutator.mutatable.Request;
import jp.mzw.ajaxmutator.generator.Mutation;

import org.mozilla.javascript.ast.AstNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FakeBlankResponseBodyMutator extends AbstractMutator<Request> {
    private static final Logger LOGGER = LoggerFactory.getLogger(FakeBlankResponseBodyMutator.class);

    public FakeBlankResponseBodyMutator() {
        super(Request.class);
    }
    
    @Override
    public List<Mutation> generateMutationList(Request originalNode) {
    	AstNode successHandler = originalNode.getSuccessHanlder();
        if (successHandler == null) {
            return null;
        }

        if (originalNode.getType() == Request.Type.JQUERY) {
            // success(data, textStatus, jqXHR)
            StringBuilder replacementBuilder = new StringBuilder();
            replacementBuilder
                    .append("function(data, textStatus, jqXHR) {(")
                    .append(successHandler.toSource())
                    .append(")")
                    .append(".apply(this, [/* blank response mutation */'', textStatus, jqXHR]);}");
            List<Mutation> mutationList = new ArrayList<Mutation>();
            mutationList.add(new Mutation(successHandler, replacementBuilder.toString()));
            return mutationList;
        } else {
            LOGGER.info("Unknown request type for " + originalNode.getAstNode());
            return null;
        }
    }
    
}
