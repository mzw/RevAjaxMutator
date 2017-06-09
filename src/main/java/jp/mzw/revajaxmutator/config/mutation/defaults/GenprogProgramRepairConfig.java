package jp.mzw.revajaxmutator.config.mutation.defaults;

import java.io.IOException;

import com.google.common.collect.ImmutableSet;

import jp.mzw.ajaxmutator.MutateVisitor;
import jp.mzw.ajaxmutator.MutateVisitorBuilder;
import jp.mzw.ajaxmutator.detector.genprog.StatementDetector;
import jp.mzw.ajaxmutator.mutator.Mutator;
import jp.mzw.ajaxmutator.mutator.genprog.StatementDeleteMutatorToNoOp;
import jp.mzw.ajaxmutator.mutator.genprog.StatementInsertMutatorRA;
import jp.mzw.ajaxmutator.mutator.genprog.StatementSwapMutatorRA;
import jp.mzw.ajaxmutator.test.conductor.MutationTestConductor;
import jp.mzw.revajaxmutator.config.app.AppConfig;
import jp.mzw.revajaxmutator.config.mutation.MutateConfigurationBase;

public class GenprogProgramRepairConfig extends MutateConfigurationBase {

	public GenprogProgramRepairConfig(final AppConfig config) throws IOException {
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
	}
	
}
