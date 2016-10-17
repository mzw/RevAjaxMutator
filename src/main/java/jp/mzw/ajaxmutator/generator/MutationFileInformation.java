package jp.mzw.ajaxmutator.generator;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Data class that represents generated mutation file.
 *
 * @author Kazuki Nishiura
 */
public class MutationFileInformation {
	private final String fileName;
	private final String absolutePath;
	private State state;

	private int startLine = 0;
	private int endLine = 0;
	private String mutatable = "none";
	private String fixer = "none";
	private String repairValue = "none";
	private String repairSource = "none";
	private double weight = 0.0;
	private Map<String, Boolean> testResults = new HashMap<String, Boolean>();
	private int numOfPassedTest = 0;
	private int numOfFailedTest = 0;

	public enum State {
		NON_EQUIVALENT_LIVE("non-equivalent live"), EQUIVALENT("equivalent"), KILLED(
				"killed");

		private final String stringExpression;

		private State(String stringExpression) {
			this.stringExpression = stringExpression;
		}

		public static State fromString(String key) {
			key = key.trim();
			for (int i = 0; i < values().length; i++) {
				if (values()[i].stringExpression.equals(key)) {
					return values()[i];
				}
			}
			return null;
		}
	}

	public MutationFileInformation(String fileName, String absolutePath) {
		this(fileName, absolutePath, State.NON_EQUIVALENT_LIVE);
	}

	public MutationFileInformation(String fileName, String absolutePath,
			State state) {
		this.fileName = fileName;
		this.absolutePath = absolutePath;
		this.state = state;
	}

	public MutationFileInformation(String fileName, String absolutePath,
			State state, int startLine, int endLine, String mutatable,
			String fixer, String repairValue, String repairSource) {
		this.fileName = fileName;
		this.absolutePath = absolutePath;
		this.state = state;
		this.startLine = startLine;
		this.endLine = endLine;
		this.mutatable = mutatable;
		this.fixer = fixer;
		this.repairValue = repairValue;
		this.repairSource = repairSource;
	}

	public String getFileName() {
		return fileName;
	}
	
	public String getFileNameWithoutExtension()
	{
	  int index = fileName.lastIndexOf('.');
	  if (index!=-1)
	  {
	    return fileName.substring(0, index);
	  }
	  return "";
	}

	public String getAbsolutePath() {
		return absolutePath;
	}

	public boolean canBeSkipped() {
		return state != State.NON_EQUIVALENT_LIVE;
	}

	public void setState(State state) {
		this.state = state;
	}

	public State getState() {
		return state;
	}

	public String getKilledStatusAsString() {
		return state.stringExpression;
	}
	

    public int getStartLine() {
		return startLine;
	}

	public int getEndLine() {
		return endLine;
	}

	public String getMutatable() {
		return mutatable;
	}

	public String getFixer() {
		return fixer;
	}

	public String getRepairValue() {
		this.repairValue = this.repairValue.replace(System.lineSeparator(), "");
		if(this.repairValue.contains(",")){
			this.repairValue = "";
		}
		return this.repairValue;
	}

	public String getRepairSource() {
		return repairSource;
	}

    public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public Map<String, Boolean> getTestResults() {
		return testResults;
	}

	public void setTestResults(Map<String, Boolean> testResults) {
		if(!testResults.equals(null)){
			this.testResults = testResults;
		}
		this.numOfFailedTest = 0;
		this.numOfPassedTest = 0;
		for(Boolean result: testResults.values()){
			if(result){
				numOfPassedTest++;
			} else {
				numOfFailedTest++;
			}
		}
	}

	public int getNumOfPassedTest() {
		return numOfPassedTest;
	}

	public int getNumOfFailedTest() {
		return numOfFailedTest;
	}

	@Override
	public String toString() {
		return fileName + ":" + getKilledStatusAsString();
	}
}
