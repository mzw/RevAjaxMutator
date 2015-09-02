package jp.mzw.revajaxmutator.fixer;

import java.util.Collection;

import jp.gr.java_conf.daisy.ajax_mutator.mutatable.EventAttachment;
import jp.gr.java_conf.daisy.ajax_mutator.util.StringToAst;

import org.mozilla.javascript.ast.AstNode;

public class EventCallbackERFixer extends AbstractReplacingAmongFixer<EventAttachment> {
    public EventCallbackERFixer(Collection<EventAttachment> mutationTargets, String[] parseResult) {
        super(EventAttachment.class, mutationTargets, parseResult);
    }

    @Override
    protected AstNode getFocusedNode(EventAttachment node) {
        return node.getCallback();
    }
}