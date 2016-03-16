package jp.mzw.ajaxmutator.mutator.genprog;

import java.util.ArrayList;
import java.util.List;

import org.mozilla.javascript.ast.AstNode;

import jp.mzw.ajaxmutator.generator.Mutation;
import jp.mzw.ajaxmutator.mutator.AbstractMutator;
import jp.mzw.ajaxmutator.mutator.replace.among.AbstractReplacingAmongMutator;
import jp.mzw.ajaxmutator.mutatable.genprog.Statement;

public abstract class AbstractStatementsMutator<T extends Statement> extends AbstractMutator<T> {

	protected Statement src;
	protected Statement dst;
	
	public AbstractStatementsMutator(Class<? extends T> applicableClass, Statement src, Statement dst) {
		super(applicableClass);
		this.src = src;
		this.dst = dst;
	}

    @Override
    public List<Mutation> generateMutationList(Statement stmt) {
		// According to AjaxMutator specification
		if(!stmt.equals(src)) {
			return null;
		}

		List<Mutation> mutationList = new ArrayList<Mutation>();

		AstNode srcNode = src != null? src.getAstNode(): null;
		AstNode dstNode = dst != null? dst.getAstNode(): null;

		if (isEqual(srcNode, dstNode) || AbstractReplacingAmongMutator.include(srcNode, dstNode) || AbstractReplacingAmongMutator.include(dstNode, srcNode)) {
			// NOP
		} else {
			mutationList.add(new Mutation(srcNode, formatAccordingTo(srcNode, dstNode)));
		}

		if (!mutationList.isEmpty()) {
			return mutationList;
		}
		return null;
    }

	protected abstract String formatAccordingTo(AstNode srcNode, AstNode dstNode);

}
