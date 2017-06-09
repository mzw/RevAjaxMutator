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
		// Generate test coverage
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
		// Program repair - record and generate mutants
		if ("generate".equals(cmd)) {
			new MutationAnalysis().record(args);
			new ProgramRepair().generate(args);
			System.exit(0);
		}
		// Program repair - generate mutants
		if ("generate-only".equals(cmd)) {
			new ProgramRepair().generate(args);
			System.exit(0);
		}
		// Program repair - test generated mutants against test cases
		if ("validate".equals(cmd)) {
			new ProgramRepair().validate(args);
			System.exit(0);
		}
		if ("validate-concurrently".equals(cmd)) {
			new ProgramRepair().validateConcurrently(args);
			System.exit(0);
		}

		// Others
		if ("record".equals(cmd)) {
			new MutationAnalysis().record(args);
			System.exit(0);
		}
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
		final StringBuilder builder = new StringBuilder();
		builder.append("$ java -cp ${ClassPath} jp.mzw.revajaxmutator.CLI <command> [arguments...]").append("\n");
		builder.append("\n");
		builder.append("Commands:").append("\n");
		return builder.toString();
	}

	/**
	 * Show usage of this command
	 */
	public void showUsage() {
		final StringBuilder builder = new StringBuilder();
		builder.append(getUsageHeader());
		builder.append(this.getUsageContent());
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
		for (final Annotation annotation : method.getAnnotations()) {
			if (annotation.annotationType().equals(org.junit.Test.class)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isTestMethodToIgnore(Method method) {
		for (final Annotation annotation : method.getAnnotations()) {
			if (annotation.annotationType().equals(org.junit.Ignore.class)) {
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
		} catch (final ClassNotFoundException e) {
			if (!className.contains(".")) {
				throw e;
			}
		}
		final int i = className.lastIndexOf('.');
		return Class.forName(className.substring(0, i) + '$' + className.substring(i + 1));
	}

	protected static String getCommandDescription(String command, String description) {
		final StringBuilder builder = new StringBuilder();
		builder.append("    ").append(command).append("\n");
		builder.append("                  ").append(description).append("\n");
		return builder.toString();
	}
}
