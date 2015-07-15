package jp.mzw.revajaxmutator.fixer;

import java.util.ArrayList;
import java.util.List;

import jp.gr.java_conf.daisy.ajax_mutator.mutatable.EventAttachment;
import jp.gr.java_conf.daisy.ajax_mutator.mutation_generator.Mutation;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.AbstractMutator;

public class EventTargetGivenFixer extends AbstractMutator<EventAttachment> {
    private final String target;

    public EventTargetGivenFixer(String target) {
        super(EventAttachment.class);
        this.target = target;
    }

    @Override
    public List<Mutation> generateMutationList(EventAttachment originalNode) {
    	List<Mutation> mutationList = new ArrayList<Mutation>();
        mutationList.add(new Mutation(originalNode.getTarget(), target));
        return mutationList;
    }
}
