package jp.mzw.revajaxmutator.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.mzw.ajaxmutator.generator.MutationFileInformation;
import jp.mzw.ajaxmutator.generator.MutationListManager;

public class Sorter {

	List<MutationFileInformation> list;
	Map<SortKey, List<MutationFileInformation>> map;

	public Sorter(MutationListManager manager) {
		list = getMutationFileInformationList(manager);
		map = createMap(list);
	}

	private List<MutationFileInformation> getMutationFileInformationList(
			MutationListManager manager) {
		List<MutationFileInformation> list = new ArrayList<MutationFileInformation>();
		for (String name : manager.getListOfMutationName()) {
			for (MutationFileInformation info : manager
					.getMutationFileInformationList(name)) {
				list.add(info);
			}
		}
		return list;
	}

	private Map<SortKey, List<MutationFileInformation>> createMap(
			List<MutationFileInformation> list) {
		Map<SortKey, List<MutationFileInformation>> map = new HashMap<SortKey, List<MutationFileInformation>>();
		for (MutationFileInformation info : list) {
			SortKey sortKey = new SortKey(info.getWeight(),
					info.getMutatable(), info.getFixer(),
					info.getRepairSource());
			if (!map.containsKey(sortKey)) {
				map.put(sortKey, new ArrayList<MutationFileInformation>());
			}
			map.get(sortKey).add(info);
		}
		return map;
	}

	public enum SortType {
		SIMPLE_LOCATION, SIMPLE_AJAX_DEFECT, SIMPLE_REPAIR_SOURCE, RSREPAIR, DFS, REPAIR_SOURCE_DFS, BFS_REPAIR_SOURCE, BFS_FIXER_CLASS, BFS_DEFECT_CLASS, BFS_AJAX_FEATURE, RANDOM, IDDFS_CANDIDATE_SOURCE, IDDFS_FIXER_CLASS, IDDFS_DEFECT_CLASS, IDDFS_AJAX_FEATURE, IDDFS_CANDIDATE_SOURCE_PERCENTAGE, IDDFS_FIXER_CLASS_PERCENTAGE, IDDFS_DEFECT_CLASS_PERCENTAGE, IDDFS_AJAX_FEATURE_PERCENTAGE
	}
	
