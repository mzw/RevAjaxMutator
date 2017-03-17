package jp.mzw.revajaxmutator.config.mutation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import jp.mzw.revajaxmutator.parser.RepairSource;
import jp.mzw.revajaxmutator.parser.html.EventSet;
import jp.mzw.revajaxmutator.parser.html.HTMLParser;
import jp.mzw.revajaxmutator.parser.java.TestCaseParser;
import jp.mzw.revajaxmutator.parser.javascript.JavaScriptParser;

public class ConfigHelper {
	private TestCaseParser testCaseParser;
	private HTMLParser htmlParser;
	private JavaScriptParser jsParser;
	private EventSet eventSet = null;

	public ConfigHelper parseHtml(File file) throws IOException {
		this.htmlParser = new HTMLParser(file);
		return this;
	}

	public ConfigHelper parseTestCase(File file) throws IOException {
		this.testCaseParser = new TestCaseParser(file);
		return this;
	}

	public ConfigHelper parseJavaScript(File file) throws IOException {
		this.jsParser = new JavaScriptParser(file);
		return this;
	}

	public List<RepairSource> getRepairSourcesForDomSelectionAttributeFixer() {
		final List<RepairSource> repairSources = new ArrayList<RepairSource>();
		final ArrayList<String> duplicate = new ArrayList<>();

		// add default
		repairSources.add(new RepairSource("document", RepairSource.Type.DEFAULT));

		// From Test Case
		for (final String attr : this.testCaseParser.getAttributeValues()) {
			if (!duplicate.contains(attr)) {
				duplicate.add(attr);
				repairSources.add(new RepairSource(attr, RepairSource.Type.TESTCASE));
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

	public List<RepairSource> getRepairSourcesForEventTarget() {
		if (this.eventSet == null) {
			this.eventSet = this.htmlParser.getAllEventSet();
		}
		final List<RepairSource> repairSources = new ArrayList<RepairSource>();
		final ArrayList<String> duplicate = new ArrayList<>();

		// add default
		repairSources.add(new RepairSource("$(document)", RepairSource.Type.DEFAULT));
		// From Test Case
		for (final String attr : this.testCaseParser.getAttributeValues()) {
			if (!duplicate.contains(attr)) {
				duplicate.add(attr);
				repairSources.add(new RepairSource("$(" + attr + ")", RepairSource.Type.TESTCASE));
			}
		}
		// From HTML
		for (final String eventTarget : this.eventSet.getTargetSet()) {
			repairSources.add(new RepairSource(eventTarget, RepairSource.Type.HTML));
		}
		return repairSources;
	}

	public List<RepairSource> getRepairSourcesForEventType() {
		if (this.eventSet == null) {
			this.eventSet = this.htmlParser.getAllEventSet();
		}

		// From HTML
		final List<RepairSource> repairSources = new ArrayList<RepairSource>();
		final Set<String> htmlSet = new HashSet<>();
		for (final String eventType : this.eventSet.getTypeSet()) {
			htmlSet.add(eventType);
		}

		for (final String eventType : htmlSet) {
			repairSources.add(new RepairSource(eventType, RepairSource.Type.HTML));
		}

		// From JavaScript
		final Set<String> jsSet = new HashSet<>();
		for (final String name : this.jsParser.getEventTypes()) {
			// add similar events to the list of repair sources
			if (keyboardPressETKeywords.contains(name)) {
				for (final String eventType : keyboardPressETKeywords) {
					jsSet.add(eventType);
				}
			} else if (mouseClickETKeywords.contains(name)) {
				for (final String eventType : mouseClickETKeywords) {
					jsSet.add(eventType);
				}
			} else if (editETKeywords.contains(name)) {
				for (final String eventType : editETKeywords) {
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

	public List<RepairSource> getRepairSourcesForEventCallback() {
		if (this.eventSet == null) {
			this.eventSet = this.htmlParser.getAllEventSet();
		}
		final List<RepairSource> repairSources = new ArrayList<RepairSource>();
		final ArrayList<String> duplicate = new ArrayList<>();

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

	public List<RepairSource> getRepairSourcesForTimerEventDuration() {
		final List<RepairSource> repairSources = new ArrayList<RepairSource>();
		// default
		final String[] durations = new String[] { "0", "50", "100", "250", "500", "750", "1000" };
		for (final String duration : durations) {
			repairSources.add(new RepairSource(duration, RepairSource.Type.DEFAULT));
		}
		return repairSources;
	}

	public List<RepairSource> getRepairSourcesForAttributeValues() {
		final List<RepairSource> repairSources = new ArrayList<RepairSource>();
		final ArrayList<String> duplicate = new ArrayList<>();
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

	public List<RepairSource> getRepairSourcesForAppendedDOM() {
		final List<RepairSource> repairSources = new ArrayList<RepairSource>();
		return repairSources;
	}

	private static final Set<String> keyboardPressETKeywords = ImmutableSet.of("textinput", "keyup", "keypress", "keydown");
	private static final Set<String> mouseClickETKeywords = ImmutableSet.of("click", "contextmenu", "dblclick", "mousedown", "mouseup", "mouseenter",
			"mouseleave", "mouseover", "mousewheel", "wheel");
	private static final Set<String> editETKeywords = ImmutableSet.of("change", "copy", "cut", "paste", "reset", "select", "textinput");
}
