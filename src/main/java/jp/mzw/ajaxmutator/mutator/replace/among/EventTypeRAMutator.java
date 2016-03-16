package jp.mzw.ajaxmutator.mutator.replace.among;

import jp.mzw.ajaxmutator.mutatable.EventAttachment;
import org.mozilla.javascript.ast.AstNode;

import java.util.Collection;

/**
 * @author Kazuki Nishiura
 */
public class EventTypeRAMutator
        extends AbstractReplacingAmongMutator<EventAttachment> {
    public EventTypeRAMutator(Collection<EventAttachment> mutationTargets) {
        super(EventAttachment.class, mutationTargets);
    }

    @Override
    protected AstNode getFocusedNode(EventAttachment node) {
        return node.getEvent();
    }
}
