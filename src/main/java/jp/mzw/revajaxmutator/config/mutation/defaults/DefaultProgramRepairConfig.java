package jp.mzw.revajaxmutator.config.mutation.defaults;

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
import jp.mzw.ajaxmutator.detector.generic.AssignmentDetector;
import jp.mzw.ajaxmutator.detector.generic.BreakDetector;
import jp.mzw.ajaxmutator.detector.generic.ContinueDetector;
import jp.mzw.ajaxmutator.detector.generic.ForDetector;
import jp.mzw.ajaxmutator.detector.generic.FuncNodeDetector;
import jp.mzw.ajaxmutator.detector.generic.IfDetector;
import jp.mzw.ajaxmutator.detector.generic.ReturnDetector;
import jp.mzw.ajaxmutator.detector.generic.SwitchDetector;
import jp.mzw.ajaxmutator.detector.generic.VariableDeclarationDetector;
import jp.mzw.ajaxmutator.detector.generic.WhileDetector;
import jp.mzw.ajaxmutator.detector.genprog.StatementDetector;
import jp.mzw.ajaxmutator.detector.jquery.JQueryAppendDetector;
import jp.mzw.ajaxmutator.detector.jquery.JQueryAttributeModificationDetector;
import jp.mzw.ajaxmutator.detector.jquery.JQueryCloneDetector;
import jp.mzw.ajaxmutator.detector.jquery.JQueryDOMSelectionDetector;
import jp.mzw.ajaxmutator.detector.jquery.JQueryEventAttachmentDetector;
import jp.mzw.ajaxmutator.detector.jquery.JQueryRemoveDetector;
import jp.mzw.ajaxmutator.detector.jquery.JQueryReplaceWithDetector;
import jp.mzw.ajaxmutator.detector.jquery.JQueryRequestDetector;
import jp.mzw.ajaxmutator.mutator.Mutator;
import jp.mzw.ajaxmutator.mutator.generic.ChangeConditionSwitchStatementMutator;
import jp.mzw.ajaxmutator.mutator.generic.ChangeValueVariableDecMutator;
import jp.mzw.ajaxmutator.mutator.generic.IfStatementMutator;
import jp.mzw.ajaxmutator.mutator.generic.RemoveBreakContinueForLoopMutator;
import jp.mzw.ajaxmutator.mutator.generic.RemoveBreakContinueWhileLoopMutator;
import jp.mzw.ajaxmutator.mutator.generic.RemoveBreakFromSwitchStatementMutator;
import jp.mzw.ajaxmutator.mutator.generic.RemoveElseIfStatementMutator;
import jp.mzw.ajaxmutator.mutator.generic.RemoveParamsFromFuncNodeMutator;
import jp.mzw.ajaxmutator.mutator.generic.RemoveReturnStatementMutator;
import jp.mzw.ajaxmutator.mutator.generic.RemoveVariableDeclarationMutator;
import jp.mzw.ajaxmutator.mutator.generic.ReplaceArithmeticOperatorsAssignmentMutator;
import jp.mzw.ajaxmutator.mutator.generic.ReplaceArithmeticOperatorsForLoopMutator;
import jp.mzw.ajaxmutator.mutator.generic.ReplaceArithmeticOperatorsVariableDecMutator;
import jp.mzw.ajaxmutator.mutator.generic.ReplaceLogicalOperatorsForLoopMutator;
import jp.mzw.ajaxmutator.mutator.generic.ReplaceLogicalOperatorsIfStatementMutator;
import jp.mzw.ajaxmutator.mutator.generic.ReplaceLogicalOperatorsWhileLoopMutator;
import jp.mzw.ajaxmutator.mutator.generic.ReplaceNumToStrVariableDecMutator;
import jp.mzw.ajaxmutator.mutator.generic.ReplacePlusMinusOperatorForLoopMutator;
import jp.mzw.ajaxmutator.mutator.generic.ReplaceRelationalOperatorsForLoopMutator;
import jp.mzw.ajaxmutator.mutator.generic.ReplaceRelationalOperatorsIfStatementMutator;
import jp.mzw.ajaxmutator.mutator.generic.ReplaceRelationalOperatorsWhileLoopMutator;
import jp.mzw.ajaxmutator.mutator.generic.ReplaceReturnStatementMutator;
import jp.mzw.ajaxmutator.mutator.generic.SwapFuncParamsMutator;
import jp.mzw.ajaxmutator.mutator.genprog.StatementDeleteMutatorToNoOp;
import jp.mzw.ajaxmutator.mutator.genprog.StatementInsertMutatorRA;
import jp.mzw.ajaxmutator.mutator.genprog.StatementSwapMutatorRA;
import jp.mzw.ajaxmutator.test.conductor.MutationTestConductor;
import jp.mzw.revajaxmutator.config.app.AppConfig;
import jp.mzw.revajaxmutator.config.mutation.ConfigHelper;
import jp.mzw.revajaxmutator.config.mutation.MutateConfigurationBase;
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
import jp.mzw.revajaxmutator.search.Sorter.SortType;

