package jp.mzw.revajaxmutator.search;

import java.util.Comparator;

import jp.mzw.revajaxmutator.parser.RepairSource;

public class SortKey implements Comparable<SortKey>, Comparator<SortKey> {
	private final double statementWeight;

	private final AjaxFeature ajaxFeature;
	private final DefectClass defectClass;
	private final FixerClass fixerClass;
	private final RepairSource.Type repairSourceType;

	private final int totalWeight;
	private final int candidateSourceBfsWeight;
	private final int fixerClassBfsWeight;
	private final int defectClassBfsWeight;
	private final int ajaxFeatureBfsWeight;
	private final int repairSourcePrioritizedDfsWeight;

	private final int simpleAjaxAndDefectClassWeight;
	private final int simpleStatementWeight;
	private final int simpleCandidateSourceWeight;

	public SortKey(double weight, String defectClass, String fixerClass, String repairSource) {
		this.statementWeight = weight;
		this.ajaxFeature = AjaxFeature.fromMutatableName(defectClass);
		this.defectClass = DefectClass.fromMutableName(defectClass);
		this.fixerClass = FixerClass.fromFixerName(fixerClass);
		this.repairSourceType = RepairSource.getType(repairSource);
		this.totalWeight = calculateWeight();
		this.candidateSourceBfsWeight = calculateCandidateSourceBFSWeight();
		this.fixerClassBfsWeight = calculateFixerClassBFSWeight();
		this.defectClassBfsWeight = calculateDefectClassBFSWeight();
		this.ajaxFeatureBfsWeight = calculateAjaxFeatureBFSWeight();
		this.repairSourcePrioritizedDfsWeight = calculateRepairSourcePrioritizedDFSWeight();

		this.simpleAjaxAndDefectClassWeight = calculateSimpleAjaxFeatureAndDefectClassPrioritizationWeight();
		this.simpleStatementWeight = calculateSimpleLocationWeight();
		this.simpleCandidateSourceWeight = calculateSimpleCandidateSourceWeight();
	}

	private int calculateSimpleAjaxFeatureAndDefectClassPrioritizationWeight() {
		int totalWeight = 0;
		totalWeight += this.defectClass.getValue();
		totalWeight += this.ajaxFeature.getValue() * 10;
		return totalWeight;
	}

	private int calculateSimpleLocationWeight() {
		int totalWeight = 0;
		totalWeight += this.statementWeight;
		return totalWeight;
	}

	private int calculateSimpleCandidateSourceWeight() {
		int totalWeight = 0;
		totalWeight += this.repairSourceType.getWeight();
		return totalWeight;
	}

	private int calculateRepairSourcePrioritizedDFSWeight() {
		int totalWeight = 0;
		totalWeight += this.repairSourceType.getWeight() * 1000;
		totalWeight += this.ajaxFeature.getValue() * 100;
		totalWeight += this.defectClass.getValue() * 10;
		totalWeight += this.statementWeight;
		// totalWeight += this.fixerClass.getValue();
		return totalWeight;
	}

	private int calculateWeight() {
		int totalWeight = 0;
		totalWeight += (int) (this.statementWeight * 10000);
		totalWeight += this.ajaxFeature.getValue() * 1000;
		totalWeight += this.defectClass.getValue() * 100;
		totalWeight += this.fixerClass.getValue() * 10;
		totalWeight += this.repairSourceType.getWeight();
		return totalWeight;
	}

	private int calculateCandidateSourceBFSWeight() {
		int bfsWeight = 0;
		bfsWeight += (int) (this.statementWeight * 10000);
		bfsWeight += this.ajaxFeature.getValue() * 1000;
		bfsWeight += this.defectClass.getValue() * 100;
		bfsWeight += this.fixerClass.getValue() * 10;
		return bfsWeight;
	}

	private int calculateFixerClassBFSWeight() {
		int bfsWeight = 0;
		bfsWeight += (int) (this.statementWeight * 10000);
		bfsWeight += this.ajaxFeature.getValue() * 1000;
		bfsWeight += this.defectClass.getValue() * 100;
		return bfsWeight;
	}

	private int calculateDefectClassBFSWeight() {
		int bfsWeight = 0;
		bfsWeight += (int) (this.statementWeight * 10000);
		bfsWeight += this.ajaxFeature.getValue() * 1000;
		return bfsWeight;
	}

	private int calculateAjaxFeatureBFSWeight() {
		int bfsWeight = 0;
		bfsWeight += (int) (this.statementWeight * 10000);
		return bfsWeight;
	}

	// ----------------getter-------------------

	public int getTotalWeight() {
		return this.totalWeight;
	}

	public int getCandidateSourceBfsWeight() {
		return candidateSourceBfsWeight;
	}

	public int getFixerClassBfsWeight() {
		return fixerClassBfsWeight;
	}

	public int getDefectClassBfsWeight() {
		return defectClassBfsWeight;
	}

	public int getAjaxFeatureBfsWeight() {
		return ajaxFeatureBfsWeight;
	}

	public int getRepairSourcePrioritizedDfsWeight() {
		return repairSourcePrioritizedDfsWeight;
	}

	public int getSimpleAjaxAndDefectClassWeight() {
		return simpleAjaxAndDefectClassWeight;
	}

	public int getSimpleStatementWeight() {
		return simpleStatementWeight;
	}

	public int getSimpleCandidateSourceWeight() {
		return simpleCandidateSourceWeight;
	}

	// ascending order
	@Override
	public int compareTo(SortKey o) {
		return this.totalWeight - o.totalWeight;
	}

	// ascending order
	@Override
	public int compare(SortKey o1, SortKey o2) {
		return o1.getTotalWeight() - o2.totalWeight;
	}

	// for HashSet
	@Override
	public boolean equals(Object obj) {
		if (this.totalWeight == ((SortKey) obj).totalWeight) {
			return true;
		} else {
			return false;
		}
	}

	// for HashSet
	@Override
	public int hashCode() {
		return totalWeight;
	}

}
