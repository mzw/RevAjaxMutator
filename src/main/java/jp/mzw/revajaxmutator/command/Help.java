package jp.mzw.revajaxmutator.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Help extends Command {
	protected static Logger LOG = LoggerFactory.getLogger(Help.class);

	public String getUsageContent() {
		StringBuilder builder = new StringBuilder();
		for (Class<? extends Command> clazz : getCommands()) {
			try {
				Command command = clazz.newInstance();
				if (command instanceof Help) {
					continue;
				}
				builder.append(command.getUsageContent());
			} catch (InstantiationException | IllegalAccessException e) {
				LOG.error("Invalid command class: {}", clazz.getName());
			}
		}
		return builder.toString();
	}

}
