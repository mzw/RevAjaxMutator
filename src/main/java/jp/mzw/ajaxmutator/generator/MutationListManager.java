package jp.mzw.ajaxmutator.generator;

import jp.mzw.ajaxmutator.util.Util;

import java.io.File;
import java.util.*;

/**
 * @author Kazuki Nishiura
 */
public class MutationListManager {
	public static final String MUTATION_LIST_FILE_NAME = "mutation_list.csv";

	private List<String> mutationTitles = new ArrayList<String>();
	private Map<String, List<MutationFileInformation>> mutationFiles = new HashMap<String, List<MutationFileInformation>>();
	private String reportOutputDir;

	public MutationListManager(String reportOutputDir) {
		this.reportOutputDir = reportOutputDir;
	}

	public void addMutationFileInformation(String title, MutationFileInformation fileInformation) {
		if (!mutationFiles.containsKey(title)) {
			mutationTitles.add(title);
			mutationFiles.put(title, new ArrayList<MutationFileInformation>());
		}
		mutationFiles.get(title).add(fileInformation);
	}

	/**
	 * @return return of names that represents classes of mutations.
	 */
	public List<String> getListOfMutationName() {
		return mutationTitles;
	}

	/**
	 * @param name
	 *            String that represents an class of mutation. See
	 *            {@link #getListOfMutationName()}.
	 * @return List of mutation file which belong to given class.
	 */
	public List<MutationFileInformation> getMutationFileInformationList(String name) {
		return mutationFiles.get(name);
	}

	public List<MutationFileInformation> getMutationFileInformationList() {
		List<MutationFileInformation> list = new ArrayList<MutationFileInformation>();
		for (List<MutationFileInformation> fileInfoList : mutationFiles.values()) {
			for (MutationFileInformation mutationfileinfomation : fileInfoList) {
				list.add(mutationfileinfomation);
			}
		}
		return list;
	}

	public int getNumberOfUnkilledMutants() {
		int total = 0;
		for (List<MutationFileInformation> fileInfoList : mutationFiles.values()) {
			for (MutationFileInformation fileInfo : fileInfoList) {
				if (fileInfo.getState() == MutationFileInformation.State.NON_EQUIVALENT_LIVE) {
					total++;
				}
			}
		}
		return total;
	}

	public int getNumberOfMaxMutants() {
		int total = 0;
		for (List<MutationFileInformation> fileInfoList : mutationFiles.values()) {
			for (MutationFileInformation fileInfo : fileInfoList) {
				if (fileInfo.getState() != MutationFileInformation.State.EQUIVALENT) {
					total++;
				}
			}
		}
		return total;
	}

	private String generateContentsOfMutationReport() {
		StringBuilder builder = new StringBuilder();
		for (String title : mutationTitles) {
			builder.append(title).append(',').append(mutationFiles.get(title).size()).append(System.lineSeparator());
			for (MutationFileInformation info : mutationFiles.get(title)) {
				builder.append(info.getFileName()).append(',').append(info.getKilledStatusAsString()).append(',')
						.append(info.getAbsolutePath()).append(',').append(info.getStartLine()).append(',')
						.append(info.getEndLine()).append(',').append(info.getMutatable()).append(',')
						.append(info.getFixer()).append(',').append(info.getRepairValue()).append(',')
						.append(info.getRepairSource()).append(',').append(info.getNumOfPassedTest()).append(',')
						.append(info.getNumOfFailedTest()).append(',').append(info.getTestResults().toString())
						.append(System.lineSeparator());
			}
		}
		return builder.toString();
	}

	public boolean generateMutationListFile() {
		return Util.writeToFile(getMutationListFilePath(), generateContentsOfMutationReport());
	}

	public void readExistingMutationListFile() {
		clear();
		List<String> lines = Util.readFromFile(getMutationListFilePath());
		String title = null;
		for (String line : lines) {
			String[] elms = line.split(",");
			if (elms.length == 2) {
				title = elms[0];
				mutationTitles.add(title);
				mutationFiles.put(title, new ArrayList<MutationFileInformation>());
				continue;
			}
			// for repair
			else if (elms.length == 12) {
				mutationFiles.get(title)
						.add(new MutationFileInformation(elms[0], elms[2],
								MutationFileInformation.State.fromString(elms[1]), Integer.parseInt(elms[3]),
								Integer.parseInt(elms[4]), elms[5], elms[6], elms[7], elms[8]));
				continue;
			}
			mutationFiles.get(title).add(
					new MutationFileInformation(elms[0], elms[2], MutationFileInformation.State.fromString(elms[1])));
		}
	}

	private void clear() {
		mutationTitles.clear();
		mutationFiles.clear();
	}

	public String getMutationListFilePath() {
		return reportOutputDir + File.separator + MUTATION_LIST_FILE_NAME;
	}
}
