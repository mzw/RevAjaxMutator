package jp.mzw.ajaxmutator.mutator.replace.among;

import jp.mzw.ajaxmutator.mutatable.EventAttachment;
import jp.mzw.ajaxmutator.util.StringToAst;
import org.mozilla.javascript.ast.AstNode;

import java.util.Collection;

/**
 * @author Kazuki Nishiura
 */
public class EventCallbackRAMutator
        extends AbstractReplacingAmongMutator<EventAttachment> {
    public EventCallbackRAMutator(Collection<EventAttachment> mutationTargets) {
        super(EventAttachment.class, mutationTargets);
    }

    @Override
    protected AstNode getFocusedNode(EventAttachment node) {
        return node.getCallback();
    }

    @Override
    public AstNode getDefaultReplacingNode() {
        return StringToAst.parseAsFunctionNode("function(){/* no-op function */}");
    }
}
