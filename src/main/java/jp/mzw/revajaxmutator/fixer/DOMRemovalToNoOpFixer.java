package jp.mzw.revajaxmutator.fixer;

import jp.mzw.ajaxmutator.mutatable.DOMRemoval;

/**
 * @author Junto Nakaoka
 */
public class DOMRemovalToNoOpFixer extends ReplacingToNoOpFixer<DOMRemoval> {
	public DOMRemovalToNoOpFixer() {
		super(DOMRemoval.class);
	}
}
