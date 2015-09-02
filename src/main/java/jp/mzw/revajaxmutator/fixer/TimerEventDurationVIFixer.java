package jp.mzw.revajaxmutator.fixer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.gr.java_conf.daisy.ajax_mutator.mutatable.TimerEventAttachment;
import jp.gr.java_conf.daisy.ajax_mutator.mutation_generator.Mutation;
import jp.gr.java_conf.daisy.ajax_mutator.mutator.AbstractMutator;

import org.mozilla.javascript.ast.AstNode;

public class TimerEventDurationVIFixer extends AbstractMutator<TimerEventAttachment>{
	private List<String> candidateOfTimerDuration;
	
	
	public TimerEventDurationVIFixer() {
        super(TimerEventAttachment.class);
        candidateOfTimerDuration = new ArrayList<String>(Arrays.asList(new String[]{"1","10","100","500","1000","5000"}));
    }

	@Override
	public List<Mutation> generateMutationList(TimerEventAttachment originalNode) {
        AstNode focusedNode = getFocusedNode(originalNode);
        List<Mutation> mutationList = new ArrayList<Mutation>();
        for(String timerDuration: candidateOfTimerDuration){
        	mutationList.add(new Mutation(
                    focusedNode, timerDuration));
        }
        return mutationList;
	}
	

	private AstNode getFocusedNode(TimerEventAttachment node){
		return node.getDuration();
	}

}
