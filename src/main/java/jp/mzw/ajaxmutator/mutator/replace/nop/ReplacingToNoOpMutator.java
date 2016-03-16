package jp.mzw.ajaxmutator.mutator.replace.nop;

import com.google.common.collect.ImmutableSet;

import jp.mzw.ajaxmutator.mutatable.Mutatable;
import jp.mzw.ajaxmutator.generator.Mutation;
import jp.mzw.ajaxmutator.mutator.AbstractMutator;
import jp.mzw.ajaxmutator.util.AstUtil;

import org.mozilla.javascript.ast.ExpressionStatement;
import org.mozilla.javascript.ast.VariableDeclaration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Replacing statement including specified node to no-op. Removing a node would cause easy-to-kill
 * mutation (for instance, removing "getElementById('hoge').removeChild('fuga')" from
 * "document.getElementById('hoge').removeChild('fuga');" create "document.;" which is syntactically
 * invalid. To mitigate the issue, this class try to remove whole statement holding specified node.
 */
public abstract class ReplacingToNoOpMutator<T extends Mutatable> extends AbstractMutator<T> {
    private static final Set<Class<?>> CLASS_OF_STATEMENT
            = ImmutableSet.<Class<?>>of(VariableDeclaration.class, ExpressionStatement.class);
    public static final String NO_OPERATION_STR = "/* No-op */";

    public ReplacingToNoOpMutator(Class<? extends T> applicableClass) {
        super(applicableClass);
    }

    @Override
    public List<Mutation> generateMutationList(T originalNode) {
    	List<Mutation> mutationList = new ArrayList<Mutation>();
    	mutationList.add(new Mutation(
                AstUtil.parentOfAnyTypes(originalNode.getAstNode(), CLASS_OF_STATEMENT, true),
                NO_OPERATION_STR));
    	return mutationList;
    }
    
}
