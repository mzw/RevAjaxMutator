package jp.mzw.revajaxmutator;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.logging.LogManager;

import jp.mzw.revajaxmutator.command.Command;
import jp.mzw.revajaxmutator.command.Help;

import org.junit.runners.model.InitializationError;
import org.owasp.webscarab.model.StoreException;

public class CLI {

	public static void main(String[] args)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, InitializationError, InterruptedException, StoreException, IOException {
		if (args.length == 0) {
			new Help().showUsage();
			System.exit(1);
		}
		
		InputStream is = CLI.class.getClassLoader().getResourceAsStream("mylogging.properties");
		if (is != null) {
			LogManager.getLogManager().readConfiguration(is);
		}
		
		String cmd = args[0];
		String[] rargs = Arrays.copyOfRange(args, 1, args.length);
		Command.command(cmd, rargs);
	}

}
