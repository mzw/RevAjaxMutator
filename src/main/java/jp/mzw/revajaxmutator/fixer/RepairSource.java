package jp.mzw.revajaxmutator.fixer;

/**
 * 
 * @author Junto Nakaoka
 *
 */
public class RepairSource implements Comparable<RepairSource> {
	private String repairValue;
	private Candidate.CandidateSource repairSource;

	public RepairSource(String repairValue,
			Candidate.CandidateSource repairSource) {
		this.repairValue = repairValue;
		this.repairSource = repairSource;
	}

	@Override
	public int compareTo(RepairSource o) {
		if (this.repairValue.equals(o.getRepairValue()))
			return 0;// same repair value
		else
			return 1;// different repair value
	}

	public String getRepairValue() {
		return this.repairValue;
	}

	public Candidate.CandidateSource getRepairSource() {
		return this.repairSource;
	}

}
