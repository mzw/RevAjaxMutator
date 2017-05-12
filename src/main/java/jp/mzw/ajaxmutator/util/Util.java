package jp.mzw.ajaxmutator.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.mozilla.javascript.ast.AstNode;

public class Util {
	/**
	 * @return true if copy succeed.
	 */
	@SuppressWarnings("resource")
	public static boolean copyFile(String srcPath, String destPath) {
		boolean success = false;
		FileChannel srcChannel = null;
		FileChannel destChannel = null;

		try {
			final File dest = new File(destPath);
			if (dest.exists()) {
				dest.createNewFile();
			}
			srcChannel = new FileInputStream(srcPath).getChannel();
			destChannel = new FileOutputStream(destPath).getChannel();

			srcChannel.transferTo(0, srcChannel.size(), destChannel);
			success = true;
		} catch (final IOException e) {
			e.printStackTrace();
			success = false;
		} finally {
			try {
				if (srcChannel != null) {
					srcChannel.close();
				}
				if (destChannel != null) {
					destChannel.close();
				}
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
		return success;
	}

	/**
	 * read all string from specified file
	 *
	 * @return list of String, each element is a line in file. If some error
	 *         happen during reading, returns null.
	 */
	public static List<String> readFromFile(String pathToFile) {
		final List<String> lines = new ArrayList<String>();
		boolean success = false;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(new File(pathToFile)));
			String line;
			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
			success = true;
		} catch (final IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
		}
		return success ? lines : null;
	}

	/**
	 * Write content into a file with specified path. This method override
	 * existing file if it exists.
	 *
	 * @return true if write is successfully finished.
	 */
	public static boolean writeToFile(String pathToFile, String content) {
		// System.out.println("PathToFile: " + pathToFile + "content: " +
		// content + "\n");
		FileWriter writer = null;
		try {
			writer = new FileWriter(new File(pathToFile));
			writer.write(content);
			writer.flush();
		} catch (final IOException e) {
			System.err.println("IOException\n" + e.getMessage());
			return false;
		} finally {
			try {
				writer.close();
			} catch (final IOException e) {
				System.err.println("Fail to close source file" + e.getMessage());
				return false;
			}
		}
		return true;
	}

	public static String oneLineStringOf(AstNode node) {
		if (node == null) {
			return "";
		}
		final String[] str = node.toSource().split("\n");
		if (str.length == 1) {
			return str[0];
		} else {
			int numOfShownLine = 2;
			final boolean use1 = str.length > 2 && str[0].length() < 10;
			final boolean useLast2 = str.length + (use1 ? 1 : 0) > 2 && str[str.length - 1].length() < 10;
			numOfShownLine += (use1 ? 1 : 0) + (useLast2 ? 1 : 0);

			return str[0] + (use1 ? str[1] : "") + " ..(" + (str.length - numOfShownLine) + ").. "
					+ (useLast2 ? str[str.length - 2] : "") + str[str.length - 1];
		}
	}

	public static String omitLineBreak(AstNode node) {
		return omitLineBreak(node.toSource());
	}

	public static String omitLineBreak(String string) {
		return string.replaceAll("(\r)?\n", "");
	}

	public static String getFileNameWithoutExtension(String fileName) {
		final int index = fileName.lastIndexOf('.');
		if (index != -1) {
			return fileName.substring(0, index);
		}
		return "";
	}

	/**
	 * Convert line separator of given file to System's default line separator
	 * obtained by System.lineSeparator().
	 *
	 * @param file
	 *            text file whose line separator can be converted.
	 */
	public static void normalizeLineBreak(File file) {
		final List<String> lines = new ArrayList<String>();

		// read from file
		try {
			final Scanner scanner = new Scanner(new FileInputStream(file));
			while (scanner.hasNext()) {
				lines.add(scanner.nextLine());
			}
			scanner.close();
		} catch (final IOException e) {
			throw new IllegalStateException("Failed to read file " + file);
		}

		try {
			final BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			for (final String line : lines) {
				writer.write(line);
				writer.write(System.lineSeparator());
			}
			writer.close();
		} catch (final IOException e) {
			throw new IllegalStateException("Failed to write file " + file);
		}
	}

	public static String join(Collection<?> c) {
		final StringBuilder builder = new StringBuilder();
		final Iterator<?> itr = c.iterator();
		while (itr.hasNext()) {
			builder.append(itr.next());
		}
		return builder.toString();
	}

	public static String join(String[] arrayOfString) {
		return join(arrayOfString, null);
	}

	public static String join(String[] arrayOfString, String separator) {
		final StringBuilder builder = new StringBuilder();
		for (int i = 0; i < arrayOfString.length; i++) {
			builder.append(arrayOfString[i]);
			if (separator != null && i != arrayOfString.length - 1) {
				builder.append(separator);
			}
		}
		return builder.toString();
	}

	public static Integer getFreePort() throws IOException {
		try (ServerSocket socket = new ServerSocket(0)) {
			socket.setReuseAddress(true);
			return socket.getLocalPort();
		}
	}
}
