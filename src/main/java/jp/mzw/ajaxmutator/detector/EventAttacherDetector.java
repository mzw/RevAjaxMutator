package jp.mzw.ajaxmutator.detector;

import jp.mzw.ajaxmutator.mutatable.EventAttachment;

import org.mozilla.javascript.ast.AstNode;

/**
 * Implementation of detector which detect function call for event handler
 *
 * @author Kazuki Nishiura
 */
public abstract class EventAttacherDetector
        extends AbstractDetector<EventAttachment> {
    /**
     * detect event attachment from passed function call
     */
    @Override
    public EventAttachment detect(AstNode node) {
        return detectFromFunctionCall(node, true);
    }
}
