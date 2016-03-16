package jp.mzw.ajaxmutator.mutator;

import java.util.ArrayList;
import java.util.List;

import jp.mzw.ajaxmutator.mutatable.Request;
import jp.mzw.ajaxmutator.generator.Mutation;

import org.mozilla.javascript.ast.AstNode;

/**
 * Replacing onsuccess and onfailure handler of jQuery's ajax method.
 */
public class ReplacingAjaxCallbackMutator extends AbstractMutator<Request> {
    public ReplacingAjaxCallbackMutator() {
        super(Request.class);
    }

    @Override
    public List<Mutation> generateMutationList(Request originalNode) {
    	AstNode successHandler = originalNode.getSuccessHanlder();
        AstNode failureHandler = originalNode.getFailureHandler();
        if (successHandler == null || failureHandler == null) {
            return null;
        }
        String replacement = originalNode.getAstNode().toSource();
        String PLACE_HOLDER = "__OLD__SUCCESS__HANDLER__";
        String successHandlerSrc = successHandler.toSource();
        String failureHandlerSrc = failureHandler.toSource();
        replacement = replacement.replace(successHandlerSrc, PLACE_HOLDER);
        replacement = replacement.replace(failureHandlerSrc, successHandlerSrc);
        replacement = replacement.replace(PLACE_HOLDER, failureHandlerSrc);
        List<Mutation> mutationList = new ArrayList<Mutation>();
        mutationList.add(new Mutation(originalNode.getAstNode(), replacement));
        return mutationList;
    }
    
}
