package jp.mzw.revajaxmutator.fixer;

import jp.mzw.ajaxmutator.detector.jquery.JQueryRequestDetector;
import jp.mzw.ajaxmutator.mutatable.Request;
import jp.mzw.ajaxmutator.util.StringToAst;
import jp.mzw.revajaxmutator.parser.RepairSource;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.StringLiteral;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Junto Nakaoka
 */
public class RequestMethodRAFixer
        extends AbstractReplacingAmongFixer<Request> {
    public RequestMethodRAFixer(Collection<Request> mutationTargets) {
        super(Request.class, mutationTargets, new ArrayList<RepairSource>());
    }

    @Override
    protected AstNode getFocusedNode(Request node) {
        return node.getRequestMethodNode();
    }

    @Override
    protected String formatAccordingTo(AstNode mutatingNode, AstNode mutatedNode) {
        if (mutatedNode instanceof StringLiteral
                && !(mutatingNode instanceof StringLiteral)) {
            // mutated: $.ajax(.., 'POST', ...)
            // mutating: $.get(...)
            return '"' + mutatingNode.toSource().toUpperCase() + '"';
        } else if (mutatingNode instanceof StringLiteral
            && JQueryRequestDetector.AJAX_SHORTCUT_METHODS.contains(
                mutatedNode.toSource().trim())) {
            // mutated: $.get(...)
            // mutating: $.ajax(.., 'POST', ...)
            return ((StringLiteral) mutatingNode).getValue();
        }
        return super.formatAccordingTo(mutatingNode, mutatedNode);
    }

    @Override
    public AstNode getDefaultReplacingNode() {
        return StringToAst.parseAsStringLiteral("'get'");
    }
}
