package jp.mzw.ajaxmutator.mutator;

import java.util.List;

import jp.mzw.ajaxmutator.mutatable.Mutatable;
import jp.mzw.ajaxmutator.generator.Mutation;

import org.mozilla.javascript.ast.AstNode;

/**
 * Basic implementation for {@link Mutator}
 *
 * @author Kazuki Nishiura
 */
public abstract class AbstractMutator<T extends Mutatable>
        implements Mutator<T> {
    Class<? extends T> applicableClass;

    public AbstractMutator(Class<? extends T> applicableClass) {
        this.applicableClass = applicableClass;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isApplicable(Class<?> c) {
        return applicableClass.isAssignableFrom(c);
    }

    /**
     * @return Simple (Human understandable) name of the mutation class produced
     * by this mutator.
     */
    public String mutationName() {
        return this.getClass().getSimpleName().replace("Mutator", "Mutation");
    }

    /**
     * Generate mutation for given original Node. Mutation consist of two parts:
     * mutated node, and mutating content.
     * Mutated node is an originalNode itself or it's subnode and which is
     * mutated. Mutating content is a something that replaces mutated node.
     *
     * @param originalNode node which is a target for mutation.
     * @return Mutation for given node or part of given node.
     */
    public abstract List<Mutation> generateMutationList(T originalNode);

    /**
     * Determine the equality of given to AstNodes in the context of mutator.
     * Subclass may want to override this method to create only meaningful
     * mutants.
     */
    protected boolean isEqual(AstNode node1, AstNode node2) {
        if (node1 == null && node2 == null) {
            return true;
        } else if (node1 == null || node2 == null) {
            return false;
        }
        return node1.toSource().equals(node2.toSource());
    }
}
