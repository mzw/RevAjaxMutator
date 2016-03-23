package jp.mzw.revajaxmutator.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.mzw.revajaxmutator.parser.EventSet;
import jp.mzw.revajaxmutator.parser.HTMLParser;
import jp.mzw.revajaxmutator.parser.RepairSource;
import jp.mzw.revajaxmutator.parser.TestCaseParser;

public class ConfigHelper {
	private TestCaseParser testCaseParser;
	private HTMLParser htmlParser;
	private EventSet eventSet = null;

	public ConfigHelper parseHtml(File file) throws IOException {
		this.htmlParser = new HTMLParser(file);
		return this;
	}

	public ConfigHelper parseTestCase(File file) throws IOException {
		this.testCaseParser = new TestCaseParser(file);
		return this;
	}

	public List<RepairSource> getRepairSourcesForDomSelectionAttributeFixer() {
		List<RepairSource> repairSources = new ArrayList<RepairSource>();

		// add default
		repairSources.add(new RepairSource("document",
				RepairSource.Type.DEFAULT));
		// From Test Case
		for (String attr : testCaseParser.getAttributeValues()) {
			System.out.println(attr);
			repairSources
					.add(new RepairSource(attr, RepairSource.Type.TESTCASE));
		}
		// From HTML
		List<String> htmlAttributes = htmlParser.getAllElementIdentifier();
		for (String attr : htmlAttributes) {
			repairSources.add(new RepairSource(attr, RepairSource.Type.HTML));
		}

		return repairSources;
	}

	public List<RepairSource> getRepairSourcesForEventTarget() {
		if (eventSet == null) {
			eventSet = htmlParser.getAllEventSet();
		}
		List<RepairSource> repairSources = new ArrayList<RepairSource>();
		// add default
		repairSources.add(new RepairSource("$(document)",
				RepairSource.Type.DEFAULT));
		// add testcase
		repairSources.add(new RepairSource("$('.categories-search')",
				RepairSource.Type.TESTCASE));

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
		List<RepairSource> repairSources = new ArrayList<RepairSource>();
		for (String eventType : eventSet.getTypeSet()) {
			repairSources.add(new RepairSource(eventType,
					RepairSource.Type.HTML));
		}
		return repairSources;
	}

	public List<RepairSource> getRepairSourcesForEventCallback() {
		if (eventSet == null) {
			eventSet = htmlParser.getAllEventSet();
		}
		List<RepairSource> repairSources = new ArrayList<RepairSource>();
		for (String eventCallback : eventSet.getCallbackSet()) {
			repairSources.add(new RepairSource(eventCallback,
					RepairSource.Type.HTML));
		}
		// add for quizzy
		// Set<String> quizzyFunctionsSet =
		// ImmutableSet.of("checkRadioButton","showAndHideDiscription","startQuiz","requestNextQuestion","checkQuestion","restartQuizzy");
		// for(String quizzyString : quizzyFunctionsSet){
		// repairSources.add(new RepairSource(quizzyString,
		// CandidateSource.JS));
		// }

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
		List<String> attributeValues = htmlParser.getAllAttributeValues();
		for (String attributeValue : attributeValues) {
			repairSources.add(new RepairSource(attributeValue,
					RepairSource.Type.HTML));
		}
		repairSources.add(new RepairSource("\"editinlineschedule-\" + id",
				RepairSource.Type.JavaScript));
		repairSources
				.add(new RepairSource(
						"\"https://maps.googleapis.com/maps/api/js?sensor=false&libraries=places&callback=hmapspro_map_initialiser\"",
						RepairSource.Type.TESTCASE));
		return repairSources;
	}

	public List<RepairSource> getRepairSourcesForAppendedDOM() {
		List<RepairSource> repairSources = new ArrayList<RepairSource>();
		// repairSources.add(new
		// RepairSource("'<h2 class=\"screen-reader-text\">' + pressThisL10n.allMediaHeading + '</h2><ul class=\"wppt-all-media-list\"/>'",
		// CandidateSource.TESTCASE));
		// repairSources.add(new
		// RepairSource("$( '<p class=\"' + className +'\">' + stripTags( msg ) + '</p>' )",
		// CandidateSource.TESTCASE));
		return repairSources;
	}
}
