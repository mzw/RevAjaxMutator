package jp.mzw.ajaxmutator.mutator.replace.among;

import jp.mzw.ajaxmutator.mutatable.DOMAppending;
import org.mozilla.javascript.ast.AstNode;

import java.util.Collection;

/**
 * @author Kazuki Nishiura
 */
public class AppendedDOMRAMutator
        extends AbstractReplacingAmongMutator<DOMAppending> {
    public AppendedDOMRAMutator(Collection<DOMAppending> mutationTargets) {
        super(DOMAppending.class, mutationTargets);
    }

    @Override
    protected AstNode getFocusedNode(DOMAppending node) {
        return node.getAppendedDom();
    }
}
