package jp.mzw.revajaxmutator.search;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import jp.mzw.ajaxmutator.generator.MutationFileInformation;
import jp.mzw.ajaxmutator.generator.MutationListManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.collect.ImmutableSet;

public class Sorter {

//
//	private String reportOutputDir = "";
//	private static final String DEFAULT_FOLDER_NAME = "mutants";
//	private static final double Wpath = 0.5;
//	private static final int CANDIDATE_NUM = 24;
//	private static final int AJAXFEATURE_NUM = 6;
//	private static final int EVENTCLASS_NUM = 2;
//	private static final int DOMCLASS_NUM = 6;
//	private final Set<Integer> answers;
//	private int allMutationFilesSize=0;
//	private String failureJSON = "none";
//	private String successJSON;
//	private String urlPathToJS;
//
//	public enum SortType {
//		SIMPLE_LOCATION, SIMPLE_AJAX_DEFECT, SIMPLE_CANDIDATE_SOURCE,
//		RSREPAIR,
//
//		DFS, CANDIDATE_SOURCE_DFS,
//		BFS_CANDIDATE_SOURCE, BFS_FIXER_CLASS, BFS_DEFECT_CLASS, BFS_AJAX_FEATURE, RANDOM,
//		IDDFS_CANDIDATE_SOURCE,IDDFS_FIXER_CLASS, IDDFS_DEFECT_CLASS, IDDFS_AJAX_FEATURE,
//		IDDFS_CANDIDATE_SOURCE_PERCENTAGE, IDDFS_FIXER_CLASS_PERCENTAGE, IDDFS_DEFECT_CLASS_PERCENTAGE, IDDFS_AJAX_FEATURE_PERCENTAGE
//	}
//
//	public Sorter(File file, Set<Integer> answers) {
//		this.reportOutputDir = file.getParent() + File.separator
//				+ DEFAULT_FOLDER_NAME;
//		this.answers = answers;
//	}
//
//	public Sorter(File file, Set<Integer> answers, String failureJsonFile, String urlPathToJs) {
//		this.reportOutputDir = file.getParent() + File.separator
//				+ DEFAULT_FOLDER_NAME;
//		this.answers = answers;
//		this.failureJSON = failureJsonFile;
//		this.urlPathToJS = urlPathToJs;
//	}
//
//	//String failureJSON, String successJSON, String JSFileNameInJson
//
//	public void sort(SortType sortType) throws JSONException, IOException {
//		// MutationListManager
//		MutationListManager mutationListManager = new MutationListManager(
//				reportOutputDir);
//
//		// read file
//		List<MutationFileInformation> mutationFileInformations = setWeight(mutationListManager
//				.readExistingMutationListFilesForSort());
//
//		this.allMutationFilesSize = mutationFileInformations.size();
//
//		// shuffle list
//		Collections.shuffle(mutationFileInformations);
//
//		// create maps
//		Map<SortKey, List<MutationFileInformation>> sortMap = new HashMap<SortKey, List<MutationFileInformation>>();
//		createMap(sortMap, mutationFileInformations);
//
//		// sort mutationFileInformations
//		List<MutationFileInformation> sortedList = sortMutationFileInfos(
//				sortMap, mutationFileInformations, sortType);
//
//		// write file
//		mutationListManager.generateSortedMutationListFile(sortedList);
//	}
//
//	public void repeatedTrials(int iterationNumber) throws JSONException, IOException {
//		// MutationListManager
//		MutationListManager mutationListManager = new MutationListManager(
//				reportOutputDir);
//
//		// read file and set weight
//		List<MutationFileInformation> mutationFileInformations = setWeight(mutationListManager
//				.readExistingMutationListFilesForSort());
//
//		this.allMutationFilesSize = mutationFileInformations.size();
//
//		for (SortType sortType : ImmutableSet.of(SortType.CANDIDATE_SOURCE_DFS,SortType.SIMPLE_LOCATION, SortType.SIMPLE_AJAX_DEFECT, SortType.SIMPLE_CANDIDATE_SOURCE, SortType.RSREPAIR, SortType.RANDOM)) {
//			List<Integer> answerPosList = new ArrayList<Integer>();
//			for (int i = 0; i < iterationNumber; i++) {
//				// shuffle list
//				Collections.shuffle(mutationFileInformations);
//
//				// create maps
//				Map<SortKey, List<MutationFileInformation>> sortMap = new HashMap<SortKey, List<MutationFileInformation>>();
//				createMap(sortMap, mutationFileInformations);
//
//				// sort mutationFileInformations
//				List<MutationFileInformation> sortedList = sortMutationFileInfos(
//						sortMap, mutationFileInformations, sortType);
//				for (int j = 0; j < sortedList.size(); j++) {
//					if (answers.contains(getFileNumber(sortedList.get(j)))) {
//						answerPosList.add(j);
//						break;
//					}
//				}
//			}
//			int sum = 0;
//			for (Integer answerPos : answerPosList) {
//				// System.out.println(answerPos);
//				sum += answerPos;
//			}
//			Collections.sort(answerPosList);
//			int median = answerPosList.get(answerPosList.size()/2);
//			System.out.println(sortType.name() + "'s MEAN:   " + sum
//					/ answerPosList.size() + ",   " +  sortType.name() + "'s MEDIAN:   " + median);
//		}
//	}
//
//	public void proposalPermutationTrial() throws JSONException, IOException{
//		// MutationListManager
//		MutationListManager mutationListManager = new MutationListManager(
//				reportOutputDir);
//
//		// read file and set weight
//		List<MutationFileInformation> mutationFileInformations = setWeight(mutationListManager
//				.readExistingMutationListFilesForSort());
//
//		this.allMutationFilesSize = mutationFileInformations.size();//size
//		Map<String,Double> resultMap = new TreeMap<String, Double>();
//
//		for(int num4Candidate = 0;num4Candidate < CANDIDATE_NUM;num4Candidate++){
//			for(int num4AjaxFeature =0;num4AjaxFeature < AJAXFEATURE_NUM;num4AjaxFeature++){
//				for(int num4Event = 0;num4Event < EVENTCLASS_NUM;num4Event++){
//					for(int num4DOM = 0;num4DOM < DOMCLASS_NUM;num4DOM++){
//						String resultMapKey = num4Candidate + " / " + num4AjaxFeature + " / " + num4Event + " / " + num4DOM;
//
//						//		for (SortType sortType : ImmutableSet.of(SortType.CANDIDATE_SOURCE_DFS,SortType.SIMPLE_LOCATION, SortType.SIMPLE_AJAX_DEFECT, SortType.SIMPLE_CANDIDATE_SOURCE, SortType.RSREPAIR, 
//						List<Integer> answerPosList = new ArrayList<Integer>();
//						for (int i = 0; i < 5; i++) {
//							// shuffle list
//							Collections.shuffle(mutationFileInformations);
//
//							// create maps
//							Map<SortKey, List<MutationFileInformation>> sortMap = new HashMap<SortKey, List<MutationFileInformation>>();
//							createMap(sortMap, mutationFileInformations);
//
//							// sort mutationFileInformations
//							List<MutationFileInformation> sortedList = sortByCandidateSourceDFSwithAllPermutations(sortMap, mutationFileInformations, num4Candidate, num4AjaxFeature, num4Event, num4DOM); 
//
//							//						sortMutationFileInfos(
//							//						sortMap, mutationFileInformations, SortType.CANDIDATE_SOURCE_DFS);
//							for (int j = 0; j < sortedList.size(); j++) {
//								if (answers.contains(getFileNumber(sortedList.get(j)))) {
//									answerPosList.add(j);
//									break;
//								}
//							}
//						}
//						double sum = 0;
//						for (Integer answerPos : answerPosList) {
//							// System.out.println(answerPos);
//							sum += (double)answerPos / (double)this.allMutationFilesSize;
//						}
//						double mean = sum / answerPosList.size();
//						System.out.println(resultMapKey + ":   " + mean);
//						resultMap.put(resultMapKey, mean);
//					}
//				}
//			}
//		}
//		
//		mutationListManager.generateProposalPermutationResultFile(resultMap);
//	}
//
//	public void candidatePermutationTrial() throws JSONException, IOException{
//		// MutationListManager
//		MutationListManager mutationListManager = new MutationListManager(
//				reportOutputDir);
//
//		// read file and set weight
//		List<MutationFileInformation> mutationFileInformations = setWeight(mutationListManager
//				.readExistingMutationListFilesForSort());
//
//		this.allMutationFilesSize = mutationFileInformations.size();//size
//		Map<String,Double> resultMap = new TreeMap<String, Double>();
//
//		for(int num4Candidate = 0;num4Candidate < CANDIDATE_NUM;num4Candidate++){
////			for(int num4AjaxFeature =0;num4AjaxFeature < AJAXFEATURE_NUM;num4AjaxFeature++){
////				for(int num4Event = 0;num4Event < EVENTCLASS_NUM;num4Event++){
////					for(int num4DOM = 0;num4DOM < DOMCLASS_NUM;num4DOM++){
//						String resultMapKey = "" + num4Candidate;
//
//						//		for (SortType sortType : ImmutableSet.of(SortType.CANDIDATE_SOURCE_DFS,SortType.SIMPLE_LOCATION, SortType.SIMPLE_AJAX_DEFECT, SortType.SIMPLE_CANDIDATE_SOURCE, SortType.RSREPAIR, 
//						List<Integer> answerPosList = new ArrayList<Integer>();
//						for (int i = 0; i < 100; i++) {
//							// shuffle list
//							Collections.shuffle(mutationFileInformations);
//
//							// create maps
//							Map<SortKey, List<MutationFileInformation>> sortMap = new HashMap<SortKey, List<MutationFileInformation>>();
//							createMap(sortMap, mutationFileInformations);
//
//							// sort mutationFileInformations
//							List<MutationFileInformation> sortedList = sortBySimpleWithPermutation(sortMap, mutationFileInformations, SortType.SIMPLE_CANDIDATE_SOURCE, num4Candidate, 0, 0, 0); 
//							for (int j = 0; j < sortedList.size(); j++) {
//								if (answers.contains(getFileNumber(sortedList.get(j)))) {
//									answerPosList.add(j);
//									break;
//								}
//							}
//						}
//						double sum = 0;
//						for (Integer answerPos : answerPosList) {
//							// System.out.println(answerPos);
//							sum += (double)answerPos / (double)this.allMutationFilesSize;
//						}
//						double mean = sum / answerPosList.size();
//						System.out.println(resultMapKey + ":   " + mean);
//						resultMap.put(resultMapKey, mean);
////					}
////				}
////			}
//		}
//		mutationListManager.generateCandidatePermutationResultFile(resultMap);
//	}
//	
//	public void defectclassPermutationTrial() throws JSONException, IOException{
//		// MutationListManager
//		MutationListManager mutationListManager = new MutationListManager(
//				reportOutputDir);
//
//		// read file and set weight
//		List<MutationFileInformation> mutationFileInformations = setWeight(mutationListManager
//				.readExistingMutationListFilesForSort());
//
//		this.allMutationFilesSize = mutationFileInformations.size();//size
//		Map<String,Double> resultMap = new TreeMap<String, Double>();
//
////		for(int num4Candidate = 0;num4Candidate < CANDIDATE_NUM;num4Candidate++){
//			for(int num4AjaxFeature =0;num4AjaxFeature < AJAXFEATURE_NUM;num4AjaxFeature++){
//				for(int num4Event = 0;num4Event < EVENTCLASS_NUM;num4Event++){
//					for(int num4DOM = 0;num4DOM < DOMCLASS_NUM;num4DOM++){
//						String resultMapKey = num4AjaxFeature + " / " + num4Event + " / " + num4DOM;
//
//						//		for (SortType sortType : ImmutableSet.of(SortType.CANDIDATE_SOURCE_DFS,SortType.SIMPLE_LOCATION, SortType.SIMPLE_AJAX_DEFECT, SortType.SIMPLE_CANDIDATE_SOURCE, SortType.RSREPAIR, 
//						List<Integer> answerPosList = new ArrayList<Integer>();
//						for (int i = 0; i < 10; i++) {
//							// shuffle list
//							Collections.shuffle(mutationFileInformations);
//
//							// create maps
//							Map<SortKey, List<MutationFileInformation>> sortMap = new HashMap<SortKey, List<MutationFileInformation>>();
//							createMap(sortMap, mutationFileInformations);
//
//							// sort mutationFileInformations
//							List<MutationFileInformation> sortedList = sortBySimpleWithPermutation(sortMap, mutationFileInformations, SortType.SIMPLE_AJAX_DEFECT, 0, num4AjaxFeature, num4Event, num4DOM); 
//							for (int j = 0; j < sortedList.size(); j++) {
//								if (answers.contains(getFileNumber(sortedList.get(j)))) {
//									answerPosList.add(j);
//									break;
//								}
//							}
//						}
//						double sum = 0;
//						for (Integer answerPos : answerPosList) {
//							// System.out.println(answerPos);
//							sum += (double)answerPos / (double)this.allMutationFilesSize;
//						}
//						double mean = sum / answerPosList.size();
//						System.out.println(resultMapKey + ":   " + mean);
//						resultMap.put(resultMapKey, mean);
//					}
//				}
//			}
////		}
//		
//		mutationListManager.generateDefectclassPermutationResultFile(resultMap);
//	}
//
//
//
//
//
//
//
//
//
//
//
//	public void printGnuplot(int iterationNumber) throws JSONException, IOException{
//		// MutationListManager
//		MutationListManager mutationListManager = new MutationListManager(
//				reportOutputDir);
//
//		// read file
//		List<MutationFileInformation> mutationFileInformations = setWeight(mutationListManager
//				.readExistingMutationListFilesForSort());
//
//		this.allMutationFilesSize = mutationFileInformations.size();
//		List<List<Integer>> outputList = new ArrayList<List<Integer>>();
//		for (SortType sortType : ImmutableSet.of(SortType.CANDIDATE_SOURCE_DFS, SortType.RANDOM)) {
//			List<Integer> answerPosList = new ArrayList<Integer>();
//			for (int i = 0; i < iterationNumber; i++) {
//				// shuffle list
//				Collections.shuffle(mutationFileInformations);
//
//				// create maps
//				Map<SortKey, List<MutationFileInformation>> sortMap = new HashMap<SortKey, List<MutationFileInformation>>();
//				createMap(sortMap, mutationFileInformations);
//
//				// sort mutationFileInformations
//				List<MutationFileInformation> sortedList = sortMutationFileInfos(
//						sortMap, mutationFileInformations, sortType);
//				for (int j = 0; j < sortedList.size(); j++) {
//					if (answers.contains(getFileNumber(sortedList.get(j)))) {
//						answerPosList.add(j);
//						break;
//					}
//				}
//			}
//			int sum = 0;
//			for (Integer answerPos : answerPosList) {
//				// System.out.println(answerPos);
//				sum += answerPos;
//			}
//			Collections.sort(answerPosList);
//			int median = answerPosList.get(answerPosList.size()/2);
//			System.out.println(sortType.name() + "'s MEAN:   " + sum
//					/ answerPosList.size() + ",   " +  sortType.name() + "'s MEDIAN:   " + median);
//
//			outputList.add(answerPosList);
//		}
//		System.out.println(mutationListManager.generateGnuplotFile(outputList.get(0),outputList.get(1)));
//
//
//	}
//
//	private int getFileNumber(MutationFileInformation fileInfo) {
//		String fileNumStr = fileInfo.getFileName().substring("mutant".length(),
//				fileInfo.getFileName().length() - 5);
//		int fileNum = Integer.parseInt(fileNumStr);
//		return fileNum;
//	}
//
//	private void createMap(Map<SortKey, List<MutationFileInformation>> map,
//			List<MutationFileInformation> list) {
//		for (MutationFileInformation mutationFileInfo : list) {
//			SortKey sortKey = new SortKey(mutationFileInfo.getWeight(),
//					mutationFileInfo.getMutatable(),
//					mutationFileInfo.getFixer(),
//					mutationFileInfo.getRepairSource());
//			if (!map.containsKey(sortKey)) {
//				map.put(sortKey, new ArrayList<MutationFileInformation>());
//			}
//			map.get(sortKey).add(mutationFileInfo);
//		}
//	}
//
//	private List<MutationFileInformation> sortMutationFileInfos(
//			Map<SortKey, List<MutationFileInformation>> map,
//			List<MutationFileInformation> list, SortType sortType) {
//		switch (sortType) {
//		case SIMPLE_LOCATION:
//		case SIMPLE_AJAX_DEFECT:
//		case SIMPLE_CANDIDATE_SOURCE:
//			return sortBySimple(map, list, sortType);
//		case RSREPAIR:
//			return sortByRSRepair(map, list);
//		case DFS:
//			return sortByDFS(map, list);
//		case CANDIDATE_SOURCE_DFS:
//			return sortByCandidateSourceDFS(map, list);
//		case BFS_CANDIDATE_SOURCE:
//		case BFS_FIXER_CLASS:
//		case BFS_DEFECT_CLASS:
//		case BFS_AJAX_FEATURE:
//			return sortByBFS(map, list, sortType);
//		case IDDFS_CANDIDATE_SOURCE:
//		case IDDFS_FIXER_CLASS:
//		case IDDFS_DEFECT_CLASS:
//		case IDDFS_AJAX_FEATURE:
//			return sortByIDDFS(map, list, sortType, 5);
//		case IDDFS_CANDIDATE_SOURCE_PERCENTAGE:
//		case IDDFS_FIXER_CLASS_PERCENTAGE:
//		case IDDFS_DEFECT_CLASS_PERCENTAGE:
//		case IDDFS_AJAX_FEATURE_PERCENTAGE:
//			return sortByIDDFSwithPercentage(map, list, sortType, 5);
//		case RANDOM:
//			Collections.shuffle(list);
//			return list;
//		default:
//			return list;
//		}
//	}
//
//	private List<MutationFileInformation> sortByRSRepair(Map<SortKey, List<MutationFileInformation>> map,
//			List<MutationFileInformation> list){
//		List<MutationFileInformation> rtnList = new ArrayList<MutationFileInformation>();
//		for(SortKey sortKey : map.keySet()){
//			if(sortKey.getSimpleStatementWeight()<9){
//				rtnList.addAll(map.get(sortKey));
//			}
//		}
//		Collections.shuffle(rtnList);
//		return rtnList;
//	}
//
//	private List<MutationFileInformation> sortByDFS(
//			Map<SortKey, List<MutationFileInformation>> map,
//			List<MutationFileInformation> list) {
//		List<MutationFileInformation> rtnList = new ArrayList<MutationFileInformation>();
//		// sort MapKeys
//		List<SortKey> sortedMapKeys = new ArrayList<SortKey>(map.keySet());
//		Collections.sort(sortedMapKeys);
//
//		for (int i = 0; i < sortedMapKeys.size(); i++) {
//			rtnList.addAll(map.get(sortedMapKeys.get(i)));
//		}
//		return rtnList;
//	}
//
//	//new Algorithm
//	private List<MutationFileInformation> sortByCandidateSourceDFS(Map<SortKey, List<MutationFileInformation>> map,
//			List<MutationFileInformation> list){
//		List<MutationFileInformation> rtnList = new ArrayList<MutationFileInformation>();
//		Map<Integer, List<SortKey>> dfsSortKeyMap = new HashMap<Integer, List<SortKey>>();
//
//		for (SortKey key : map.keySet()) {
//			if (!dfsSortKeyMap.containsKey(key
//					.getRepairSourcePrioritizedDfsWeight())) {
//				dfsSortKeyMap.put(key.getRepairSourcePrioritizedDfsWeight(),
//						new ArrayList<SortKey>());
//			}
//			dfsSortKeyMap.get(key.getRepairSourcePrioritizedDfsWeight()).add(key);
//		}
//
//		// sort bfsSortKeyMap
//		List<Integer> sortedDfsSortKeyMap = new ArrayList<Integer>(
//				dfsSortKeyMap.keySet());
//		Collections.sort(sortedDfsSortKeyMap);
//
//		// add info to list
//		for (Integer dfsSortKeyMapInteger : sortedDfsSortKeyMap) {
//			List<SortKey> tempSortKeyList = dfsSortKeyMap
//					.get(dfsSortKeyMapInteger);
//			Collections.sort(tempSortKeyList);
//			while (true) {
//				int count = 0;
//				for (int i = 0; i < tempSortKeyList.size(); i++) {
//					List<MutationFileInformation> mutationFileInfoList = map
//							.get(tempSortKeyList.get(i));
//					if (mutationFileInfoList.size() > 0) {
//						count++;
//						rtnList.add(mutationFileInfoList.remove(0));
//					}
//				}
//				if (count == 0) {
//					break;
//				}
//			}
//		}
//
//		return rtnList;
//
//	}
//
//	private List<MutationFileInformation> sortByCandidateSourceDFSwithAllPermutations(Map<SortKey, List<MutationFileInformation>> map,
//			List<MutationFileInformation> list,
//			int num4Candidate, int num4AjaxFeature, int num4Event, int num4DOM){
//		List<MutationFileInformation> rtnList = new ArrayList<MutationFileInformation>();
//		Map<Integer, List<SortKey>> dfsSortKeyMap = new HashMap<Integer, List<SortKey>>();
//
//		for (SortKey key : map.keySet()) {
//			if (!dfsSortKeyMap.containsKey(key
//					.getRepairSourcePrioritizedDfsWeight4AllPermutationsTrial(num4Candidate, num4AjaxFeature, num4Event, num4DOM))) {
//				dfsSortKeyMap.put(key.getRepairSourcePrioritizedDfsWeight4AllPermutationsTrial(num4Candidate, num4AjaxFeature, num4Event, num4DOM),
//						new ArrayList<SortKey>());
//			}
//			dfsSortKeyMap.get(key.getRepairSourcePrioritizedDfsWeight4AllPermutationsTrial(num4Candidate, num4AjaxFeature, num4Event, num4DOM)).add(key);
//		}
//
//		// sort bfsSortKeyMap
//		List<Integer> sortedDfsSortKeyMap = new ArrayList<Integer>(
//				dfsSortKeyMap.keySet());
//		Collections.sort(sortedDfsSortKeyMap);
//
//		// add info to list
//		for (Integer dfsSortKeyMapInteger : sortedDfsSortKeyMap) {
//			List<SortKey> tempSortKeyList = dfsSortKeyMap
//					.get(dfsSortKeyMapInteger);
//			Collections.sort(tempSortKeyList);
//			while (true) {
//				int count = 0;
//				for (int i = 0; i < tempSortKeyList.size(); i++) {
//					List<MutationFileInformation> mutationFileInfoList = map
//							.get(tempSortKeyList.get(i));
//					if (mutationFileInfoList.size() > 0) {
//						count++;
//						rtnList.add(mutationFileInfoList.remove(0));
//					}
//				}
//				if (count == 0) {
//					break;
//				}
//			}
//		}
//		return rtnList;
//	}
//
//	private List<MutationFileInformation> sortByBFS(
//			Map<SortKey, List<MutationFileInformation>> map,
//			List<MutationFileInformation> list, SortType sortType) {
//		List<MutationFileInformation> rtnList = new ArrayList<MutationFileInformation>();
//		Map<Integer, List<SortKey>> bfsSortKeyMap = new HashMap<Integer, List<SortKey>>();
//
//		// create new map for MapKeys for BFS
//		switch (sortType) {
//		case BFS_CANDIDATE_SOURCE:
//			for (SortKey key : map.keySet()) {
//				if (!bfsSortKeyMap.containsKey(key
//						.getCandidateSourceBfsWeight())) {
//					bfsSortKeyMap.put(key.getCandidateSourceBfsWeight(),
//							new ArrayList<SortKey>());
//				}
//				bfsSortKeyMap.get(key.getCandidateSourceBfsWeight()).add(key);
//			}
//			break;
//		case BFS_FIXER_CLASS:
//			for (SortKey key : map.keySet()) {
//				if (!bfsSortKeyMap.containsKey(key.getFixerClassBfsWeight())) {
//					bfsSortKeyMap.put(key.getFixerClassBfsWeight(),
//							new ArrayList<SortKey>());
//				}
//				bfsSortKeyMap.get(key.getFixerClassBfsWeight()).add(key);
//			}
//			break;
//		case BFS_DEFECT_CLASS:
//			for (SortKey key : map.keySet()) {
//				if (!bfsSortKeyMap.containsKey(key.getDefectClassBfsWeight())) {
//					bfsSortKeyMap.put(key.getDefectClassBfsWeight(),
//							new ArrayList<SortKey>());
//				}
//				bfsSortKeyMap.get(key.getDefectClassBfsWeight()).add(key);
//			}
//			break;
//		case BFS_AJAX_FEATURE:
//			for (SortKey key : map.keySet()) {
//				if (!bfsSortKeyMap.containsKey(key.getAjaxFeatureBfsWeight())) {
//					bfsSortKeyMap.put(key.getAjaxFeatureBfsWeight(),
//							new ArrayList<SortKey>());
//				}
//				bfsSortKeyMap.get(key.getAjaxFeatureBfsWeight()).add(key);
//			}
//			break;
//		default:
//			return list;
//		}
//
//		// sort bfsSortKeyMap
//		List<Integer> sortedBfsSortKeyMap = new ArrayList<Integer>(
//				bfsSortKeyMap.keySet());
//		Collections.sort(sortedBfsSortKeyMap);
//
//		// add info to list
//		for (Integer bfsSortKeyMapInteger : sortedBfsSortKeyMap) {
//			List<SortKey> tempSortKeyList = bfsSortKeyMap
//					.get(bfsSortKeyMapInteger);
//			Collections.sort(tempSortKeyList);
//			while (true) {
//				int count = 0;
//				for (int i = 0; i < tempSortKeyList.size(); i++) {
//					List<MutationFileInformation> mutationFileInfoList = map
//							.get(tempSortKeyList.get(i));
//					if (mutationFileInfoList.size() > 0) {
//						count++;
//						rtnList.add(mutationFileInfoList.remove(0));
//					}
//				}
//				if (count == 0) {
//					break;
//				}
//			}
//		}
//		return rtnList;
//	}
//
//	private List<MutationFileInformation> sortBySimple(
//			Map<SortKey, List<MutationFileInformation>> map,
//			List<MutationFileInformation> list, SortType sortType) {
//		List<MutationFileInformation> rtnList = new ArrayList<MutationFileInformation>();
//		Map<Integer, List<SortKey>> simpleSortKeyMap = new HashMap<Integer, List<SortKey>>();
//
//		// create new map for MapKeys for BFS
//		switch (sortType) {
//		case SIMPLE_LOCATION:
//			for (SortKey key : map.keySet()) {
//				if (!simpleSortKeyMap.containsKey(key
//						.getSimpleStatementWeight())) {
//					simpleSortKeyMap.put(key.getSimpleStatementWeight(),
//							new ArrayList<SortKey>());
//				}
//				simpleSortKeyMap.get(key.getSimpleStatementWeight()).add(key);
//			}
//			break;
//		case SIMPLE_AJAX_DEFECT:
//			for (SortKey key : map.keySet()) {
//				if (!simpleSortKeyMap.containsKey(key.getSimpleAjaxAndDefectClassWeight())) {
//					simpleSortKeyMap.put(key.getSimpleAjaxAndDefectClassWeight(),
//							new ArrayList<SortKey>());
//				}
//				simpleSortKeyMap.get(key.getSimpleAjaxAndDefectClassWeight()).add(key);
//			}
//			break;
//		case SIMPLE_CANDIDATE_SOURCE:
//			for (SortKey key : map.keySet()) {
//				if (!simpleSortKeyMap.containsKey(key.getSimpleCandidateSourceWeight())) {
//					simpleSortKeyMap.put(key.getSimpleCandidateSourceWeight(),
//							new ArrayList<SortKey>());
//				}
//				simpleSortKeyMap.get(key.getSimpleCandidateSourceWeight()).add(key);
//			}
//			break;
//		default:
//			return list;
//		}
//
//		// sort bfsSortKeyMap
//		List<Integer> sortedSimpleSortKeyMap = new ArrayList<Integer>(
//				simpleSortKeyMap.keySet());
//		Collections.sort(sortedSimpleSortKeyMap);
//
//		// add info to list
//		for (Integer simpleSortKeyMapInteger : sortedSimpleSortKeyMap) {
//			List<SortKey> tempSortKeyList = simpleSortKeyMap
//					.get(simpleSortKeyMapInteger);
//			List<MutationFileInformation> shuffleList = new ArrayList<MutationFileInformation>();
//			for(SortKey sortkey:tempSortKeyList){
//				shuffleList.addAll(map.get(sortkey));
//			}
//			Collections.shuffle(shuffleList);
//			rtnList.addAll(shuffleList);
//		}
//		return rtnList;
//	}
//
//	private List<MutationFileInformation> sortBySimpleWithPermutation(
//			Map<SortKey, List<MutationFileInformation>> map,
//			List<MutationFileInformation> list, SortType sortType,
//			int num4Candidate, int num4AjaxFeatures, int num4Event, int num4DOM) {
//		List<MutationFileInformation> rtnList = new ArrayList<MutationFileInformation>();
//		Map<Integer, List<SortKey>> simpleSortKeyMap = new HashMap<Integer, List<SortKey>>();
//
//		// create new map for MapKeys for BFS
//		switch (sortType) {
//		//		case SIMPLE_LOCATION:
//		//			for (SortKey key : map.keySet()) {
//		//				if (!simpleSortKeyMap.containsKey(key
//		//						.getSimpleStatementWeight())) {
//		//					simpleSortKeyMap.put(key.getSimpleStatementWeight(),
//		//							new ArrayList<SortKey>());
//		//				}
//		//				simpleSortKeyMap.get(key.getSimpleStatementWeight()).add(key);
//		//			}
//		//			break;
//		case SIMPLE_AJAX_DEFECT:
//			for (SortKey key : map.keySet()) {
//				if (!simpleSortKeyMap.containsKey(key.getSimpleAjaxAndDefectClassWeight4AllPermutationsTrial(num4AjaxFeatures, num4Event, num4DOM))) {
//					simpleSortKeyMap.put(key.getSimpleAjaxAndDefectClassWeight4AllPermutationsTrial(num4AjaxFeatures, num4Event, num4DOM),
//							new ArrayList<SortKey>());
//				}
//				simpleSortKeyMap.get(key.getSimpleAjaxAndDefectClassWeight4AllPermutationsTrial(num4AjaxFeatures, num4Event, num4DOM)).add(key);
//			}
//			break;
//		case SIMPLE_CANDIDATE_SOURCE:
//			for (SortKey key : map.keySet()) {
//				if (!simpleSortKeyMap.containsKey(key.getSimpleCandidateSourceWeight4AllPermutationsTrial(num4Candidate))) {
//					simpleSortKeyMap.put(key.getSimpleCandidateSourceWeight4AllPermutationsTrial(num4Candidate),
//							new ArrayList<SortKey>());
//				}
//				simpleSortKeyMap.get(key.getSimpleCandidateSourceWeight4AllPermutationsTrial(num4Candidate)).add(key);
//			}
//			break;
//		default:
//			return list;
//		}
//
//		// sort bfsSortKeyMap
//		List<Integer> sortedSimpleSortKeyMap = new ArrayList<Integer>(
//				simpleSortKeyMap.keySet());
//		Collections.sort(sortedSimpleSortKeyMap);
//
//		// add info to list
//		for (Integer simpleSortKeyMapInteger : sortedSimpleSortKeyMap) {
//			List<SortKey> tempSortKeyList = simpleSortKeyMap
//					.get(simpleSortKeyMapInteger);
//			List<MutationFileInformation> shuffleList = new ArrayList<MutationFileInformation>();
//			for(SortKey sortkey:tempSortKeyList){
//				shuffleList.addAll(map.get(sortkey));
//			}
//			Collections.shuffle(shuffleList);
//			rtnList.addAll(shuffleList);
//		}
//		return rtnList;
//	}
//
//
//
//
//
//	private List<MutationFileInformation> sortByIDDFS(
//			Map<SortKey, List<MutationFileInformation>> map,
//			List<MutationFileInformation> list, SortType sortType, int depth) {
//		List<MutationFileInformation> rtnList = new ArrayList<MutationFileInformation>();
//		Map<Integer, List<SortKey>> iddfsSortKeyMap = new HashMap<Integer, List<SortKey>>();
//
//		// create new map for MapKeys for BFS
//		switch (sortType) {
//		case IDDFS_CANDIDATE_SOURCE:
//			for (SortKey key : map.keySet()) {
//				if (!iddfsSortKeyMap.containsKey(key
//						.getCandidateSourceBfsWeight())) {
//					iddfsSortKeyMap.put(key.getCandidateSourceBfsWeight(),
//							new ArrayList<SortKey>());
//				}
//				iddfsSortKeyMap.get(key.getCandidateSourceBfsWeight()).add(key);
//			}
//			break;
//		case IDDFS_FIXER_CLASS:
//			for (SortKey key : map.keySet()) {
//				if (!iddfsSortKeyMap.containsKey(key.getFixerClassBfsWeight())) {
//					iddfsSortKeyMap.put(key.getFixerClassBfsWeight(),
//							new ArrayList<SortKey>());
//				}
//				iddfsSortKeyMap.get(key.getFixerClassBfsWeight()).add(key);
//			}
//			break;
//		case IDDFS_DEFECT_CLASS:
//			for (SortKey key : map.keySet()) {
//				if (!iddfsSortKeyMap.containsKey(key.getDefectClassBfsWeight())) {
//					iddfsSortKeyMap.put(key.getDefectClassBfsWeight(),
//							new ArrayList<SortKey>());
//				}
//				iddfsSortKeyMap.get(key.getDefectClassBfsWeight()).add(key);
//			}
//			break;
//		case IDDFS_AJAX_FEATURE:
//			for (SortKey key : map.keySet()) {
//				if (!iddfsSortKeyMap.containsKey(key.getAjaxFeatureBfsWeight())) {
//					iddfsSortKeyMap.put(key.getAjaxFeatureBfsWeight(),
//							new ArrayList<SortKey>());
//				}
//				iddfsSortKeyMap.get(key.getAjaxFeatureBfsWeight()).add(key);
//			}
//			break;
//		default:
//			return list;
//		}
//
//		// sort iddfsSortKeyMap
//		List<Integer> sortedBfsSortKeyMap = new ArrayList<Integer>(
//				iddfsSortKeyMap.keySet());
//		Collections.sort(sortedBfsSortKeyMap);
//
//		// add info to list
//		for (Integer bfsSortKeyMapInteger : sortedBfsSortKeyMap) {
//			List<SortKey> tempSortKeyList = iddfsSortKeyMap
//					.get(bfsSortKeyMapInteger);
//			Collections.sort(tempSortKeyList);
//			while (true) {
//				int count = 0;
//				for (int i = 0; i < tempSortKeyList.size(); i++) {
//					List<MutationFileInformation> mutationFileInfoList = map
//							.get(tempSortKeyList.get(i));
//					if (mutationFileInfoList.size() > 0) {
//						count++;
//						for (int j = 0; j < depth
//								&& j < mutationFileInfoList.size(); j++) {
//							rtnList.add(mutationFileInfoList.remove(0));
//						}
//					}
//				}
//				if (count == 0) {
//					break;
//				}
//			}
//		}
//
//		return rtnList;
//	}
//
//	private List<MutationFileInformation> sortByIDDFSwithPercentage(
//			Map<SortKey, List<MutationFileInformation>> map,
//			List<MutationFileInformation> list, SortType sortType, int depth) {
//		List<MutationFileInformation> rtnList = new ArrayList<MutationFileInformation>();
//		Map<Integer, List<SortKey>> iddfsSortKeyMap = new HashMap<Integer, List<SortKey>>();
//
//
//		// create new map for MapKeys for BFS
//		switch (sortType) {
//		case IDDFS_CANDIDATE_SOURCE_PERCENTAGE:
//			for (SortKey key : map.keySet()) {
//				if (!iddfsSortKeyMap.containsKey(key
//						.getCandidateSourceBfsWeight())) {
//					iddfsSortKeyMap.put(key.getCandidateSourceBfsWeight(),
//							new ArrayList<SortKey>());
//				}
//				iddfsSortKeyMap.get(key.getCandidateSourceBfsWeight()).add(key);
//			}
//			break;
//		case IDDFS_FIXER_CLASS_PERCENTAGE:
//			for (SortKey key : map.keySet()) {
//				if (!iddfsSortKeyMap.containsKey(key.getFixerClassBfsWeight())) {
//					iddfsSortKeyMap.put(key.getFixerClassBfsWeight(),
//							new ArrayList<SortKey>());
//				}
//				iddfsSortKeyMap.get(key.getFixerClassBfsWeight()).add(key);
//			}
//			break;
//		case IDDFS_DEFECT_CLASS_PERCENTAGE:
//			for (SortKey key : map.keySet()) {
//				if (!iddfsSortKeyMap.containsKey(key.getDefectClassBfsWeight())) {
//					iddfsSortKeyMap.put(key.getDefectClassBfsWeight(),
//							new ArrayList<SortKey>());
//				}
//				iddfsSortKeyMap.get(key.getDefectClassBfsWeight()).add(key);
//			}
//			break;
//		case IDDFS_AJAX_FEATURE_PERCENTAGE:
//			for (SortKey key : map.keySet()) {
//				if (!iddfsSortKeyMap.containsKey(key.getAjaxFeatureBfsWeight())) {
//					iddfsSortKeyMap.put(key.getAjaxFeatureBfsWeight(),
//							new ArrayList<SortKey>());
//				}
//				iddfsSortKeyMap.get(key.getAjaxFeatureBfsWeight()).add(key);
//			}
//			break;
//		default:
//			return list;
//		}
//
//		// sort iddfsSortKeyMap
//		List<Integer> sortedBfsSortKeyMap = new ArrayList<Integer>(
//				iddfsSortKeyMap.keySet());
//		Collections.sort(sortedBfsSortKeyMap);
//
//		// add info to list
//		for (Integer bfsSortKeyMapInteger : sortedBfsSortKeyMap) {
//			List<SortKey> tempSortKeyList = iddfsSortKeyMap
//					.get(bfsSortKeyMapInteger);
//			Collections.sort(tempSortKeyList);
//			while (true) {
//				int count = 0;
//				for (int i = 0; i < tempSortKeyList.size(); i++) {
//					List<MutationFileInformation> mutationFileInfoList = map
//							.get(tempSortKeyList.get(i));
//					int targetFileListSize = mutationFileInfoList.size();
//					if (mutationFileInfoList.size() > 0) {
//						count++;
//						int depthWithPercentage = calculateDepthWithPercentage(this.allMutationFilesSize, targetFileListSize, depth);
//						for (int j = 0; j < depthWithPercentage && j < mutationFileInfoList.size(); j++) {
//							rtnList.add(mutationFileInfoList.remove(0));
//						}
//					}
//				}
//				if (count == 0) {
//					break;
//				}
//			}
//		}
//		return rtnList;
//	}
//
//	private int calculateDepthWithPercentage(int allMutationFileInfosSize, int targetListSize, int depth){
//		int depthWithPercentage;
//		double percentage = ((double) targetListSize) / ((double) allMutationFileInfosSize);
//		//		System.out.println("percentage:" + percentage);
//		depthWithPercentage = (int) (((double)depth)*percentage);
//		//		System.out.println("depthWithPercentage" + depthWithPercentage);
//		if(depthWithPercentage==0){
//			depthWithPercentage=1;
//		}
//		return depthWithPercentage;
//	}
//
//
//	//TODO:for now, only count for failure json
//	//TODO: change 10 -  part
//	private List<MutationFileInformation> setWeight(
//			List<MutationFileInformation> mutationFileInformations)
//					throws IOException, JSONException {
//		//if jscover can't get covereage
//		if(this.failureJSON.equals("none")){
//			for(MutationFileInformation mutationFileInfo : mutationFileInformations){
//				mutationFileInfo.setWeight(1);
//			}
//			return mutationFileInformations;
//		}
//
//
//		//		JSONObject success_coverage_json = Utils
//		//				.parse(successJSON);
//		JSONObject failure_coverage_json = Coverage.parse(new File(failureJSON));
//
//		//		String url_path_to_js_file = "";
//
//		//		JSONArray success = Utils.getCoverageData(success_coverage_json,
//		//				url_path_to_js_file);
//
//		JSONArray failure = Coverage.getCoverageData(failure_coverage_json,
//				urlPathToJS);
//
//		int line_num = failure.length(); // same: failure.length()
//		double[] weighted_path = new double[line_num];
//
//		//get max value
//		int max=0;
//		for (int i = 1; i < line_num; i++) {
//			int failure_cover_freq = Coverage.getCoverFreq(failure.get(i));
//			if(failure_cover_freq > max){
//				max = failure_cover_freq;
//			}
//		}
//
//		for (int i = 1; i < line_num; i++) {
//			//			Object success_line = success.get(i);
//			Object failure_line = failure.get(i);
//
//			//			int success_cover_freq = Utils.getCoverFreq(success_line);
//			int failure_cover_freq = Coverage.getCoverFreq(failure_line);
//
//			//			if (0 < success_cover_freq && 0 < failure_cover_freq) {
//			//				weighted_path[i] = Wpath;
//			//			} else if (success_cover_freq == 0 && 0 < failure_cover_freq) {
//			//				weighted_path[i] = 1;
//			//			} else {
//			//				weighted_path[i] = 0;
//			//			}
//			weighted_path[i] = (int)(9.0*(1.0 - (double) failure_cover_freq / (double)max));//put assumption that all weight should be under 9
//			System.out.println("weightedPath:" + weighted_path[i]);
//		}
//		for (MutationFileInformation mutationFileInformation : mutationFileInformations) {
//			int lineno = mutationFileInformation.getStartLine();
//			mutationFileInformation.setWeight(weighted_path[lineno]);
//		}
//		return mutationFileInformations;
//	}
	
}