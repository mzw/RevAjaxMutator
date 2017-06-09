package jp.mzw.revajaxmutator.parser.html;

import java.util.Set;

import com.google.common.collect.Sets;

public class EventSet {
	
	/** Contains target elements of event handlers */
	private Set<String> targetSet;
	
	/** Contains event types of event handlers */
	private Set<String> typeSet;
	
	/** Contains callback functions of event handlers */
	private Set<String> callbackSet;

	/**
	 * Constructor to instantiate empty sets
	 */
	public EventSet() {
		this.targetSet = Sets.newHashSet();
		this.typeSet = Sets.newHashSet();
		this.callbackSet = Sets.newHashSet();
	}

	/**
	 * Constructor with given sets
	 * 
	 * @param targetSet
	 * @param typeSet
	 * @param callbackSet
	 */
	public EventSet(final Set<String> targetSet, final Set<String> typeSet, final Set<String> callbackSet) {
		// TODO Remove null entries
		// TODO Remove duplications
		this.targetSet = targetSet;
		this.typeSet = typeSet;
		this.callbackSet = callbackSet;
	}

	/**
	 * Add target element of event handler
	 * if not be (null and duplicated)
	 * 
	 * @param target represents page element(s)
	 */
	public void addTarget(final String target) {
		if (target == null) {
			throw new NullPointerException("Target should not be null");
		}
		if (!this.targetSet.contains(target)) {
			this.targetSet.add(target);
		}
	}

	/**
	 * Add event type of event handler
	 * if not be (null and duplicated)
	 * 
	 * @param type represents event type
	 */
	public void addType(String type) {
		if (type == null) {
			throw new NullPointerException("Type should not be null");
		}
		if (!this.typeSet.contains(type)) {
			this.typeSet.add(type);
		}
	}

	/**
	 * Add callback function of event handler
	 * if not be (null and duplicated)
	 * 
	 * @param callback represents callback function
	 */
	public void addCallback(String callback) {
		if (callback == null) {
			throw new NullPointerException("Callback should not be null");
		}
		if (!this.callbackSet.contains(callback)) {
			this.callbackSet.add(callback);
		}
	}

	/**
	 * Get set of target elements of event handlers
	 * 
	 * @return set of target elements of event handlers
	 */
	public Set<String> getTargetSet() {
		return this.targetSet;
	}

	/**
	 * Get set of event types of event handlers
	 * 
	 * @return set of event types of event handlers
	 */
	public Set<String> getTypeSet() {
		return this.typeSet;
	}

	/**
	 * Get set of callback function of event handlers
	 * 
	 * @return set of callback functions of event handlers
	 */
	public Set<String> getCallbackSet() {
		return this.callbackSet;
	}

}
