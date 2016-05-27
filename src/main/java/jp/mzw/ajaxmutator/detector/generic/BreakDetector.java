package jp.mzw.ajaxmutator.detector.generic;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.BreakStatement;

import jp.mzw.ajaxmutator.detector.AbstractDetector;
import jp.mzw.ajaxmutator.mutatable.generic.Break;

public class BreakDetector extends AbstractDetector<Break> {

    public Break detect(AstNode node) {
        return detectFromBranch(node, true);
    }

    @Override
    protected Break detectFromBranch(
            BreakStatement breakstatement) {
                return new Break(
                        breakstatement);
    }
}
