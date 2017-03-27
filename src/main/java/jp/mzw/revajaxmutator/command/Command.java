package jp.mzw.revajaxmutator.command;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract public class Command implements ICommand {
	protected static Logger LOG = LoggerFactory.getLogger(Command.class);

	public static void command(String cmd, String[] args) {
		// Test
		if ("test".equals(cmd)) {
			new Test().all(args);
			System.exit(0);
		}
		if ("test-each".equals(cmd)) {
			new Test().each(args);
			System.exit(0);
		}
		// Mutation analysis
		if ("mutate".equals(cmd)) {
			new MutationAnalysis().record(args);
			new MutationAnalysis().mutate(args);
			System.exit(0);
		}
		if ("mutate-only".equals(cmd)) {
			new MutationAnalysis().mutate(args);
			System.exit(0);
		}
		if ("analyze".equals(cmd)) {
			new MutationAnalysis().analyze(args);
			System.exit(0);
		}
		if ("analyze-concurrently".equals(cmd)) {
			new MutationAnalysis().concurrently(args);
			System.exit(0);
		}
		// Program repair
		if ("generate".equals(cmd)) {
			new MutationAnalysis().record(args);
			new ProgramRepair().generate(args);
			System.exit(0);
		}
		// Program repair
		if ("generate-only".equals(cmd)) {
			new ProgramRepair().generate(args);
			System.exit(0);
		}
		if ("validate".equals(cmd)) {
			new ProgramRepair().validate(args);
			System.exit(0);
		}

		// Others
		if ("proxy".equals(cmd)) {
			new Proxy().launch(args);
			System.exit(0);
		}

		// Otherwise
		new Help().showUsage();
	}

	/**
	 * Get list of commands
	 * 
	 * @return list of commands
	 */
	public static List<Class<? extends Command>> getCommands() {
		return Arrays.asList(Help.class, Test.class, MutationAnalysis.class, ProgramRepair.class, Proxy.class);
	}

	/**
	 * Get usage header
	 * 
	 * @return usage header as string
	 */
	protected static String getUsageHeader() {
		StringBuilder builder = new StringBuilder();
		builder.append("$ java -cp ${ClassPath} jp.mzw.revajaxmutator.CLI").append("\n");
		builder.append("\n");
		return builder.toString();
	}

	/**
	 * Show usage of this command
	 */
	public void showUsage() {
		StringBuilder builder = new StringBuilder();
		builder.append(getUsageHeader());
		builder.append(getUsageContent());
		System.out.println(builder);
	}

	/**
	 * Determine whether given method has Test annotation
	 * 
	 * @param method
	 *            is test method candidate
	 * @return true if has, otherwise false
	 */
	public static boolean isTestMethod(Method method) {
		for (Annotation annotation : method.getAnnotations()) {
			if (annotation.annotationType().equals(org.junit.Test.class)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @param className
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static Class<?> getClass(String className) throws ClassNotFoundException {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			if (!className.contains(".")) {
				throw e;
			}
		}
		int i = className.lastIndexOf('.');
		return Class.forName(className.substring(0, i) + '$' + className.substring(i + 1));
	}
}
