package jp.mzw.revajaxmutator;

import java.util.Set;

import jp.mzw.ajaxmutator.MutateVisitor;
import jp.mzw.ajaxmutator.MutationTestConductor;
import jp.mzw.ajaxmutator.mutator.Mutator;

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
