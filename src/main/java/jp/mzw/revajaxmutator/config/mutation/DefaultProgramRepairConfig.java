package jp.mzw.revajaxmutator.config.mutation;

import java.io.IOException;

import com.google.common.collect.ImmutableSet;

import jp.mzw.ajaxmutator.MutateVisitor;
import jp.mzw.ajaxmutator.MutateVisitorBuilder;
import jp.mzw.ajaxmutator.detector.dom.AppendChildDetector;
import jp.mzw.ajaxmutator.detector.dom.AttributeAssignmentDetector;
import jp.mzw.ajaxmutator.detector.dom.CloneNodeDetector;
import jp.mzw.ajaxmutator.detector.dom.CreateElementDetector;
import jp.mzw.ajaxmutator.detector.dom.DOMNormalizationDetector;
import jp.mzw.ajaxmutator.detector.dom.DOMSelectionDetector;
import jp.mzw.ajaxmutator.detector.dom.RemoveChildDetector;
import jp.mzw.ajaxmutator.detector.dom.ReplaceChildDetector;
import jp.mzw.ajaxmutator.detector.dom.SetAttributeDetector;
import jp.mzw.ajaxmutator.detector.event.AddEventListenerDetector;
import jp.mzw.ajaxmutator.detector.event.AttachEventDetector;
import jp.mzw.ajaxmutator.detector.event.TimerEventDetector;
import jp.mzw.ajaxmutator.detector.jquery.JQueryAppendDetector;
import jp.mzw.ajaxmutator.detector.jquery.JQueryAttributeModificationDetector;
import jp.mzw.ajaxmutator.detector.jquery.JQueryCloneDetector;
import jp.mzw.ajaxmutator.detector.jquery.JQueryDOMSelectionDetector;
import jp.mzw.ajaxmutator.detector.jquery.JQueryEventAttachmentDetector;
import jp.mzw.ajaxmutator.detector.jquery.JQueryRemoveDetector;
import jp.mzw.ajaxmutator.detector.jquery.JQueryReplaceWithDetector;
import jp.mzw.ajaxmutator.detector.jquery.JQueryRequestDetector;
import jp.mzw.ajaxmutator.mutator.Mutator;
import jp.mzw.ajaxmutator.test.conductor.MutationTestConductor;
import jp.mzw.revajaxmutator.config.app.AppConfig;
import jp.mzw.revajaxmutator.fixer.AppendedDOMRAFixer;
import jp.mzw.revajaxmutator.fixer.AttributeModificationTargetVIFixer;
import jp.mzw.revajaxmutator.fixer.AttributeModificationValueERFixer;
import jp.mzw.revajaxmutator.fixer.DOMCloningToNoOpFixer;
import jp.mzw.revajaxmutator.fixer.DOMCreationToNoOpFixer;
import jp.mzw.revajaxmutator.fixer.DOMNormalizationToNoOpFixer;
import jp.mzw.revajaxmutator.fixer.DOMRemovalToNoOpFixer;
import jp.mzw.revajaxmutator.fixer.DOMReplacementSrcTargetFixer;
import jp.mzw.revajaxmutator.fixer.DOMSelectionAtrributeFixer;
import jp.mzw.revajaxmutator.fixer.DOMSelectionSelectNearbyFixer;
import jp.mzw.revajaxmutator.fixer.EventCallbackERFixer;
import jp.mzw.revajaxmutator.fixer.EventTargetTSFixer;
import jp.mzw.revajaxmutator.fixer.EventTypeTSFixer;
import jp.mzw.revajaxmutator.fixer.RequestMethodRAFixer;
import jp.mzw.revajaxmutator.fixer.RequestOnSuccessHandlerERFixer;
import jp.mzw.revajaxmutator.fixer.RequestResponseBodyVIFixer;
import jp.mzw.revajaxmutator.fixer.RequestURLVIFixer;
import jp.mzw.revajaxmutator.fixer.TimerEventCallbackERFixer;
import jp.mzw.revajaxmutator.fixer.TimerEventDurationVIFixer;

