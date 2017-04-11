package jp.mzw.revajaxmutator.config.app;

import java.io.IOException;

import jp.mzw.revajaxmutator.config.mutation.MutateConfiguration;
import jp.mzw.revajaxmutator.config.mutation.defaults.DefaultMutationAnalysisConfig;
import jp.mzw.revajaxmutator.config.mutation.defaults.DefaultProgramRepairConfig;

public class DefaultAppConfig extends AppConfig {

	public DefaultAppConfig(String filename) throws IOException {
		super(filename);
	}

	@Override
	public MutateConfiguration getMutationAnalysisConfig() throws InstantiationException, IllegalAccessException, IOException {
		return new DefaultMutationAnalysisConfig(this);
	}

	@Override
	public MutateConfiguration getProgramRepairConfig() throws IOException {
		return new DefaultProgramRepairConfig(this);
	}
	
}
