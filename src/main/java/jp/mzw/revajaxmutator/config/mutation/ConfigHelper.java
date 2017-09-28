package jp.mzw.revajaxmutator.config.mutation;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import com.google.common.collect.Sets;

import jp.mzw.revajaxmutator.config.mutation.defaults.DefaultParameters;
import jp.mzw.revajaxmutator.parser.RepairSource;
import jp.mzw.revajaxmutator.parser.html.EventSet;
import jp.mzw.revajaxmutator.parser.html.HTMLParser;
import jp.mzw.revajaxmutator.parser.java.TestCaseParser;
import jp.mzw.revajaxmutator.parser.javascript.JavaScriptParser;

/**
 * Provides repair values from given Default, JavaScript, HTML, and test case
 *
 * TODO: Brush up
 *
 * @author Yuta Maezawa
 *
 */
public class ConfigHelper {

	/** Provides repair values from given test-case file */
	private TestCaseParser testCaseParser;

	/** Provides repair values from given HTML file */
	private HTMLParser htmlParser;

	/** Provides repair values from given JavaScript file */
	private JavaScriptParser jsParser;

	private EventSet eventSet = null;

	/**
	 * Parse given file as HTML to obtain parameters
	 *
	 * @param file
	 *            HTML file
	 * @return this ConfigHelper
	 * @throws IOException
	 *             causes if given file is not found
	 */
	public ConfigHelper parseHtml(File file) throws IOException {
		this.htmlParser = new HTMLParser(file);
		return this;
	}

	/**
	 * Parse given file as test case to obtain parameters
	 *
	 * @param file
	 *            Java file
	 * @return this ConfigHelper
	 * @throws IOException
	 *             causes if given file is not found
	 */
	public ConfigHelper parseTestCase(File file) throws IOException {
		this.testCaseParser = new TestCaseParser(file);
		return this;
	}

	/**
	 * Parse given file as JavaScript to obtain parameters
	 *
	 * @param file
	 *            JavaScript file
	 * @return this ConfigHelper
	 * @throws IOException
	 *             causes if given file tis not found
	 */
	public ConfigHelper parseJavaScript(File file) throws IOException {
		this.jsParser = new JavaScriptParser(file);
		return this;
	}

	/**
	 * Get DOM selection attributes for repairing DOM manipulations with invalid
	 * attributes
	 *
	 * Default : attributes based on heuristics JavaScript: TODO HTML : DOM
	 * element identifiers implemented in given HTML file TestCase : Selenium
	 * locators as target attributes
	 *
	 * Note that parameters at JavaScript file are available as default
	 *
	 * @return List of DOM selection attributes
	 */
	public Collection<? extends RepairSource> getRepairSourcesForDomSelectionAttributeFixer() {
		final Set<RepairSource> repairSources = Sets.newHashSet();
		final Set<String> duplicate = Sets.newHashSet();

		// add default
		for (final String value : DefaultParameters.DOM_SELECTION_ATTRIBUTE_VALUES) {
			repairSources.add(new RepairSource(value, RepairSource.Type.Default));
		}

		// From Test Case
		for (final String attr : this.testCaseParser.getAttributeValues()) {
			if (!duplicate.contains(attr)) {
				duplicate.add(attr);
				repairSources.add(new RepairSource(attr, RepairSource.Type.TestCase));
			}
		}
		// From HTML
		for (final String attr : this.htmlParser.getAllElementIdentifier()) {
			if (!duplicate.contains(attr)) {
				duplicate.add(attr);
				repairSources.add(new RepairSource(attr, RepairSource.Type.HTML));
			}
		}

		return repairSources;
	}

	/**
	 * Get DOM elements for repairing invalid target elements at event handlers
	 *
	 * Default : DOM elements based on heuristics JavaScript: TODO HTML : DOM
	 * elements at which event handlers are implemented TestCase : Selenium
	 * locators as target elements
	 *
	 * @return
	 */
	public Collection<? extends RepairSource> getRepairSourcesForEventTarget() {
		if (this.eventSet == null) {
			this.eventSet = this.htmlParser.getAllEventSet();
		}
		final Set<RepairSource> repairSources = Sets.newHashSet();
		final Set<String> duplicate = Sets.newHashSet();

		// add default
		for (final String target : DefaultParameters.TARGET_ELEMENTS_HANDLING_EVENT) {
			repairSources.add(new RepairSource(target, RepairSource.Type.Default));
		}

		// From Test Case
		for (final String attr : this.testCaseParser.getAttributeValues()) {
			if (!duplicate.contains(attr)) {
				duplicate.add(attr);
				repairSources.add(new RepairSource("$(" + attr + ")", RepairSource.Type.TestCase));
			}
		}
		// From HTML
		for (final String eventTarget : this.eventSet.getTargetSet()) {
			repairSources.add(new RepairSource(eventTarget, RepairSource.Type.HTML));
		}
		for (final String identifier : this.htmlParser.getAllElementIdentifier()) {
			repairSources.add(new RepairSource("$(" + identifier + ")", RepairSource.Type.HTML));
		}
		return repairSources;
	}

