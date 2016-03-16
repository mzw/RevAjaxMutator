package jp.mzw.ajaxmutator.mutator.replace.nop;

import jp.mzw.ajaxmutator.mutatable.DOMRemoval;

/**
 * @author Kazuki Nishiura
 */
public class DOMRemovalToNoOpMutator extends ReplacingToNoOpMutator<DOMRemoval> {
    public DOMRemovalToNoOpMutator() {
        super(DOMRemoval.class);
    }
}
