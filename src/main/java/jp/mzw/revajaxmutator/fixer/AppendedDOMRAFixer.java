package jp.mzw.revajaxmutator.fixer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jp.mzw.ajaxmutator.mutatable.DOMAppending;

import org.mozilla.javascript.ast.AstNode;

/**
 * @Junto Nakaoka
 */
public class AppendedDOMRAFixer
        extends AbstractReplacingAmongFixer<DOMAppending> {
    public AppendedDOMRAFixer(Collection<DOMAppending> mutationTargets, List<RepairSource> repairSources) {
        super(DOMAppending.class, mutationTargets, repairSources);
    }
    
    public AppendedDOMRAFixer(Collection<DOMAppending> mutationTargets) {
        super(DOMAppending.class, mutationTargets, new ArrayList<RepairSource>());
    }

    @Override
    protected AstNode getFocusedNode(DOMAppending node) {
        return node.getAppendedDom();
    }
}
