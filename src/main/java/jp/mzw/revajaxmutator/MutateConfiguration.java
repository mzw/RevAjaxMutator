package jp.mzw.revajaxmutator;

import java.util.Set;

import jp.mzw.ajaxmutator.MutationTestConductor;
import jp.mzw.ajaxmutator.mutator.Mutator;

public interface MutateConfiguration {
    MutationTestConductor mutationTestConductor();
    Set<Mutator<?>> mutators();
}
