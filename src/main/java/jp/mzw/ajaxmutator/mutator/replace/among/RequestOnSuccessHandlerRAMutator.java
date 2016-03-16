package jp.mzw.ajaxmutator.mutator.replace.among;

import jp.mzw.ajaxmutator.mutatable.Request;
import jp.mzw.ajaxmutator.util.StringToAst;
import org.mozilla.javascript.ast.AstNode;

import java.util.Collection;

/**
 * @author Kazuki Nishiura
 */
public class RequestOnSuccessHandlerRAMutator
        extends AbstractReplacingAmongMutator<Request> {

    public RequestOnSuccessHandlerRAMutator(
            Collection<Request> mutationTargets) {
        super(Request.class, mutationTargets);
    }

    @Override
    protected AstNode getFocusedNode(Request node) {
        return node.getSuccessHanlder();
    }

    @Override
    public AstNode getDefaultReplacingNode() {
        return StringToAst.parseAsFunctionNode("function() {}");
    }
}
