package jp.mzw.revajaxmutator;

import java.util.Set;

import jp.gr.java_conf.daisy.ajax_mutator.MutationTestConductor;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.Mutator;

@SuppressWarnings("rawtypes")
public interface MutateConfiguration {
    MutationTestConductor mutationTestConductor();
    Set<Mutator> mutators();
}
