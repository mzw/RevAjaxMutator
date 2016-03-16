package jp.mzw.ajaxmutator.mutator.genprog;

import java.util.ArrayList;
import java.util.List;

import jp.mzw.ajaxmutator.generator.Mutation;
import jp.mzw.ajaxmutator.mutator.replace.nop.ReplacingToNoOpMutator;
import jp.mzw.ajaxmutator.mutatable.genprog.Statement;

public class StatementDeleteMutatorToNoOp extends ReplacingToNoOpMutator<Statement> {

	public StatementDeleteMutatorToNoOp() {
		super(Statement.class);
	}
	
    @Override
    public List<Mutation> generateMutationList(Statement stmt) {
    	if(stmt.getWeight() == 0) {
    		return null;
    	}
    	
    	List<Mutation> mutationList = new ArrayList<Mutation>();
    	mutationList.add(new Mutation(stmt.getAstNode(), NO_OPERATION_STR));
    	return mutationList;
    }
    
}