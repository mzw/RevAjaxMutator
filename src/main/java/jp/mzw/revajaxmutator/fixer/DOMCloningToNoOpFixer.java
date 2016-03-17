package jp.mzw.revajaxmutator.fixer;

import jp.mzw.ajaxmutator.mutatable.DOMCloning;

/**
 * Replacing DOM cloning to No-op.
 */
public class DOMCloningToNoOpFixer extends ReplacingToNoOpFixer<DOMCloning> {
	public DOMCloningToNoOpFixer() {
		super(DOMCloning.class);
	}
}
