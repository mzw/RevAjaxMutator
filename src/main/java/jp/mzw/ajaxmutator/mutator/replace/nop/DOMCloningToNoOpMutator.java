package jp.mzw.ajaxmutator.mutator.replace.nop;

import jp.mzw.ajaxmutator.mutatable.DOMCloning;

/**
 * Replacing DOM cloning to No-op.
 */
public class DOMCloningToNoOpMutator extends ReplacingToNoOpMutator<DOMCloning> {
    public DOMCloningToNoOpMutator() {
        super(DOMCloning.class);
    }
}
