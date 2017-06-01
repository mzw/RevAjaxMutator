package jp.mzw.revajaxmutator.search;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import jp.mzw.ajaxmutator.generator.MutationFileInformation;
import jp.mzw.ajaxmutator.generator.MutationListManager;

public class Sorter {

	/**
	 * Types to prioritize mutants to run tests on
	 * 
	 * @author Yuta Maezawa
	 *
	 */
	public enum SortType {
		SIMPLE_LOCATION, SIMPLE_AJAX_DEFECT, SIMPLE_REPAIR_SOURCE, RSREPAIR, DFS, REPAIR_SOURCE_DFS, BFS_REPAIR_SOURCE, BFS_FIXER_CLASS, BFS_DEFECT_CLASS, BFS_AJAX_FEATURE, RANDOM, IDDFS_CANDIDATE_SOURCE, IDDFS_FIXER_CLASS, IDDFS_DEFECT_CLASS, IDDFS_AJAX_FEATURE, IDDFS_CANDIDATE_SOURCE_PERCENTAGE, IDDFS_FIXER_CLASS_PERCENTAGE, IDDFS_DEFECT_CLASS_PERCENTAGE, IDDFS_AJAX_FEATURE_PERCENTAGE
	}

	/** List of created mutants */
	List<MutationFileInformation> mutants;

	/** Contains mutants by keys to sort */
	Map<SortKey, List<MutationFileInformation>> map;

	/**
	 * Constructor
	 * 
	 * @param manager provides list of craeted mutants
	 */
	public Sorter(final MutationListManager manager) {
		mutants = manager.getMutationFileInformationList();
		map = createMap(mutants);
	}

	/**
	 * Create map to sort mutants
	 * 
	 * @param mutants is list of created mutants
	 * @return map to sort mutants
	 */
	private Map<SortKey, List<MutationFileInformation>> createMap(final List<MutationFileInformation> mutants) {
		Map<SortKey, List<MutationFileInformation>> map = Maps.newHashMap();
		for (MutationFileInformation mutant : mutants) {
			SortKey key = new SortKey(mutant.getWeight(), mutant.getMutatable(), mutant.getFixer(), mutant.getRepairSource());
			if (!map.containsKey(key)) {
				map.put(key, Lists.newArrayList());
			}
			map.get(key).add(mutant);
		}
		return map;
	}

	/**
	 * Get sort type by string
	 * 
	 * @param type represents sort type
	 * @return null if invalid sort type is given
	 */
	public static SortType getSortType(final String type) {
		for (SortType _type : SortType.values()) {
			if (_type.name().compareToIgnoreCase(type) == 0) {
				return _type;
			}
		}
		return SortType.REPAIR_SOURCE_DFS; // default
	}

	public List<MutationFileInformation> sort(SortType sortType) {
		switch (sortType) {
		case SIMPLE_LOCATION:
		case SIMPLE_AJAX_DEFECT:
		case SIMPLE_REPAIR_SOURCE:
			return sortBySimple(map, mutants, sortType);
		case RSREPAIR:
			return sortByRSRepair(map, mutants);
		case DFS:
			return sortByDFS(map, mutants);
		case REPAIR_SOURCE_DFS:
			return sortByRepairSourceDFS(map, mutants);
		case BFS_REPAIR_SOURCE:
		case BFS_FIXER_CLASS:
		case BFS_DEFECT_CLASS:
		case BFS_AJAX_FEATURE:
			return sortByBFS(map, mutants, sortType);
		case IDDFS_CANDIDATE_SOURCE:
		case IDDFS_FIXER_CLASS:
		case IDDFS_DEFECT_CLASS:
		case IDDFS_AJAX_FEATURE:
			return sortByIDDFS(map, mutants, sortType, 5);
		case IDDFS_CANDIDATE_SOURCE_PERCENTAGE:
		case IDDFS_FIXER_CLASS_PERCENTAGE:
		case IDDFS_DEFECT_CLASS_PERCENTAGE:
		case IDDFS_AJAX_FEATURE_PERCENTAGE:
			return sortByIDDFSwithPercentage(map, mutants, sortType, 5);
		case RANDOM:
			Collections.shuffle(mutants);
			return mutants;
		default:
			return mutants;
		}
	}

