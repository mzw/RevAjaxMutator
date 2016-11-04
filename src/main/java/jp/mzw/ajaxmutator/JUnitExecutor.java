package jp.mzw.ajaxmutator;

import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runners.model.InitializationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * TestExecutor for testclasses written in Junit4.
 *
 * @author Kazuki Nishiura
 */
public class JUnitExecutor implements TestExecutor ,Cloneable{
	protected Logger LOGGER = LoggerFactory.getLogger(JUnitExecutor.class);
	private final boolean shouldRunAllTest;
    private final Class<?>[] targetClasses;
    private Map<String, Boolean> testResults;
    private String executionMessage;

    public JUnitExecutor(Class<?>... targetClasses) {
        this(true, targetClasses);
    }

    public JUnitExecutor(boolean shouldRunAllTest, Class<?>... targetClasses) {
        this.shouldRunAllTest = shouldRunAllTest;
        this.targetClasses = targetClasses;
    }
    
    public List<Result> run() {
        ArrayList<Result> results = new ArrayList<Result>();
        for (Class<?> testClass: targetClasses) {
        	Result result = runSingleTest(testClass);
        	results.add(result);
        }
    	return results;
    }
    private Result runSingleTest(Class<?> testClass) {
        Runner runner;
        try {
        	RunWith runWith = testClass.getAnnotation(RunWith.class);
        	if(runWith == null) {
                runner = new JUnitTestRunner(testClass, shouldRunAllTest);
        	} else if(Theories.class.equals(runWith.value())) {
        		runner = new JUnitTheoryRunner(testClass, shouldRunAllTest);
        	} else {
                runner = new JUnitTestRunner(testClass, shouldRunAllTest);
        	}
        } catch (InitializationError error) {
            throw new IllegalStateException(error);
        }
        Result result = (new JUnitCore()).run(runner);
        return result;
    }
    

    @Override
    public boolean execute() {
        testResults = new TreeMap<String, Boolean>();
        
        for (Class<?> testClass: targetClasses) {
        	
        	//テストクラス数分テスト実行
            if (!executeSingleTest(testClass)) {
                updateMessage(false);
                return false;
            }
        }
        updateMessage(true);
        return true;
    }
    
    
    private boolean executeSingleTest(Class<?> testClass) {
        Runner runner;
        try {
        	//Runwith:テスト実行方法に際してのクラスを指定するためのアノテーション
        	//testClassは実行するテストのクラス
        	RunWith runWith = testClass.getAnnotation(RunWith.class);
        	if(runWith == null) {
        		//アノテーション指定なし
                runner = new JUnitTestRunner(testClass, shouldRunAllTest);
        	} else if(Theories.class.equals(runWith.value())) {
        		runner = new JUnitTheoryRunner(testClass, shouldRunAllTest);
        	} else {
        		LOGGER.debug("Found unimplemented test-runner: {}", runWith.value());
                runner = new JUnitTestRunner(testClass, shouldRunAllTest);
        	}
        } catch (InitializationError error) {
            throw new IllegalStateException(error);
        }
        
        //ランナーを走らせる
        System.out.println("[ThredID="+Thread.currentThread().getId()+"]"+"JUnitCore run()");
        Result result = (new JUnitCore()).run(runner);
        
        System.out.println("[ThredID="+Thread.currentThread().getId()+"]"+"result.getFailureCount():"+result.getFailureCount());
        System.out.println("[ThredID="+Thread.currentThread().getId()+"]"+"result.getRunCount():"+result.getRunCount());
        System.out.println("[ThredID="+Thread.currentThread().getId()+"]"+"result.getRunTime():"+result.getRunTime());
        
        System.out.println("[ThredID="+Thread.currentThread().getId()+"]"+"storeResult");
        storeResult(result);
        
        System.out.println("[ThredID="+Thread.currentThread().getId()+"]"+"result.wasSuccessful():"+result.wasSuccessful());
        return result.wasSuccessful();
    }

    private void storeResult(Result result) {
    	
        List<String> testMethods = new ArrayList<String>();
        
        for (Class<?> clazz: targetClasses) {
            for (Method method: clazz.getMethods()) {
                if (method.isAnnotationPresent(Test.class))
                    testMethods.add(method.getName());
            }
        }
        for (String methodName: testMethods) {
            testResults.put(methodName, true);
        }
        if (!result.wasSuccessful()) {
        	System.out.println("[ThredID="+Thread.currentThread().getId()+"]"+"テスト失敗");
            for (Failure failure: result.getFailures()) {
            	LOGGER.debug("Failure trace: {}", failure.getTrace());
                if (failure.getDescription().getMethodName() == null) {
                    testResults.put("setup or teardown", false);
                    continue;
                }
                
                System.out.println("[ThredID="+Thread.currentThread().getId()+"]"+"testResults.put");
                testResults.put(failure.getDescription().getMethodName(), false);
            }
        }
    }

    private void updateMessage(boolean result) {
        StringBuilder messageBuilder = new StringBuilder();
        if (result) {
        	System.out.println("[ThredID="+Thread.currentThread().getId()+"]"+"result=true");
            messageBuilder.append("Test succeed (failed to kill mutants), ")
                    .append(testResults.size()).append(" tests ran.\n");
        } else {
        	System.out.println("[ThredID="+Thread.currentThread().getId()+"]"+"result=false");
            messageBuilder.append("Mutant is killed; tests failed within ")
                .append(testResults.size()).append('\n');
        }
        for (Map.Entry<String, Boolean> entry: testResults.entrySet()) {
            messageBuilder.append(entry.getKey()).append(':')
                    .append(entry.getValue() ? 'x' : 'o').append(", ");
        }
        messageBuilder.append("result: " + (result ? 'x' : 'o'));
        
        executionMessage = messageBuilder.toString();
        System.out.println("[ThredID="+Thread.currentThread().getId()+"]"+"executionMessage:"+ executionMessage);
    }
    
    @Override
    public String getTargetClassName(){
    	return targetClasses[0].getName();
    }

    @Override
    public String getMessageOnLastExecution() {
        return executionMessage;
    }
}
