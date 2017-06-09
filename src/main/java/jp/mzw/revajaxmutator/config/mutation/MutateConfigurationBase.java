package jp.mzw.revajaxmutator.config.mutation;

import java.util.Set;

import jp.mzw.ajaxmutator.MutateVisitor;
import jp.mzw.ajaxmutator.mutator.Mutator;
import jp.mzw.ajaxmutator.test.conductor.MutationTestConductor;

public abstract class MutateConfigurationBase implements MutateConfiguration {
    protected MutationTestConductor conductor;
    protected MutateVisitor visitor;
    protected Set<Mutator<?>> mutators;

    @Override
    public MutationTestConductor mutationTestConductor() {
        return conductor;
    }

    @Override
    public Set<Mutator<?>> mutators() {
        return mutators;
    }
}