	private List<MutationFileInformation> sortBySimple(Map<SortKey, List<MutationFileInformation>> map, List<MutationFileInformation> list, SortType sortType) {
		List<MutationFileInformation> rtnList = Lists.newArrayList();
		Map<Integer, List<SortKey>> simpleSortKeyMap = Maps.newHashMap();

		// create new map for MapKeys for BFS
		switch (sortType) {
		case SIMPLE_LOCATION:
			for (SortKey key : map.keySet()) {
				if (!simpleSortKeyMap.containsKey(key.getSimpleStatementWeight())) {
					simpleSortKeyMap.put(key.getSimpleStatementWeight(), Lists.newArrayList());
				}
				simpleSortKeyMap.get(key.getSimpleStatementWeight()).add(key);
			}
			break;
		case SIMPLE_AJAX_DEFECT:
			for (SortKey key : map.keySet()) {
				if (!simpleSortKeyMap.containsKey(key.getSimpleAjaxAndDefectClassWeight())) {
					simpleSortKeyMap.put(key.getSimpleAjaxAndDefectClassWeight(), Lists.newArrayList());
				}
				simpleSortKeyMap.get(key.getSimpleAjaxAndDefectClassWeight()).add(key);
			}
			break;
		case SIMPLE_REPAIR_SOURCE:
			for (SortKey key : map.keySet()) {
				if (!simpleSortKeyMap.containsKey(key.getSimpleCandidateSourceWeight())) {
					simpleSortKeyMap.put(key.getSimpleCandidateSourceWeight(), Lists.newArrayList());
				}
				simpleSortKeyMap.get(key.getSimpleCandidateSourceWeight()).add(key);
			}
			break;
		default:
			return list;
		}

		// sort bfsSortKeyMap
		List<Integer> sortedSimpleSortKeyMap = Lists.newArrayList(simpleSortKeyMap.keySet());
		Collections.sort(sortedSimpleSortKeyMap);

		// add info to list
		for (Integer simpleSortKeyMapInteger : sortedSimpleSortKeyMap) {
			List<SortKey> tempSortKeyList = simpleSortKeyMap.get(simpleSortKeyMapInteger);
			List<MutationFileInformation> shuffleList = Lists.newArrayList();
			for (SortKey sortkey : tempSortKeyList) {
				shuffleList.addAll(map.get(sortkey));
			}
			Collections.shuffle(shuffleList);
			rtnList.addAll(shuffleList);
		}
		return rtnList;
	}

	/**
	 * Conceptual replication of RSRepair, i.e., sorting mutants randomly
	 * 
	 * @param map
	 * @param mutants
	 * @return
	 */
	private List<MutationFileInformation> sortByRSRepair(final Map<SortKey, List<MutationFileInformation>> map, final List<MutationFileInformation> mutants) {
		List<MutationFileInformation> ret = Lists.newArrayList();
		for (SortKey key : map.keySet()) {
			if (key.getSimpleStatementWeight() < 9) {
				ret.addAll(map.get(key));
			}
		}
		Collections.shuffle(ret);
		return ret;
	}

