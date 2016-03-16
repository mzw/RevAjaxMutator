package jp.mzw.ajaxmutator.mutator.replace.among;

import jp.mzw.ajaxmutator.mutatable.TimerEventAttachment;
import org.mozilla.javascript.ast.AstNode;

import java.util.Collection;

/**
 * @author Kazuki Nishiura
 */
public class TimerEventCallbackRAMutator
        extends AbstractReplacingAmongMutator<TimerEventAttachment> {
    public TimerEventCallbackRAMutator(
            Collection<TimerEventAttachment> mutationTargets) {
        super(TimerEventAttachment.class, mutationTargets);
    }

    @Override
    protected AstNode getFocusedNode(TimerEventAttachment node) {
        return node.getCallback();
    }
}
