package jp.mzw.revajaxmutator.config.mutation;

import java.util.Set;

import jp.mzw.ajaxmutator.mutator.Mutator;
import jp.mzw.ajaxmutator.test.conductor.MutationTestConductor;

public interface MutateConfiguration {
    MutationTestConductor mutationTestConductor();
    Set<Mutator<?>> mutators();
}
