package jp.mzw.revajaxmutator.fixer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.mzw.ajaxmutator.mutatable.TimerEventAttachment;
import jp.mzw.ajaxmutator.generator.Mutation;
import jp.mzw.ajaxmutator.mutator.AbstractMutator;

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
