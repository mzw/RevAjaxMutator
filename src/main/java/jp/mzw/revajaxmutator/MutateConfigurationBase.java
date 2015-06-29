package jp.mzw.revajaxmutator;

import java.util.Set;

import jp.gr.java_conf.daisy.ajax_mutator.MutateVisitor;
import jp.gr.java_conf.daisy.ajax_mutator.MutationTestConductor;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.Mutator;

@SuppressWarnings("rawtypes")
public abstract class MutateConfigurationBase implements MutateConfiguration {
    protected MutationTestConductor conductor;
    protected MutateVisitor visitor;
    protected Set<Mutator> mutators;

    @Override
    public MutationTestConductor mutationTestConductor() {
        return conductor;
    }

    @Override
    public Set<Mutator> mutators() {
        return mutators;
    }
}
