package jp.mzw.revajaxmutator.parser;

import java.util.Set;

public class EventSet {
	private Set<String> targetSet;
	private Set<String> typeSet;
	private Set<String> callbackSet;

	public EventSet(Set<String> targetSet, Set<String> typeSet,
			Set<String> callbackSet) {
		this.targetSet = targetSet;
		this.typeSet = typeSet;
		this.callbackSet = callbackSet;
	}

	public Set<String> getTargetSet() {
		return targetSet;
	}

	public Set<String> getTypeSet() {
		return typeSet;
	}

	public Set<String> getCallbackSet() {
		return callbackSet;
	}

}
