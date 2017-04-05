package jp.mzw.revajaxmutator.search;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import jp.mzw.ajaxmutator.generator.MutationFileInformation;
import jp.mzw.ajaxmutator.generator.MutationListManager;
import jp.mzw.revajaxmutator.config.app.AppConfig;
import jp.mzw.revajaxmutator.config.mutation.MutateConfiguration;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class SearcherTest {

	Searcher searcher;
	MutationListManager manager;

	@Before
	public void setup() throws InstantiationException, IllegalAccessException,
			JSONException, IOException {
		searcher = new Searcher(AppTestConfig.class);
		manager = searcher.getMutationListManager();
	}

	@Test
	public void fixerNames() {
		Assert.assertEquals(Arrays.asList("EventCallbackERFixer",
				"EventTargetTSFixer", "TimerEventDurationVIFixer",
				"RequestResponseBodyVIFixer", "RequestURLVIFixer",
				"RequestOnSuccessHandlerERFixer", "RequestMethodRAFixer",
				"DOMSelectionSelectNearbyFixer", "DOMSelectionAtrributeFixer",
				"AttributeModificationTargetVIFixer",
				"AttributeModificationValueERFixer"),
				manager.getListOfMutationName());
	}

	@Ignore // TODO Debug spectrum-based fault localization
	@Test
	public void setWeight() {
		boolean isWeightedInfo = false;
		for (String name : manager.getListOfMutationName()) {
			for (MutationFileInformation info : manager.getMutationFileInformationList(name)) {
				if (info.getWeight() != 0)
					isWeightedInfo = true;
			}
		}
		Assert.assertTrue(isWeightedInfo);
	}

	@Test
	public void search() throws IOException {
		searcher.search();

		String path = manager.getMutationListFilePath();
		File file = new File(path);
		File backupFile = new File(path + ".bak");

		Assert.assertTrue(backupFile.exists());

		file.delete();
		FileUtils.copyFile(backupFile, file);
		backupFile.delete();
	}

	protected static class AppTestConfig extends AppConfig {
		protected AppTestConfig() throws IOException {
			super("app-test.properties");
		}

		@Override
		public MutateConfiguration getMutationAnalysisConfig() throws InstantiationException, IllegalAccessException, IOException {
			return null;
		}

		@Override
		public MutateConfiguration getProgramRepairConfig() throws IOException {
			return null;
		}
		
	}
}