	private List<MutationFileInformation> sortByRepairSourceDFS(Map<SortKey, List<MutationFileInformation>> map, List<MutationFileInformation> list) {
		List<MutationFileInformation> rtnList = Lists.newArrayList();
		Map<Integer, List<SortKey>> dfsSortKeyMap = Maps.newHashMap();

		for (SortKey key : map.keySet()) {
			if (!dfsSortKeyMap.containsKey(key.getRepairSourcePrioritizedDfsWeight())) {
				dfsSortKeyMap.put(key.getRepairSourcePrioritizedDfsWeight(), Lists.newArrayList());
			}
			dfsSortKeyMap.get(key.getRepairSourcePrioritizedDfsWeight()).add(key);
		}

		// sort bfsSortKeyMap
		List<Integer> sortedDfsSortKeyMap = Lists.newArrayList(dfsSortKeyMap.keySet());
		Collections.sort(sortedDfsSortKeyMap);

		// add info to list
		for (Integer dfsSortKeyMapInteger : sortedDfsSortKeyMap) {
			List<SortKey> tempSortKeyList = dfsSortKeyMap.get(dfsSortKeyMapInteger);
			Collections.sort(tempSortKeyList);
			while (true) {
				int count = 0;
				for (int i = 0; i < tempSortKeyList.size(); i++) {
					List<MutationFileInformation> mutationFileInfoList = map.get(tempSortKeyList.get(i));
					if (mutationFileInfoList.size() > 0) {
						count++;
						rtnList.add(mutationFileInfoList.remove(0));
					}
				}
				if (count == 0) {
					break;
				}
			}
		}

		return rtnList;
	}

	private List<MutationFileInformation> sortByDFS(Map<SortKey, List<MutationFileInformation>> map, List<MutationFileInformation> list) {
		List<MutationFileInformation> rtnList = Lists.newArrayList();
		// sort MapKeys
		List<SortKey> sortedMapKeys = Lists.newArrayList(map.keySet());
		Collections.sort(sortedMapKeys);

		for (int i = 0; i < sortedMapKeys.size(); i++) {
			rtnList.addAll(map.get(sortedMapKeys.get(i)));
		}
		return rtnList;
	}

	private List<MutationFileInformation> sortByBFS(Map<SortKey, List<MutationFileInformation>> map, List<MutationFileInformation> list, SortType sortType) {
		List<MutationFileInformation> rtnList = Lists.newArrayList();
		Map<Integer, List<SortKey>> bfsSortKeyMap = Maps.newHashMap();

		// create new map for MapKeys for BFS
		switch (sortType) {
		case BFS_REPAIR_SOURCE:
			for (SortKey key : map.keySet()) {
				if (!bfsSortKeyMap.containsKey(key.getCandidateSourceBfsWeight())) {
					bfsSortKeyMap.put(key.getCandidateSourceBfsWeight(), Lists.newArrayList());
				}
				bfsSortKeyMap.get(key.getCandidateSourceBfsWeight()).add(key);
			}
			break;
		case BFS_FIXER_CLASS:
			for (SortKey key : map.keySet()) {
				if (!bfsSortKeyMap.containsKey(key.getFixerClassBfsWeight())) {
					bfsSortKeyMap.put(key.getFixerClassBfsWeight(), Lists.newArrayList());
				}
				bfsSortKeyMap.get(key.getFixerClassBfsWeight()).add(key);
			}
			break;
		case BFS_DEFECT_CLASS:
			for (SortKey key : map.keySet()) {
				if (!bfsSortKeyMap.containsKey(key.getDefectClassBfsWeight())) {
					bfsSortKeyMap.put(key.getDefectClassBfsWeight(), Lists.newArrayList());
				}
				bfsSortKeyMap.get(key.getDefectClassBfsWeight()).add(key);
			}
			break;
		case BFS_AJAX_FEATURE:
			for (SortKey key : map.keySet()) {
				if (!bfsSortKeyMap.containsKey(key.getAjaxFeatureBfsWeight())) {
					bfsSortKeyMap.put(key.getAjaxFeatureBfsWeight(), Lists.newArrayList());
				}
				bfsSortKeyMap.get(key.getAjaxFeatureBfsWeight()).add(key);
			}
			break;
		default:
			return list;
		}

		// sort bfsSortKeyMap
		List<Integer> sortedBfsSortKeyMap = Lists.newArrayList(bfsSortKeyMap.keySet());
		Collections.sort(sortedBfsSortKeyMap);

		// add info to list
		for (Integer bfsSortKeyMapInteger : sortedBfsSortKeyMap) {
			List<SortKey> tempSortKeyList = bfsSortKeyMap.get(bfsSortKeyMapInteger);
			Collections.sort(tempSortKeyList);
			while (true) {
				int count = 0;
				for (int i = 0; i < tempSortKeyList.size(); i++) {
					List<MutationFileInformation> mutationFileInfoList = map.get(tempSortKeyList.get(i));
					if (mutationFileInfoList.size() > 0) {
						count++;
						rtnList.add(mutationFileInfoList.remove(0));
					}
				}
				if (count == 0) {
					break;
				}
			}
		}
		return rtnList;
	}

