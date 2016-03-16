package jp.mzw.ajaxmutator.mutator.replace.nop;

import jp.mzw.ajaxmutator.mutatable.DOMCreation;

/**
 * Replace DOMCreation to No-op.
 */
public class DOMCreationToNoOpMutator extends ReplacingToNoOpMutator<DOMCreation> {
    public DOMCreationToNoOpMutator() {
        super(DOMCreation.class);
    }
}
