package jp.mzw.revajaxmutator.search;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import jp.mzw.ajaxmutator.generator.MutationFileInformation;
import jp.mzw.ajaxmutator.generator.MutationListManager;
import jp.mzw.revajaxmutator.config.app.AppConfig;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;

public class SearcherTest {

	Searcher searcher;
	MutationListManager manager;

	@Before
	public void setup() throws InstantiationException, IllegalAccessException,
			JSONException, IOException {
		searcher = new Searcher(AppConfig.class);
		manager = searcher.getMutationListManager();
	}

	@Test
	public void fixerNames() {
		assertEquals(Arrays.asList("EventCallbackERFixer",
				"EventTargetTSFixer", "TimerEventDurationVIFixer",
				"RequestResponseBodyVIFixer", "RequestURLVIFixer",
				"RequestOnSuccessHandlerERFixer", "RequestMethodRAFixer",
				"DOMSelectionSelectNearbyFixer", "DOMSelectionAtrributeFixer",
				"AttributeModificationTargetVIFixer",
				"AttributeModificationValueERFixer"),
				manager.getListOfMutationName());
	}

	@Test
	public void setWeight() {
		boolean isWeightedInfo = false;
		for (String name : manager.getListOfMutationName()) {
			for (MutationFileInformation info : manager
					.getMutationFileInformationList(name)) {
				if (info.getWeight() != 0)
					isWeightedInfo = true;
			}
		}
		assertTrue(isWeightedInfo);
	}

	@Test
	public void search() throws IOException {
		searcher.search();

		String path = manager.getMutationListFilePath();
		File file = new File(path);
		File backupFile = new File(path + ".bak");

		assertTrue(backupFile.exists());

		file.delete();
		FileUtils.copyFile(backupFile, file);
		backupFile.delete();
	}

}
