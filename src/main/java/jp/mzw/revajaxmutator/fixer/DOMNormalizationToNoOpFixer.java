package jp.mzw.revajaxmutator.fixer;

import jp.mzw.ajaxmutator.mutatable.DOMNormalization;

/**
 * @author Junto Nakaoka
 * Replace DOM Normalization to No-op
 */
public class DOMNormalizationToNoOpFixer  extends ReplacingToNoOpFixer<DOMNormalization> {
	public DOMNormalizationToNoOpFixer() {
        super(DOMNormalization.class);
    }
}
