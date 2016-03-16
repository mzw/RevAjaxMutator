package jp.mzw.ajaxmutator.mutator.replace.among;

import jp.mzw.ajaxmutator.mutatable.AttributeModification;
import org.mozilla.javascript.ast.AstNode;

import java.util.Collection;

/**
 * @author Kazuki Nishiura
 */
public class AttributeModificationValueRAMutator
        extends AbstractReplacingAmongMutator<AttributeModification> {
    public AttributeModificationValueRAMutator(
            Collection<AttributeModification> mutationTargets) {
        super(AttributeModification.class, mutationTargets);
    }

    @Override
    protected AstNode getFocusedNode(AttributeModification node) {
        return node.getAttributeValue();
    }
}
