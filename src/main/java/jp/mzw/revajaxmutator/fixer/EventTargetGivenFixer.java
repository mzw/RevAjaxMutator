package jp.mzw.revajaxmutator.fixer;

import java.util.ArrayList;
import java.util.List;

import jp.mzw.ajaxmutator.mutatable.EventAttachment;
import jp.mzw.ajaxmutator.generator.Mutation;
import jp.mzw.ajaxmutator.mutator.AbstractMutator;

/**
 * 
 * @author Junto Nakaoka
 *
 */
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
