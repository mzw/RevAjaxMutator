package jp.mzw.revajaxmutator.config.mutation.defaults;

import java.io.IOException;

import com.google.common.collect.ImmutableSet;

import jp.mzw.ajaxmutator.MutateVisitor;
import jp.mzw.ajaxmutator.MutateVisitorBuilder;
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
import jp.mzw.ajaxmutator.test.conductor.MutationTestConductor;
import jp.mzw.revajaxmutator.config.app.AppConfig;
import jp.mzw.revajaxmutator.config.mutation.MutateConfigurationBase;

public class GenericMutationAnalysisConfig extends MutateConfigurationBase {

	public GenericMutationAnalysisConfig(AppConfig config) throws IOException {
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
				new ReplaceArithmeticOperatorsAssignmentMutator(),
				new ReplaceArithmeticOperatorsForLoopMutator(),
				new ReplaceArithmeticOperatorsVariableDecMutator(),
				new ReplaceLogicalOperatorsForLoopMutator(),
				new ReplaceLogicalOperatorsIfStatementMutator(),
				new ReplaceLogicalOperatorsWhileLoopMutator(),
				new ReplaceNumToStrVariableDecMutator(),
				new ReplacePlusMinusOperatorForLoopMutator(),
				new ReplaceRelationalOperatorsForLoopMutator(),
				new ReplaceRelationalOperatorsIfStatementMutator(),
				new ReplaceRelationalOperatorsWhileLoopMutator(),
				new ReplaceReturnStatementMutator(),
				new SwapFuncParamsMutator());
	}
}
