package jp.mzw.ajaxmutator.test.executor;

import java.util.List;

/**
 * Interface that indicate implementing classes can execute tests and return
 * results in String.
 * 
 * @author Kazuki Nishiura
 */
public interface TestExecutor {
	/**
	 * Execute test
	 *
	 * @return if test success return true, otherwise return false. Note that in
	 *         the context of mutation analysis, if test fails, it's considered
	 *         as tests can kill mutatns.
	 */
	public boolean execute();

	public String getMessageOnLastExecution();

	public String getTargetClassName();
	
	public void setOrderdMethodNames(List<String> orderdMethodName);
}
