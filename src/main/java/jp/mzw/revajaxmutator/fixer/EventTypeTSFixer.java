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
public class EventTypeTSFixer extends AbstractReplacingAmongFixer<EventAttachment> {

    public EventTypeTSFixer(
			Collection<EventAttachment> mutationTargets,
			List<RepairSource> repairSources) {
		super(EventAttachment.class, mutationTargets, repairSources);
	}
    
    public EventTypeTSFixer(
			Collection<EventAttachment> mutationTargets) {
		super(EventAttachment.class, mutationTargets, new ArrayList<RepairSource>());
	}

	@Override
    protected AstNode getFocusedNode(EventAttachment node) {
        return node.getEvent();
    }
}
