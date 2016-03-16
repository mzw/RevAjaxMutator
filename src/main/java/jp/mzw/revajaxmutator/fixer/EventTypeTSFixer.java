package jp.mzw.revajaxmutator.fixer;

import java.util.Collection;

import jp.mzw.ajaxmutator.mutatable.EventAttachment;

import org.mozilla.javascript.ast.AstNode;

public class EventTypeTSFixer extends AbstractReplacingAmongFixer<EventAttachment> {
    public EventTypeTSFixer(Collection<EventAttachment> mutationTargets, String[] parseResult) {
        super(EventAttachment.class, mutationTargets, parseResult);
    }

    @Override
    protected AstNode getFocusedNode(EventAttachment node) {
        return node.getEvent();
    }
}
