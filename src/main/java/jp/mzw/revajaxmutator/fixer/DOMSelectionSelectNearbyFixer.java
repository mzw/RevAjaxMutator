package jp.mzw.revajaxmutator.fixer;

import java.util.List;

import jp.mzw.ajaxmutator.JSType;
import jp.mzw.ajaxmutator.mutatable.DOMSelection;
import jp.mzw.ajaxmutator.generator.Mutation;
import jp.mzw.ajaxmutator.mutator.AbstractMutator;
import jp.mzw.ajaxmutator.util.Randomizer;
import jp.mzw.ajaxmutator.util.StringToAst;
import jp.mzw.revajaxmutator.parser.RepairValue;

import org.mozilla.javascript.ast.AstNode;

import com.google.common.collect.Lists;

/**
 * 
 * @author Junto Nakaoka
 *
 */
public class DOMSelectionSelectNearbyFixer extends AbstractMutator<DOMSelection> {
	public DOMSelectionSelectNearbyFixer() {
		super(DOMSelection.class);
	}

	@Override
	public List<Mutation> generateMutationList(DOMSelection originalNode) {
		double randomValue = Randomizer.getDouble();
		AstNode node = originalNode.getAstNode();
		JSType domType = (originalNode.getSelectionMethod() == DOMSelection.SelectionMethod.JQUERY) ? JSType.JQUERY_OBJECT : JSType.DOM_ELEMENT;
		List<Mutation> mutationList = Lists.newArrayList();
		if (randomValue < 0.5) {
			mutationList.add(new Mutation(originalNode.getAstNode(), StringToAst.createParentNodeAsString(node, domType),
					new RepairValue(StringToAst.createParentNodeAsString(node, domType))));
		} else {
			mutationList.add(new Mutation(originalNode.getAstNode(), StringToAst.createChildNodeAsString(node, domType),
					new RepairValue(StringToAst.createChildNodeAsString(node, domType))));
		}
		return mutationList;
	}
}