	private List<MutationFileInformation> sortByIDDFS(Map<SortKey, List<MutationFileInformation>> map, List<MutationFileInformation> list, SortType sortType,
			int depth) {
		List<MutationFileInformation> rtnList = Lists.newArrayList();
		Map<Integer, List<SortKey>> iddfsSortKeyMap = Maps.newHashMap();

		// create new map for MapKeys for BFS
		switch (sortType) {
		case IDDFS_CANDIDATE_SOURCE:
			for (SortKey key : map.keySet()) {
				if (!iddfsSortKeyMap.containsKey(key.getCandidateSourceBfsWeight())) {
					iddfsSortKeyMap.put(key.getCandidateSourceBfsWeight(), Lists.newArrayList());
				}
				iddfsSortKeyMap.get(key.getCandidateSourceBfsWeight()).add(key);
			}
			break;
		case IDDFS_FIXER_CLASS:
			for (SortKey key : map.keySet()) {
				if (!iddfsSortKeyMap.containsKey(key.getFixerClassBfsWeight())) {
					iddfsSortKeyMap.put(key.getFixerClassBfsWeight(), Lists.newArrayList());
				}
				iddfsSortKeyMap.get(key.getFixerClassBfsWeight()).add(key);
			}
			break;
		case IDDFS_DEFECT_CLASS:
			for (SortKey key : map.keySet()) {
				if (!iddfsSortKeyMap.containsKey(key.getDefectClassBfsWeight())) {
					iddfsSortKeyMap.put(key.getDefectClassBfsWeight(), Lists.newArrayList());
				}
				iddfsSortKeyMap.get(key.getDefectClassBfsWeight()).add(key);
			}
			break;
		case IDDFS_AJAX_FEATURE:
			for (SortKey key : map.keySet()) {
				if (!iddfsSortKeyMap.containsKey(key.getAjaxFeatureBfsWeight())) {
					iddfsSortKeyMap.put(key.getAjaxFeatureBfsWeight(), Lists.newArrayList());
				}
				iddfsSortKeyMap.get(key.getAjaxFeatureBfsWeight()).add(key);
			}
			break;
		default:
			return list;
		}

		// sort iddfsSortKeyMap
		List<Integer> sortedBfsSortKeyMap = Lists.newArrayList(iddfsSortKeyMap.keySet());
		Collections.sort(sortedBfsSortKeyMap);

		// add info to list
		for (Integer bfsSortKeyMapInteger : sortedBfsSortKeyMap) {
			List<SortKey> tempSortKeyList = iddfsSortKeyMap.get(bfsSortKeyMapInteger);
			Collections.sort(tempSortKeyList);
			while (true) {
				int count = 0;
				for (int i = 0; i < tempSortKeyList.size(); i++) {
					List<MutationFileInformation> mutationFileInfoList = map.get(tempSortKeyList.get(i));
					if (mutationFileInfoList.size() > 0) {
						count++;
						for (int j = 0; j < depth && j < mutationFileInfoList.size(); j++) {
							rtnList.add(mutationFileInfoList.remove(0));
						}
					}
				}
				if (count == 0) {
					break;
				}
			}
		}

		return rtnList;
	}

