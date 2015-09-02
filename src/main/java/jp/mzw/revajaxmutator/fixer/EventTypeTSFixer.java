package jp.mzw.revajaxmutator.fixer;

import java.util.Collection;

import jp.gr.java_conf.daisy.ajax_mutator.mutatable.EventAttachment;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.replacing_among.AbstractReplacingAmongMutator;

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