	public static SortType getSortType(String type) {
		for(SortType _type : SortType.values()) {
			if(_type.name().equals(type)) {
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
			return sortBySimple(map, list, sortType);
		case RSREPAIR:
			return sortByRSRepair(map, list);
		case DFS:
			return sortByDFS(map, list);
		case REPAIR_SOURCE_DFS:
			return sortByRepairSourceDFS(map, list);
		case BFS_REPAIR_SOURCE:
		case BFS_FIXER_CLASS:
		case BFS_DEFECT_CLASS:
		case BFS_AJAX_FEATURE:
			return sortByBFS(map, list, sortType);
		case IDDFS_CANDIDATE_SOURCE:
		case IDDFS_FIXER_CLASS:
		case IDDFS_DEFECT_CLASS:
		case IDDFS_AJAX_FEATURE:
			return sortByIDDFS(map, list, sortType, 5);
		case IDDFS_CANDIDATE_SOURCE_PERCENTAGE:
		case IDDFS_FIXER_CLASS_PERCENTAGE:
		case IDDFS_DEFECT_CLASS_PERCENTAGE:
		case IDDFS_AJAX_FEATURE_PERCENTAGE:
			return sortByIDDFSwithPercentage(map, list, sortType, 5);
		case RANDOM:
			Collections.shuffle(list);
			return list;
		default:
			return list;
		}
	}

	private List<MutationFileInformation> sortBySimple(
			Map<SortKey, List<MutationFileInformation>> map,
			List<MutationFileInformation> list, SortType sortType) {
		List<MutationFileInformation> rtnList = new ArrayList<MutationFileInformation>();
		Map<Integer, List<SortKey>> simpleSortKeyMap = new HashMap<Integer, List<SortKey>>();

		// create new map for MapKeys for BFS
		switch (sortType) {
		case SIMPLE_LOCATION:
			for (SortKey key : map.keySet()) {
				if (!simpleSortKeyMap.containsKey(key
						.getSimpleStatementWeight())) {
					simpleSortKeyMap.put(key.getSimpleStatementWeight(),
							new ArrayList<SortKey>());
				}
				simpleSortKeyMap.get(key.getSimpleStatementWeight()).add(key);
			}
			break;
		case SIMPLE_AJAX_DEFECT:
			for (SortKey key : map.keySet()) {
				if (!simpleSortKeyMap.containsKey(key
						.getSimpleAjaxAndDefectClassWeight())) {
					simpleSortKeyMap.put(
							key.getSimpleAjaxAndDefectClassWeight(),
							new ArrayList<SortKey>());
				}
				simpleSortKeyMap.get(key.getSimpleAjaxAndDefectClassWeight())
						.add(key);
			}
			break;
		case SIMPLE_REPAIR_SOURCE:
			for (SortKey key : map.keySet()) {
				if (!simpleSortKeyMap.containsKey(key
						.getSimpleCandidateSourceWeight())) {
					simpleSortKeyMap.put(key.getSimpleCandidateSourceWeight(),
							new ArrayList<SortKey>());
				}
				simpleSortKeyMap.get(key.getSimpleCandidateSourceWeight()).add(
						key);
			}
			break;
		default:
			return list;
		}

		// sort bfsSortKeyMap
		List<Integer> sortedSimpleSortKeyMap = new ArrayList<Integer>(
				simpleSortKeyMap.keySet());
		Collections.sort(sortedSimpleSortKeyMap);

		// add info to list
		for (Integer simpleSortKeyMapInteger : sortedSimpleSortKeyMap) {
			List<SortKey> tempSortKeyList = simpleSortKeyMap
					.get(simpleSortKeyMapInteger);
			List<MutationFileInformation> shuffleList = new ArrayList<MutationFileInformation>();
			for (SortKey sortkey : tempSortKeyList) {
				shuffleList.addAll(map.get(sortkey));
			}
			Collections.shuffle(shuffleList);
			rtnList.addAll(shuffleList);
		}
		return rtnList;
	}

	private List<MutationFileInformation> sortByRSRepair(
			Map<SortKey, List<MutationFileInformation>> map,
			List<MutationFileInformation> list) {
		List<MutationFileInformation> rtnList = new ArrayList<MutationFileInformation>();
		for (SortKey sortKey : map.keySet()) {
			if (sortKey.getSimpleStatementWeight() < 9) {
				rtnList.addAll(map.get(sortKey));
			}
		}
		Collections.shuffle(rtnList);
		return rtnList;
	}

	private List<MutationFileInformation> sortByRepairSourceDFS(
			Map<SortKey, List<MutationFileInformation>> map,
			List<MutationFileInformation> list) {
		List<MutationFileInformation> rtnList = new ArrayList<MutationFileInformation>();
		Map<Integer, List<SortKey>> dfsSortKeyMap = new HashMap<Integer, List<SortKey>>();

		for (SortKey key : map.keySet()) {
			if (!dfsSortKeyMap.containsKey(key
					.getRepairSourcePrioritizedDfsWeight())) {
				dfsSortKeyMap.put(key.getRepairSourcePrioritizedDfsWeight(),
						new ArrayList<SortKey>());
			}
			dfsSortKeyMap.get(key.getRepairSourcePrioritizedDfsWeight()).add(
					key);
		}

		// sort bfsSortKeyMap
		List<Integer> sortedDfsSortKeyMap = new ArrayList<Integer>(
				dfsSortKeyMap.keySet());
		Collections.sort(sortedDfsSortKeyMap);

		// add info to list
		for (Integer dfsSortKeyMapInteger : sortedDfsSortKeyMap) {
			List<SortKey> tempSortKeyList = dfsSortKeyMap
					.get(dfsSortKeyMapInteger);
			Collections.sort(tempSortKeyList);
			while (true) {
				int count = 0;
				for (int i = 0; i < tempSortKeyList.size(); i++) {
					List<MutationFileInformation> mutationFileInfoList = map
							.get(tempSortKeyList.get(i));
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

	private List<MutationFileInformation> sortByDFS(
			Map<SortKey, List<MutationFileInformation>> map,
			List<MutationFileInformation> list) {
		List<MutationFileInformation> rtnList = new ArrayList<MutationFileInformation>();
		// sort MapKeys
		List<SortKey> sortedMapKeys = new ArrayList<SortKey>(map.keySet());
		Collections.sort(sortedMapKeys);

		for (int i = 0; i < sortedMapKeys.size(); i++) {
			rtnList.addAll(map.get(sortedMapKeys.get(i)));
		}
		return rtnList;
	}

	private List<MutationFileInformation> sortByBFS(
			Map<SortKey, List<MutationFileInformation>> map,
			List<MutationFileInformation> list, SortType sortType) {
		List<MutationFileInformation> rtnList = new ArrayList<MutationFileInformation>();
		Map<Integer, List<SortKey>> bfsSortKeyMap = new HashMap<Integer, List<SortKey>>();

		// create new map for MapKeys for BFS
		switch (sortType) {
		case BFS_REPAIR_SOURCE:
			for (SortKey key : map.keySet()) {
				if (!bfsSortKeyMap.containsKey(key
						.getCandidateSourceBfsWeight())) {
					bfsSortKeyMap.put(key.getCandidateSourceBfsWeight(),
							new ArrayList<SortKey>());
				}
				bfsSortKeyMap.get(key.getCandidateSourceBfsWeight()).add(key);
			}
			break;
		case BFS_FIXER_CLASS:
			for (SortKey key : map.keySet()) {
				if (!bfsSortKeyMap.containsKey(key.getFixerClassBfsWeight())) {
					bfsSortKeyMap.put(key.getFixerClassBfsWeight(),
							new ArrayList<SortKey>());
				}
				bfsSortKeyMap.get(key.getFixerClassBfsWeight()).add(key);
			}
			break;
		case BFS_DEFECT_CLASS:
			for (SortKey key : map.keySet()) {
				if (!bfsSortKeyMap.containsKey(key.getDefectClassBfsWeight())) {
					bfsSortKeyMap.put(key.getDefectClassBfsWeight(),
							new ArrayList<SortKey>());
				}
				bfsSortKeyMap.get(key.getDefectClassBfsWeight()).add(key);
			}
			break;
		case BFS_AJAX_FEATURE:
			for (SortKey key : map.keySet()) {
				if (!bfsSortKeyMap.containsKey(key.getAjaxFeatureBfsWeight())) {
					bfsSortKeyMap.put(key.getAjaxFeatureBfsWeight(),
							new ArrayList<SortKey>());
				}
				bfsSortKeyMap.get(key.getAjaxFeatureBfsWeight()).add(key);
			}
			break;
		default:
			return list;
		}

		// sort bfsSortKeyMap
		List<Integer> sortedBfsSortKeyMap = new ArrayList<Integer>(
				bfsSortKeyMap.keySet());
		Collections.sort(sortedBfsSortKeyMap);

		// add info to list
		for (Integer bfsSortKeyMapInteger : sortedBfsSortKeyMap) {
			List<SortKey> tempSortKeyList = bfsSortKeyMap
					.get(bfsSortKeyMapInteger);
			Collections.sort(tempSortKeyList);
			while (true) {
				int count = 0;
				for (int i = 0; i < tempSortKeyList.size(); i++) {
					List<MutationFileInformation> mutationFileInfoList = map
							.get(tempSortKeyList.get(i));
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

	private List<MutationFileInformation> sortByIDDFS(
			Map<SortKey, List<MutationFileInformation>> map,
			List<MutationFileInformation> list, SortType sortType, int depth) {
		List<MutationFileInformation> rtnList = new ArrayList<MutationFileInformation>();
		Map<Integer, List<SortKey>> iddfsSortKeyMap = new HashMap<Integer, List<SortKey>>();

		// create new map for MapKeys for BFS
		switch (sortType) {
		case IDDFS_CANDIDATE_SOURCE:
			for (SortKey key : map.keySet()) {
				if (!iddfsSortKeyMap.containsKey(key
						.getCandidateSourceBfsWeight())) {
					iddfsSortKeyMap.put(key.getCandidateSourceBfsWeight(),
							new ArrayList<SortKey>());
				}
				iddfsSortKeyMap.get(key.getCandidateSourceBfsWeight()).add(key);
			}
			break;
		case IDDFS_FIXER_CLASS:
			for (SortKey key : map.keySet()) {
				if (!iddfsSortKeyMap.containsKey(key.getFixerClassBfsWeight())) {
					iddfsSortKeyMap.put(key.getFixerClassBfsWeight(),
							new ArrayList<SortKey>());
				}
				iddfsSortKeyMap.get(key.getFixerClassBfsWeight()).add(key);
			}
			break;
		case IDDFS_DEFECT_CLASS:
			for (SortKey key : map.keySet()) {
				if (!iddfsSortKeyMap.containsKey(key.getDefectClassBfsWeight())) {
					iddfsSortKeyMap.put(key.getDefectClassBfsWeight(),
							new ArrayList<SortKey>());
				}
				iddfsSortKeyMap.get(key.getDefectClassBfsWeight()).add(key);
			}
			break;
		case IDDFS_AJAX_FEATURE:
			for (SortKey key : map.keySet()) {
				if (!iddfsSortKeyMap.containsKey(key.getAjaxFeatureBfsWeight())) {
					iddfsSortKeyMap.put(key.getAjaxFeatureBfsWeight(),
							new ArrayList<SortKey>());
				}
				iddfsSortKeyMap.get(key.getAjaxFeatureBfsWeight()).add(key);
			}
			break;
		default:
			return list;
		}

		// sort iddfsSortKeyMap
		List<Integer> sortedBfsSortKeyMap = new ArrayList<Integer>(
				iddfsSortKeyMap.keySet());
		Collections.sort(sortedBfsSortKeyMap);

		// add info to list
		for (Integer bfsSortKeyMapInteger : sortedBfsSortKeyMap) {
			List<SortKey> tempSortKeyList = iddfsSortKeyMap
					.get(bfsSortKeyMapInteger);
			Collections.sort(tempSortKeyList);
			while (true) {
				int count = 0;
				for (int i = 0; i < tempSortKeyList.size(); i++) {
					List<MutationFileInformation> mutationFileInfoList = map
							.get(tempSortKeyList.get(i));
					if (mutationFileInfoList.size() > 0) {
						count++;
						for (int j = 0; j < depth
								&& j < mutationFileInfoList.size(); j++) {
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

	private List<MutationFileInformation> sortByIDDFSwithPercentage(
			Map<SortKey, List<MutationFileInformation>> map,
			List<MutationFileInformation> list, SortType sortType, int depth) {
		List<MutationFileInformation> rtnList = new ArrayList<MutationFileInformation>();
		Map<Integer, List<SortKey>> iddfsSortKeyMap = new HashMap<Integer, List<SortKey>>();

		// create new map for MapKeys for BFS
		switch (sortType) {
		case IDDFS_CANDIDATE_SOURCE_PERCENTAGE:
			for (SortKey key : map.keySet()) {
				if (!iddfsSortKeyMap.containsKey(key
						.getCandidateSourceBfsWeight())) {
					iddfsSortKeyMap.put(key.getCandidateSourceBfsWeight(),
							new ArrayList<SortKey>());
				}
				iddfsSortKeyMap.get(key.getCandidateSourceBfsWeight()).add(key);
			}
			break;
		case IDDFS_FIXER_CLASS_PERCENTAGE:
			for (SortKey key : map.keySet()) {
				if (!iddfsSortKeyMap.containsKey(key.getFixerClassBfsWeight())) {
					iddfsSortKeyMap.put(key.getFixerClassBfsWeight(),
							new ArrayList<SortKey>());
				}
				iddfsSortKeyMap.get(key.getFixerClassBfsWeight()).add(key);
			}
			break;
		case IDDFS_DEFECT_CLASS_PERCENTAGE:
			for (SortKey key : map.keySet()) {
				if (!iddfsSortKeyMap.containsKey(key.getDefectClassBfsWeight())) {
					iddfsSortKeyMap.put(key.getDefectClassBfsWeight(),
							new ArrayList<SortKey>());
				}
				iddfsSortKeyMap.get(key.getDefectClassBfsWeight()).add(key);
			}
			break;
		case IDDFS_AJAX_FEATURE_PERCENTAGE:
			for (SortKey key : map.keySet()) {
				if (!iddfsSortKeyMap.containsKey(key.getAjaxFeatureBfsWeight())) {
					iddfsSortKeyMap.put(key.getAjaxFeatureBfsWeight(),
							new ArrayList<SortKey>());
				}
				iddfsSortKeyMap.get(key.getAjaxFeatureBfsWeight()).add(key);
			}
			break;
		default:
			return list;
		}

		// sort iddfsSortKeyMap
		List<Integer> sortedBfsSortKeyMap = new ArrayList<Integer>(
				iddfsSortKeyMap.keySet());
		Collections.sort(sortedBfsSortKeyMap);

		// add info to list
		for (Integer bfsSortKeyMapInteger : sortedBfsSortKeyMap) {
			List<SortKey> tempSortKeyList = iddfsSortKeyMap
					.get(bfsSortKeyMapInteger);
			Collections.sort(tempSortKeyList);
			while (true) {
				int count = 0;
				for (int i = 0; i < tempSortKeyList.size(); i++) {
					List<MutationFileInformation> mutationFileInfoList = map
							.get(tempSortKeyList.get(i));
					int targetFileListSize = mutationFileInfoList.size();
					if (mutationFileInfoList.size() > 0) {
						count++;
						int depthWithPercentage = calculateDepthWithPercentage(
								this.list.size(), targetFileListSize, depth);
						for (int j = 0; j < depthWithPercentage
								&& j < mutationFileInfoList.size(); j++) {
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

	private int calculateDepthWithPercentage(int allMutationFileInfosSize,
			int targetListSize, int depth) {
		int depthWithPercentage;
		double percentage = ((double) targetListSize)
				/ ((double) allMutationFileInfosSize);
		depthWithPercentage = (int) (((double) depth) * percentage);
		if (depthWithPercentage == 0) {
			depthWithPercentage = 1;
		}
		return depthWithPercentage;
	}

}