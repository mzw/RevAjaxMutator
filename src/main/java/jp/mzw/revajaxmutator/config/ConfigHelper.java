package jp.mzw.revajaxmutator.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
		List<RepairSource> repairSources = new ArrayList<RepairSource>();
		ArrayList<String> duplicate = new ArrayList<>();

		// add default
		repairSources.add(new RepairSource("document",
				RepairSource.Type.DEFAULT));

		// From Test Case
		for (String attr : testCaseParser.getAttributeValues()) {
			if (!duplicate.contains(attr)) {
				duplicate.add(attr);
				repairSources.add(new RepairSource(attr,
						RepairSource.Type.TESTCASE));
			}
		}
		// From HTML
		for (String attr : htmlParser.getAllElementIdentifier()) {
			if (!duplicate.contains(attr)) {
				duplicate.add(attr);
				repairSources
						.add(new RepairSource(attr, RepairSource.Type.HTML));
			}
		}

		return repairSources;
	}

	public List<RepairSource> getRepairSourcesForEventTarget() {
		if (eventSet == null) {
			eventSet = htmlParser.getAllEventSet();
		}
		List<RepairSource> repairSources = new ArrayList<RepairSource>();
		ArrayList<String> duplicate = new ArrayList<>();

		// add default
		repairSources.add(new RepairSource("$(document)",
				RepairSource.Type.DEFAULT));
		// From Test Case
		for (String attr : testCaseParser.getAttributeValues()) {
			if (!duplicate.contains(attr)) {
				duplicate.add(attr);
				repairSources.add(new RepairSource("$(" + attr + ")",
						RepairSource.Type.TESTCASE));
			}
		}
		// From HTML
		for (String eventTarget : eventSet.getTargetSet()) {
			repairSources.add(new RepairSource(eventTarget,
					RepairSource.Type.HTML));
		}
		return repairSources;
	}

	public List<RepairSource> getRepairSourcesForEventType() {
		if (eventSet == null) {
			eventSet = htmlParser.getAllEventSet();
		}
		// From HTML
		List<RepairSource> repairSources = new ArrayList<RepairSource>();
		ArrayList<String> duplicate = new ArrayList<>();
		for (String eventType : eventSet.getTypeSet()) {
			if (!duplicate.contains(eventType)) {
				duplicate.add(eventType);
				repairSources.add(new RepairSource(eventType,
						RepairSource.Type.HTML));
			}
		}
		return repairSources;
	}

	public List<RepairSource> getRepairSourcesForEventCallback() {
		if (eventSet == null) {
			eventSet = htmlParser.getAllEventSet();
		}
		List<RepairSource> repairSources = new ArrayList<RepairSource>();
		ArrayList<String> duplicate = new ArrayList<>();

		// From HTML
		for (String eventCallback : eventSet.getCallbackSet()) {
			if (!duplicate.contains(eventCallback)) {
				duplicate.add(eventCallback);
				repairSources.add(new RepairSource(eventCallback,
						RepairSource.Type.HTML));
			}
		}
		// From JavaScript
		for (String name : jsParser.getFunctionNames()) {
			if (!duplicate.contains(name)) {
				duplicate.add(name);
				repairSources.add(new RepairSource(name,
						RepairSource.Type.JavaScript));
			}
		}
		return repairSources;
	}

	public List<RepairSource> getRepairSourcesForTimerEventDuration() {
		List<RepairSource> repairSources = new ArrayList<RepairSource>();
		// default
		String[] durations = new String[] { "0", "50", "100", "250", "500",
				"750", "1000" };
		for (String duration : durations) {
			repairSources.add(new RepairSource(duration,
					RepairSource.Type.DEFAULT));
		}
		return repairSources;
	}

	public List<RepairSource> getRepairSourcesForAttributeValues() {
		List<RepairSource> repairSources = new ArrayList<RepairSource>();
		ArrayList<String> duplicate = new ArrayList<>();
		// From HTML
		for (String value : htmlParser.getAllAttributeValues()) {
			if (!duplicate.contains(value)) {
				duplicate.add(value);
				repairSources.add(new RepairSource(value,
						RepairSource.Type.HTML));
			}
		}
		return repairSources;
	}

	public List<RepairSource> getRepairSourcesForAppendedDOM() {
		List<RepairSource> repairSources = new ArrayList<RepairSource>();
		return repairSources;
	}
}
