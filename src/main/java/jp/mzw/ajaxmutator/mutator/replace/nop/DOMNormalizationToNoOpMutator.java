package jp.mzw.ajaxmutator.mutator.replace.nop;

import jp.mzw.ajaxmutator.mutatable.DOMNormalization;

/**
 * Replace DOM Normalization to No-op
 */
public class DOMNormalizationToNoOpMutator  extends ReplacingToNoOpMutator<DOMNormalization> {
    public DOMNormalizationToNoOpMutator() {
        super(DOMNormalization.class);
    }
}