public class DefaultProgramRepairConfig extends MutateConfigurationBase {

	public DefaultProgramRepairConfig(AppConfig config) throws IOException {

		MutateVisitorBuilder builder = MutateVisitor.emptyBuilder();
		builder.setAttributeModificationDetectors(ImmutableSet.of(
				new AttributeAssignmentDetector(),
				new SetAttributeDetector(),
				new JQueryAttributeModificationDetector()));
		builder.setDomAppendingDetectors(ImmutableSet.of(
				new AppendChildDetector(), new JQueryAppendDetector()));
		builder.setDomCreationDetectors(ImmutableSet
				.of(new CreateElementDetector()));
		builder.setDomCloningDetectors(ImmutableSet.of(
				new CloneNodeDetector(), new JQueryCloneDetector()));
		builder.setDomNormalizationDetectors(ImmutableSet
				.of(new DOMNormalizationDetector()));
		builder.setDomReplacementDetectors(ImmutableSet
				.of(new ReplaceChildDetector(),
						new JQueryReplaceWithDetector()));
		builder.setDomRemovalDetectors(ImmutableSet.of(
				new RemoveChildDetector(), new JQueryRemoveDetector()));
		builder.setDomSelectionDetectors(ImmutableSet.of(
				new DOMSelectionDetector(),
				new JQueryDOMSelectionDetector()));
		builder.setEventAttacherDetectors(ImmutableSet.of(
				new AddEventListenerDetector(), new AttachEventDetector(),
				new JQueryEventAttachmentDetector()));
		builder.setTimerEventDetectors(ImmutableSet
				.of(new TimerEventDetector()));
		builder.setRequestDetectors(ImmutableSet
				.of(new JQueryRequestDetector()));
		visitor = builder.build();

		conductor = new MutationTestConductor();
		conductor.setup(config.getRecordedJsFile().getPath(), "", visitor);
		conductor.setSaveInformationInterval(1);

		ConfigHelper configHelper = new ConfigHelper()
				.parseHtml(config.getRecordedHtmlFile())
				.parseTestCase(config.getTestcaseFile())
				.parseJavaScript(config.getRecordedJsFile());

		mutators = ImmutableSet
				.<Mutator<?>> of(
						new EventTargetTSFixer(visitor
								.getEventAttachments(), configHelper
								.getRepairSourcesForEventTarget()),
						new EventTypeTSFixer(visitor.getEventAttachments(),
								configHelper.getRepairSourcesForEventType()),
						new EventCallbackERFixer(visitor
								.getEventAttachments(), configHelper
								.getRepairSourcesForEventCallback()),
						new TimerEventDurationVIFixer(configHelper
								.getRepairSourcesForTimerEventDuration()),
						new TimerEventCallbackERFixer(visitor
								.getTimerEventAttachmentExpressions()),
						new AppendedDOMRAFixer(visitor.getDomAppendings()),
						new AttributeModificationTargetVIFixer(visitor
								.getAttributeModifications()),
						new AttributeModificationValueERFixer(visitor
								.getAttributeModifications(), configHelper
								.getRepairSourcesForAttributeValues()),
						new DOMSelectionSelectNearbyFixer(),
						new RequestOnSuccessHandlerERFixer(visitor
								.getRequests()),
						new RequestMethodRAFixer(visitor.getRequests()),
						new RequestURLVIFixer(visitor.getRequests()),
						new RequestResponseBodyVIFixer(),
						new DOMCreationToNoOpFixer(),
						new DOMRemovalToNoOpFixer(),
						new DOMReplacementSrcTargetFixer(),
						new DOMCloningToNoOpFixer(),
						new DOMNormalizationToNoOpFixer(),
						new DOMSelectionAtrributeFixer(
								visitor.getDomSelections(),
								configHelper
										.getRepairSourcesForDomSelectionAttributeFixer()));
	}
}
