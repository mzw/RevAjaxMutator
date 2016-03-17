package jp.mzw.revajaxmutator.fixer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jp.mzw.ajaxmutator.mutatable.EventAttachment;

import org.mozilla.javascript.ast.AstNode;

/**
 * 
 * @author Junto Nakaoka
 *
 */
public class EventTargetTSFixer extends AbstractReplacingAmongFixer<EventAttachment> {

    public EventTargetTSFixer(
			Collection<EventAttachment> mutationTargets,
			List<RepairSource> repairSources) {
		super(EventAttachment.class, mutationTargets, repairSources);
	}
    
    public EventTargetTSFixer(
			Collection<EventAttachment> mutationTargets) {
		super(EventAttachment.class, mutationTargets, new ArrayList<RepairSource>());
	}
    

	@Override
    protected AstNode getFocusedNode(EventAttachment node) {
        return node.getTarget();
    }
}
