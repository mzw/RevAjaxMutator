package jp.mzw.revajaxmutator.config.mutation.defaults;

import java.io.IOException;

import com.google.common.collect.ImmutableSet;

import jp.mzw.ajaxmutator.MutateVisitor;
import jp.mzw.ajaxmutator.MutateVisitorBuilder;
import jp.mzw.ajaxmutator.detector.EventAttacherDetector;
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
import jp.mzw.ajaxmutator.mutator.DOMSelectionSelectNearbyMutator;
import jp.mzw.ajaxmutator.mutator.Mutator;
import jp.mzw.ajaxmutator.mutator.replace.among.AttributeModificationTargetRAMutator;
import jp.mzw.ajaxmutator.mutator.replace.among.AttributeModificationValueRAMutator;
import jp.mzw.ajaxmutator.mutator.replace.among.EventCallbackRAMutator;
import jp.mzw.ajaxmutator.mutator.replace.among.EventTargetRAMutator;
import jp.mzw.ajaxmutator.mutator.replace.among.EventTypeRAMutator;
import jp.mzw.ajaxmutator.mutator.replace.among.RequestOnSuccessHandlerRAMutator;
import jp.mzw.ajaxmutator.mutator.replace.among.RequestUrlRAMutator;
import jp.mzw.ajaxmutator.mutator.replace.among.TimerEventCallbackRAMutator;
import jp.mzw.ajaxmutator.mutator.replace.among.TimerEventDurationRAMutator;
import jp.mzw.ajaxmutator.test.conductor.MutationTestConductor;
import jp.mzw.revajaxmutator.config.app.AppConfig;
import jp.mzw.revajaxmutator.config.mutation.MutateConfigurationBase;

public class DefaultMutationAnalysisConfig extends MutateConfigurationBase {

	public DefaultMutationAnalysisConfig(AppConfig config) throws InstantiationException, IllegalAccessException, IOException {

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
		builder.setEventAttacherDetectors(ImmutableSet
				.<EventAttacherDetector> of(new AddEventListenerDetector(),
						new AttachEventDetector(),
						new JQueryEventAttachmentDetector()));
		builder.setTimerEventDetectors(ImmutableSet
				.of(new TimerEventDetector()));
		builder.setRequestDetectors(ImmutableSet
				.of(new JQueryRequestDetector()));
		visitor = builder.build();

		conductor = new MutationTestConductor();
		conductor.setup(config.getRecordedJsFile().getPath(), "", visitor);
		conductor.setSaveInformationInterval(1);

		mutators = ImmutableSet
				.<Mutator<?>> of(
						new EventTargetRAMutator(visitor
								.getEventAttachments()),
						new EventTypeRAMutator(visitor
								.getEventAttachments()),
						new EventCallbackRAMutator(visitor
								.getEventAttachments()),
						new TimerEventDurationRAMutator(visitor
								.getTimerEventAttachmentExpressions()),
						new TimerEventCallbackRAMutator(visitor
								.getTimerEventAttachmentExpressions()),
						new RequestUrlRAMutator(visitor.getRequests()),
						new RequestOnSuccessHandlerRAMutator(visitor
								.getRequests()),
						new DOMSelectionSelectNearbyMutator(),
						new AttributeModificationTargetRAMutator(visitor
								.getAttributeModifications()),
						new AttributeModificationValueRAMutator(visitor
								.getAttributeModifications()));
	}
	
}
