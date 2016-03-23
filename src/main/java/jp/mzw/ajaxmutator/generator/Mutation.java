package jp.mzw.ajaxmutator.generator;

import jp.mzw.ajaxmutator.util.Util;
import jp.mzw.revajaxmutator.parser.RepairValue;

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
	private final RepairValue repairValue;
    
    /**
     * Constructor for mutation testing
     * @param originalNode
     * @param mutatingContent
     */
    public Mutation(AstNode originalNode, String mutatingContent) {
        this.originalNode = originalNode;
        this.mutatingContent = mutatingContent;
        this.repairValue = new RepairValue();
    }

    /**
     * Constructor for automated program repair
     * @param originalNode
     * @param mutatingContent
     * @param repairValue
     */
    public Mutation(AstNode originalNode, String mutatingContent, RepairValue repairValue) {
        this.originalNode = originalNode;
        this.mutatingContent = mutatingContent;
        this.repairValue = repairValue;
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