public class DefaultProgramRepairConfig extends MutateConfigurationBase {

	public DefaultProgramRepairConfig(AppConfig config) throws IOException {
		if (Boolean.parseBoolean(config.getParam("genprog")) && config.getSortType().equals(SortType.RANDOM)) {
			MutateVisitorBuilder builder = MutateVisitor.emptyBuilder();
			builder.setStatementDetectors(ImmutableSet.of(new StatementDetector()));
			MutateVisitor visitor = builder.build();

	        conductor = new MutationTestConductor();
			conductor.setup(config.getRecordedJsFile().getPath(), "", visitor);
			conductor.setSaveInformationInterval(1);

			mutators = ImmutableSet.<Mutator<?>> of(
					new StatementSwapMutatorRA(visitor.getStatements()),
					new StatementInsertMutatorRA(visitor.getStatements()),
					new StatementDeleteMutatorToNoOp());
			return;
		} else if (Boolean.parseBoolean(config.getParam("generic")) && config.getSortType().equals(SortType.RANDOM)) {
			MutateVisitorBuilder builder = MutateVisitor.emptyBuilder();
			builder.setAssignmentDetectors(ImmutableSet.of(new AssignmentDetector()));
			builder.setBreakDetectors(ImmutableSet.of(new BreakDetector()));
			builder.setContinueDetectors(ImmutableSet.of(new ContinueDetector()));
			builder.setForDetectors(ImmutableSet.of(new ForDetector()));
			builder.setFuncNodeDetectors(ImmutableSet.of(new FuncNodeDetector()));
			builder.setIfDetectors(ImmutableSet.of(new IfDetector()));
			builder.setReturnDetectors(ImmutableSet.of(new ReturnDetector()));
			builder.setSwitchDetectors(ImmutableSet.of(new SwitchDetector()));
			builder.setVariableDeclarationDetectors(ImmutableSet.of(new VariableDeclarationDetector()));
			builder.setWhileDetectors(ImmutableSet.of(new WhileDetector()));
			visitor = builder.build();

			conductor = new MutationTestConductor();
			conductor.setup(config.getRecordedJsFile().getPath(), "", visitor);
			conductor.setSaveInformationInterval(1);

			mutators = ImmutableSet.<Mutator<?>>of(
					new ChangeConditionSwitchStatementMutator(),
					new ChangeValueVariableDecMutator(),
					new IfStatementMutator(),
					new RemoveBreakContinueForLoopMutator(),
					new RemoveBreakContinueWhileLoopMutator(),
					new RemoveBreakFromSwitchStatementMutator(),
					new RemoveElseIfStatementMutator(),
					new RemoveParamsFromFuncNodeMutator(),
					new RemoveReturnStatementMutator(),
					new RemoveVariableDeclarationMutator(),
					new ReplaceArithmeticOperatorsAssignmentMutator().choose(false),
					new ReplaceArithmeticOperatorsForLoopMutator().choose(false),
					new ReplaceArithmeticOperatorsVariableDecMutator().choose(false),
					new ReplaceLogicalOperatorsForLoopMutator(),
					new ReplaceLogicalOperatorsIfStatementMutator(),
					new ReplaceLogicalOperatorsWhileLoopMutator(),
					new ReplaceNumToStrVariableDecMutator(),
					new ReplacePlusMinusOperatorForLoopMutator(),
					new ReplaceRelationalOperatorsForLoopMutator().choose(false),
					new ReplaceRelationalOperatorsIfStatementMutator().choose(false),
					new ReplaceRelationalOperatorsWhileLoopMutator().choose(false),
					new ReplaceReturnStatementMutator(),
					new SwapFuncParamsMutator());
			return;
		}

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
