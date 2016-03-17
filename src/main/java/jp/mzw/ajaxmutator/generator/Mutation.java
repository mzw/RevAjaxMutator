package jp.mzw.ajaxmutator.generator;

import jp.mzw.ajaxmutator.util.Util;
import jp.mzw.revajaxmutator.fixer.Candidate;

import org.mozilla.javascript.ast.AstNode;

/**
 * Data class that poses mutation information, namely, original node that is
 * mutated and content that replaces the original node.
 *
 * @author Kazuki Nishiura
 */
public class Mutation {
    private final AstNode originalNode;
    private final String mutatingContent;
    @SuppressWarnings("unused")
	private final Candidate candidate;

    public Mutation(AstNode originalNode, String mutatingContent, Candidate candidate) {
        this.originalNode = originalNode;
        this.mutatingContent = mutatingContent;
        this.candidate = candidate;
    }

    public Mutation(AstNode originalNode, String mutatingContent) {
        this.originalNode = originalNode;
        this.mutatingContent = mutatingContent;
        this.candidate = new Candidate(Candidate.Type.NONE);
    }

    public AstNode getOriginalNode() {
        return originalNode;
    }

    public String getMutatingContent() {
        return mutatingContent;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Mutation").append(System.lineSeparator())
                .append("from: ").append(Util.oneLineStringOf(originalNode))
                .append(System.lineSeparator())
                .append("to: ").append(Util.omitLineBreak(mutatingContent))
                .append(System.lineSeparator());
        return builder.toString();
    }
}