	private List<MutationFileInformation> sortByIDDFSwithPercentage(Map<SortKey, List<MutationFileInformation>> map, List<MutationFileInformation> list,
			SortType sortType, int depth) {
		List<MutationFileInformation> rtnList = Lists.newArrayList();
		Map<Integer, List<SortKey>> iddfsSortKeyMap = Maps.newHashMap();

		// create new map for MapKeys for BFS
		switch (sortType) {
		case IDDFS_CANDIDATE_SOURCE_PERCENTAGE:
			for (SortKey key : map.keySet()) {
				if (!iddfsSortKeyMap.containsKey(key.getCandidateSourceBfsWeight())) {
					iddfsSortKeyMap.put(key.getCandidateSourceBfsWeight(), Lists.newArrayList());
				}
				iddfsSortKeyMap.get(key.getCandidateSourceBfsWeight()).add(key);
			}
			break;
		case IDDFS_FIXER_CLASS_PERCENTAGE:
			for (SortKey key : map.keySet()) {
				if (!iddfsSortKeyMap.containsKey(key.getFixerClassBfsWeight())) {
					iddfsSortKeyMap.put(key.getFixerClassBfsWeight(), Lists.newArrayList());
				}
				iddfsSortKeyMap.get(key.getFixerClassBfsWeight()).add(key);
			}
			break;
		case IDDFS_DEFECT_CLASS_PERCENTAGE:
			for (SortKey key : map.keySet()) {
				if (!iddfsSortKeyMap.containsKey(key.getDefectClassBfsWeight())) {
					iddfsSortKeyMap.put(key.getDefectClassBfsWeight(), Lists.newArrayList());
				}
				iddfsSortKeyMap.get(key.getDefectClassBfsWeight()).add(key);
			}
			break;
		case IDDFS_AJAX_FEATURE_PERCENTAGE:
			for (SortKey key : map.keySet()) {
				if (!iddfsSortKeyMap.containsKey(key.getAjaxFeatureBfsWeight())) {
					iddfsSortKeyMap.put(key.getAjaxFeatureBfsWeight(), Lists.newArrayList());
				}
				iddfsSortKeyMap.get(key.getAjaxFeatureBfsWeight()).add(key);
			}
			break;
		default:
			return list;
		}

		// sort iddfsSortKeyMap
		List<Integer> sortedBfsSortKeyMap = Lists.newArrayList(iddfsSortKeyMap.keySet());
		Collections.sort(sortedBfsSortKeyMap);

		// add info to list
		for (Integer bfsSortKeyMapInteger : sortedBfsSortKeyMap) {
			List<SortKey> tempSortKeyList = iddfsSortKeyMap.get(bfsSortKeyMapInteger);
			Collections.sort(tempSortKeyList);
			while (true) {
				int count = 0;
				for (int i = 0; i < tempSortKeyList.size(); i++) {
					List<MutationFileInformation> mutationFileInfoList = map.get(tempSortKeyList.get(i));
					int targetFileListSize = mutationFileInfoList.size();
					if (mutationFileInfoList.size() > 0) {
						count++;
						int depthWithPercentage = calculateDepthWithPercentage(this.mutants.size(), targetFileListSize, depth);
						for (int j = 0; j < depthWithPercentage && j < mutationFileInfoList.size(); j++) {
							rtnList.add(mutationFileInfoList.remove(0));
						}
					}
				}
				if (count == 0) {
					break;
				}
			}
		}
		return rtnList;
	}

	/**
	 * Calculate depth with percentage
	 * 
	 * @param numAllMutants
	 * @param numTargetMutants
	 * @param depth
	 * @return depth with percentage
	 */
	private static int calculateDepthWithPercentage(final int numAllMutants, final int numTargetMutants, final int depth) {
		if (numAllMutants == 0) {
			throw new IllegalArgumentException("Create mutants, at least one, before sorting");
		}
		int depthWithPercentage;
		double percentage = ((double) numTargetMutants) / ((double) numAllMutants);
		depthWithPercentage = (int) (((double) depth) * percentage);
		if (depthWithPercentage == 0) {
			depthWithPercentage = 1;
		}
		return depthWithPercentage;
	}

}