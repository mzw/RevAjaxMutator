package jp.mzw.revajaxmutator.fixer;

import jp.mzw.ajaxmutator.mutatable.DOMCreation;


/**
 * @author Junto Nakaoka
 * Replace DOMCreation to No-op.
 */
public class DOMCreationToNoOpFixer extends ReplacingToNoOpFixer<DOMCreation> {
	public DOMCreationToNoOpFixer() {
		super(DOMCreation.class);
	}
}

