package jp.mzw.revajaxmutator.tracer;

public class TraceInfo {
	protected String test_suite_name;
	protected String test_case_name;
	protected String test_case_classname;
	protected int error_detected_line;
	
	protected String subj_stmt;
	protected String mut_op;
	
	public TraceInfo(
			String test_suite_name,
			String test_case_name,
			String test_case_classname,
			int error_detected_line
			) {
		this.test_suite_name = test_suite_name;
		this.test_case_name = test_case_name;
		this.test_case_classname = test_case_classname;
		this.error_detected_line = error_detected_line;
	}
	
	public String getTestSuiteName() {
		return this.test_suite_name;
	}
	public String getTestCaseName() {
		return this.test_case_name;
	}
	public String getTestCaseClassname() {
		return this.test_case_classname;
	}
	public int getErrorDetectedLine() {
		return this.error_detected_line;
	}
	
	public void setSubjectStatement(String subj_stmt) {
		this.subj_stmt = subj_stmt;
	}
	public String getSubjectStatement() {
		return this.subj_stmt;
	}
	public void setMutationOperator(String mut_op) {
		this.mut_op = mut_op;
	}
	public String getMutationOperator() {
		return this.mut_op;
	}
}
