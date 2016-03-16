package jp.mzw.ajaxmutator.mutator.genprog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jp.mzw.ajaxmutator.generator.Mutation;
import jp.mzw.ajaxmutator.mutator.replace.among.AbstractReplacingAmongMutator;
import jp.mzw.ajaxmutator.mutatable.genprog.Statement;

import org.mozilla.javascript.ast.AstNode;

public class StatementSwapMutatorRA extends AbstractReplacingAmongMutator<Statement> {
	
	public StatementSwapMutatorRA(Collection<Statement> targets) {
		super(Statement.class, targets);
	}

	@Override
	protected AstNode getFocusedNode(Statement node) {
		return node.getAstNode();
	}

	@Override
	public List<Mutation> generateMutationList(Statement stmt) {
    	if(stmt.getWeight() == 0) {
    		return null;
    	}
    	
        AstNode focusedNode = getFocusedNode(stmt);
        List<Mutation> mutationList = new ArrayList<Mutation>();
        for(AstNode candidate : candidates) {
            if(isEqual(focusedNode, candidate) || include(focusedNode, candidate) || include(candidate, focusedNode)) {
                // NOP
            } else {
                mutationList.add(new Mutation(focusedNode, formatAccordingTo(candidate, focusedNode)));
            }
        }
        if(!mutationList.isEmpty()) {
            return mutationList;
        }
        return null;
	}
}