	/**
	 * Get event type for repairing invalid event types at event handlers
	 *
	 * Default : TODO JavaScript: Event types of event handlers implemented in
	 * given JavaScript file and its similar event types HTML : Event types of
	 * event handlers implemented in given HTML file TestCase : TODO
	 *
	 * @return
	 */
	public Collection<? extends RepairSource> getRepairSourcesForEventType() {
		if (this.eventSet == null) {
			this.eventSet = this.htmlParser.getAllEventSet();
		}

		// From HTML
		final Set<RepairSource> repairSources = Sets.newHashSet();
		final Set<String> htmlSet = Sets.newHashSet();
		for (final String eventType : this.eventSet.getTypeSet()) {
			htmlSet.add(eventType);
		}
		for (final String eventType : htmlSet) {
			repairSources.add(new RepairSource("\'" + eventType + "\'", RepairSource.Type.HTML));
		}

		// From JavaScript
		final Set<String> jsSet = Sets.newHashSet();
		for (final String name : this.jsParser.getEventTypes()) {
			// add similar events to the list of repair sources
			if (DefaultParameters.EVENT_TYPES_KEYBOARD.contains(name)) {
				for (final String eventType : DefaultParameters.EVENT_TYPES_KEYBOARD) {
					jsSet.add(eventType);
				}
			} else if (DefaultParameters.EVENT_TYPES_MOUSE.contains(name)) {
				for (final String eventType : DefaultParameters.EVENT_TYPES_MOUSE) {
					jsSet.add(eventType);
				}
			} else if (DefaultParameters.EVENT_TYPES_EDIT.contains(name)) {
				for (final String eventType : DefaultParameters.EVENT_TYPES_EDIT) {
					jsSet.add(eventType);
				}
			}
		}
		// Add new .js event types
		jsSet.removeAll(htmlSet);
		for (final String eventType : jsSet) {
			repairSources.add(new RepairSource("\'" + eventType + "\'", RepairSource.Type.JavaScript));
		}

		return repairSources;
	}

	/**
	 * Get functions for repairing invalid callback function of event handlers
	 *
	 * Default: TODO JavaScript: Functions declared HTML: Callback functions at
	 * event handlers TestCase: TODO
	 *
	 * @return set of functions
	 */
	public Collection<? extends RepairSource> getRepairSourcesForEventCallback() {
		if (this.eventSet == null) {
			this.eventSet = this.htmlParser.getAllEventSet();
		}
		final Set<RepairSource> repairSources = Sets.newHashSet();
		final Set<String> duplicate = Sets.newHashSet();

		// From HTML
		for (final String eventCallback : this.eventSet.getCallbackSet()) {
			if (!duplicate.contains(eventCallback)) {
				duplicate.add(eventCallback);
				repairSources.add(new RepairSource(eventCallback, RepairSource.Type.HTML));
			}
		}
		// From JavaScript
		for (final String name : this.jsParser.getFunctionNames()) {
			if (!duplicate.contains(name)) {
				duplicate.add(name);
				repairSources.add(new RepairSource(name, RepairSource.Type.JavaScript));
			}
		}
		return repairSources;
	}

	/**
	 * Get attribute values for repairing DOM manipulation with invalid
	 * attribute values
	 *
	 * Default: TODO JavaScript: Values used in infix expressions implemented in
	 * given JavaScript file HTML: Values set at attributes of page elements
	 * implemented in given HTML file TestCase: TODO
	 *
	 * @return
	 */
	public Collection<? extends RepairSource> getRepairSourcesForAttributeValues() {
		final Set<RepairSource> repairSources = Sets.newHashSet();
		final Set<String> duplicate = Sets.newHashSet();
		// From JavaScript
		for (final String value : this.jsParser.getAttributeValuesFromInfixExpression()) {
			if (!duplicate.contains(value)) {
				duplicate.add(value);
				repairSources.add(new RepairSource(value, RepairSource.Type.JavaScript));
			}
		}
		// From HTML
		for (final String value : this.htmlParser.getAllAttributeValues()) {
			if (!duplicate.contains(value)) {
				duplicate.add(value);
				repairSources.add(new RepairSource(value, RepairSource.Type.HTML));
			}
		}
		return repairSources;
	}

	/**
	 * Get durations for repairing timer event handlers with invalid durations
	 *
	 * Default: Durations based on heuristics JavaScript: TODO HTML: TODO
	 * TestCase: TODO
	 *
	 * @return
	 */
	public Collection<? extends RepairSource> getRepairSourcesForTimerEventDuration() {
		final Set<RepairSource> repairSources = Sets.newHashSet();
		// default
		for (final Integer duration : DefaultParameters.DURATIONS) {
			repairSources.add(new RepairSource(duration.toString(), RepairSource.Type.Default));
		}
		return repairSources;
	}

	// TODO Unimplemented getRepairSources, e.g., asynchronous communication
	// destination, method, callback, etc.
}
