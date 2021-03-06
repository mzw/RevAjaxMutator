package jp.mzw.revajaxmutator.search;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.mzw.ajaxmutator.generator.MutationFileInformation;
import jp.mzw.ajaxmutator.generator.MutationFileWriter;
import jp.mzw.ajaxmutator.generator.MutationListManager;
import jp.mzw.revajaxmutator.test.result.Coverage;

public class Searcher {
	protected static Logger LOGGER = LoggerFactory.getLogger(Searcher.class);

	private MutationListManager manager;
	private Sorter.SortType sortType;

	public Searcher(final MutationListManager manager, final Sorter.SortType sortType)
			throws InstantiationException, IllegalAccessException, JSONException, IOException {
		this.manager = manager;
		this.sortType = sortType;
	}

	public void search() throws IOException {
		Sorter sorter = new Sorter(this.manager);
		List<MutationFileInformation> list = sorter.sort(this.sortType);

		String path = manager.getMutationListFilePath();
		File file = new File(path);
		File backupFile = new File(path + ".bak");
		FileUtils.copyFile(file, backupFile);

		MutationListManager _manager = new MutationListManager(file.getParent());
		for (MutationFileInformation info : list) {
			_manager.addMutationFileInformation("Sorted", info);
		}
		_manager.generateMutationListFile();
	}

	public static MutationListManager getMutationListManager(final File recordedJsFile) throws JSONException, IOException {
		File mutantDir = new File(recordedJsFile.getParent(), MutationFileWriter.DEFAULT_FOLDER_NAME);
		File mutationListFile = new File(mutantDir, MutationListManager.MUTATION_LIST_FILE_NAME);
		if (!mutationListFile.exists()) {
			LOGGER.error("Cannot find {}", mutationListFile.getAbsolutePath());
			return null;
		}
		MutationListManager manager = new MutationListManager(mutantDir.getAbsolutePath());
		manager.readExistingMutationListFile();
		return manager;
	}

	public void setWeight(final Collection<File> files, final URL url, final String pathToJsFile) throws JSONException, MalformedURLException, IOException {
		for (File file : files) {
			if (!file.exists()) {
				continue;
			}
			JSONArray failure = Coverage.getCoverageResults(Coverage.parse(file), new URL(url, pathToJsFile).getPath());
			if (failure == null)
				return;
			int line_num = failure.length();
			double[] weight = new double[line_num];
			int max = 0;
			for (int i = 1; i < line_num; i++) {
				int failure_cover_freq = Coverage.getCoverFreq(failure.get(i));
				if (failure_cover_freq > max) {
					max = failure_cover_freq;
				}
			}
			for (int i = 1; i < line_num; i++) {
				Object line = failure.get(i);
				int freq = Coverage.getCoverFreq(line);
				// put assumption that all weight should be under 9
				weight[i] = (int) (9.0 * (1.0 - (double) freq / (double) max));
			}

			// Set
			for (String name : manager.getListOfMutationName()) {
				for (MutationFileInformation info : manager.getMutationFileInformationList(name)) {
					info.setWeight(weight[info.getStartLine()]);
				}
			}
		}
	}
}
