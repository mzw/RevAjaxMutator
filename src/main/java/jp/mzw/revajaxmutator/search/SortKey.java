package jp.mzw.revajaxmutator.search;

import java.util.Comparator;

public class SortKey {
//	implements Comparable<SortKey>, Comparator<SortKey> {
//	private final double statementWeight;
//	private final AjaxFeature ajaxFeature;
//	private final DefectClass defectClass;
//	private final FixerClass fixerClass;
//	private final CandidateSource candidateSource;
//	private final int totalWeight;
//	private final int candidateSourceBfsWeight;
//	private final int fixerClassBfsWeight;
//	private final int defectClassBfsWeight;
//	private final int ajaxFeatureBfsWeight;
//	private final int repairSourcePrioritizedDfsWeight;
//	
//	private final int simpleAjaxAndDefectClassWeight;
//	private final int simpleStatementWeight;
//	private final int simpleCandidateSourceWeight;
//	
//	public SortKey(double weight, String defectClass, String fixerClass, String candidateSource) {
//		this.statementWeight = weight;
//		this.ajaxFeature = AjaxFeature.fromMutatableName(defectClass);
//		this.defectClass = DefectClass.fromStringName(defectClass);
//		this.fixerClass = FixerClass.fromStringName(fixerClass);
//		this.candidateSource = CandidateSource.fromStringName(candidateSource);
//		this.totalWeight = calculateWeight();
//		this.candidateSourceBfsWeight = calculateCandidateSourceBFSWeight();
//		this.fixerClassBfsWeight = calculateFixerClassBFSWeight();
//		this.defectClassBfsWeight = calculateDefectClassBFSWeight();
//		this.ajaxFeatureBfsWeight = calculateAjaxFeatureBFSWeight();
//		this.repairSourcePrioritizedDfsWeight = calculateRepairSourcePrioritizedDFSWeight();
//		
//		this.simpleAjaxAndDefectClassWeight = calculateSimpleAjaxFeatureAndDefectClassPrioritizationWeight();
//		this.simpleStatementWeight = calculateSimpleLocationWeight();
//		this.simpleCandidateSourceWeight = calculateSimpleCandidateSourceWeight();
//	}
//	
//	
//	private int calculateSimpleAjaxFeatureAndDefectClassPrioritizationWeight(){
//		int totalWeight = 0;
//		totalWeight += this.defectClass.getValue();
//		totalWeight += this.ajaxFeature.getValue()*10;
//		return totalWeight;
//	}
//	
//	private int calculateSimpleLocationWeight(){
//		int totalWeight = 0;
//		totalWeight += this.statementWeight;
//		return totalWeight;
//	}
//	
//	private int calculateSimpleCandidateSourceWeight(){
//		int totalWeight = 0;
//		totalWeight += this.candidateSource.getValue();
//		return totalWeight;
//	}
//	
//	private int calculateRepairSourcePrioritizedDFSWeight(){
//		int totalWeight = 0;
//		totalWeight += this.candidateSource.getValue()*1000;
//		totalWeight += this.ajaxFeature.getValue()*100;
//		totalWeight += this.defectClass.getValue()*10;
//		totalWeight += this.statementWeight;
////		totalWeight += this.fixerClass.getValue();
//		return totalWeight;
//	}
//	
//	private int calculateWeight(){
//		int totalWeight = 0;
//		totalWeight += (int)(this.statementWeight *10000);
//		totalWeight += this.ajaxFeature.getValue()*1000;
//		totalWeight += this.defectClass.getValue()*100;
//		totalWeight += this.fixerClass.getValue()*10;
//		totalWeight += this.candidateSource.getValue();
//		return totalWeight;
//	}
//	
//	private int calculateCandidateSourceBFSWeight(){
//		int bfsWeight = 0;
//		bfsWeight += (int)(this.statementWeight *10000);
//		bfsWeight += this.ajaxFeature.getValue()*1000;
//		bfsWeight += this.defectClass.getValue()*100;
//		bfsWeight += this.fixerClass.getValue()*10;
//		return bfsWeight;
//	}
//	
//	private int calculateFixerClassBFSWeight(){
//		int bfsWeight = 0;
//		bfsWeight += (int)(this.statementWeight *10000);
//		bfsWeight += this.ajaxFeature.getValue()*1000;
//		bfsWeight += this.defectClass.getValue()*100;
//		return bfsWeight;
//	}
//	
//	private int calculateDefectClassBFSWeight(){
//		int bfsWeight = 0;
//		bfsWeight += (int)(this.statementWeight *10000);
//		bfsWeight += this.ajaxFeature.getValue()*1000;
//		return bfsWeight;
//	}
//	
//	private int calculateAjaxFeatureBFSWeight(){
//		int bfsWeight = 0;
//		bfsWeight += (int)(this.statementWeight *10000);
//		return bfsWeight;
//	}
//	
//	
//	
//	//----------------getter-------------------
//	
//	public int getTotalWeight(){
//		return this.totalWeight;
//	}
//	
//	public int getCandidateSourceBfsWeight() {
//		return candidateSourceBfsWeight;
//	}
//
//	public int getFixerClassBfsWeight() {
//		return fixerClassBfsWeight;
//	}
//
//	public int getDefectClassBfsWeight() {
//		return defectClassBfsWeight;
//	}
//
//	public int getAjaxFeatureBfsWeight() {
//		return ajaxFeatureBfsWeight;
//	}
//	
//
//	public int getRepairSourcePrioritizedDfsWeight() {
//		return repairSourcePrioritizedDfsWeight;
//	}
//	
//
//	public int getSimpleAjaxAndDefectClassWeight() {
//		return simpleAjaxAndDefectClassWeight;
//	}
//
//
//	public int getSimpleStatementWeight() {
//		return simpleStatementWeight;
//	}
//
//
//	public int getSimpleCandidateSourceWeight() {
//		return simpleCandidateSourceWeight;
//	}
//
//	
//	//------------getter for all permutations trial----------------
//	public int getSimpleCandidateSourceWeight4AllPermutationsTrial(int num4Candidate){
//		int totalWeight = 0;
//		totalWeight += this.candidateSource.getValueForAllPermutationsTrial(num4Candidate);
//		return totalWeight;
//	}
//	
//	public int getSimpleAjaxAndDefectClassWeight4AllPermutationsTrial(int num4AjaxFeatures, int num4Event, int num4DOM){
//		int totalWeight = 0;
//		totalWeight += this.defectClass.getValueForAllPermutationsTrial(num4Event, num4DOM);
//		totalWeight += this.ajaxFeature.getValueForAllPermutationsTrial(num4AjaxFeatures)*10;
//		return totalWeight;
//	}
//	
//	public int getRepairSourcePrioritizedDfsWeight4AllPermutationsTrial(int num4Candidate, int num4AjaxFeature, int num4Event, int num4DOM){
//		int totalWeight = 0;
//		totalWeight += this.candidateSource.getValueForAllPermutationsTrial(num4Candidate)*1000;
//		totalWeight += this.ajaxFeature.getValueForAllPermutationsTrial(num4AjaxFeature)*100;
//		totalWeight += this.defectClass.getValueForAllPermutationsTrial(num4Event, num4DOM)*10;
//		totalWeight += this.statementWeight;
////		totalWeight += this.fixerClass.getValue();
//		return totalWeight;
//	}
//	
//	
//
//	//ascending order
//	@Override
//	public int compareTo(SortKey o) {
//		return this.totalWeight - o.totalWeight;
//	}
//	
//	//ascending order
//	@Override
//	public int compare(SortKey o1, SortKey o2) {
//		return o1.getTotalWeight() - o2.totalWeight;
//	}
//	
//	
//	//for HashSet
//	@Override
//	public boolean equals(Object obj) {
//		if(this.totalWeight == ((SortKey)obj).totalWeight){
//			return true;
//		}else{
//			return false;
//		}
//	}
//	
//	//for HashSet
//	@Override
//	public int hashCode() {
//		return totalWeight;
//	}
//	
}
