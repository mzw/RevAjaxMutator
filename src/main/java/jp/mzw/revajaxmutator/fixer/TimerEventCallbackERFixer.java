package jp.mzw.revajaxmutator.fixer;

import java.util.Collection;
import jp.mzw.ajaxmutator.mutatable.TimerEventAttachment;
import org.mozilla.javascript.ast.AstNode;

public class TimerEventCallbackERFixer extends AbstractReplacingAmongFixer<TimerEventAttachment> {
    public TimerEventCallbackERFixer(
            Collection<TimerEventAttachment> mutationTargets, String[] parseResult) {
        super(TimerEventAttachment.class, mutationTargets, parseResult);
    }

    @Override
    protected AstNode getFocusedNode(TimerEventAttachment node) {
        return node.getCallback();
    }
}