package jp.mzw.revajaxmutator.fixer;

import java.util.Collection;

import jp.gr.java_conf.daisy.ajax_mutator.mutatable.EventAttachment;
import org.mozilla.javascript.ast.AstNode;

public class EventTargetTSFixer extends AbstractReplacingAmongFixer<EventAttachment> {
    public EventTargetTSFixer(Collection<EventAttachment> mutationTargets, String[] parseResult) {
        super(EventAttachment.class, mutationTargets, parseResult);
    }

    @Override
    protected AstNode getFocusedNode(EventAttachment node) {
        return node.getTarget();
    }
}
