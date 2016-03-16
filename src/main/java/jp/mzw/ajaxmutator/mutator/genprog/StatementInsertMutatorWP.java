package jp.mzw.ajaxmutator.mutator.genprog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.mozilla.javascript.ast.AstNode;

import jp.mzw.ajaxmutator.generator.Mutation;
import jp.mzw.ajaxmutator.mutator.replace.among.AbstractReplacingAmongMutator;
import jp.mzw.ajaxmutator.mutatable.genprog.Statement;

public class StatementInsertMutatorWP extends AbstractReplacingAmongMutator<Statement> {
	
	public StatementInsertMutatorWP(Collection<Statement> targets) {
		super(Statement.class, targets);
	}

	@Override
	protected AstNode getFocusedNode(Statement node) {
		return node.getAstNode();
	}
	
	@Override
	public List<Mutation> generateMutationList(Statement stmt) {
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
	
	/**
	 * Original implementation is @jp.mzw.ajaxmutator.mutator.replacing_among.AbstractReplacingAmongMutator
	 */
    protected String formatAccordingTo(AstNode mutatingNode, AstNode mutatedNode) {
    	StringBuilder builder = new StringBuilder("/* [Insert by AjaxGenProg] */");
    	// another statement is inserted "after" it
    	builder.append(mutatedNode.toSource()).append(mutatingNode.toSource());
        return builder.toString();
    }
}