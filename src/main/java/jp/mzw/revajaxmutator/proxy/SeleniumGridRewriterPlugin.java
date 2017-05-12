package jp.mzw.revajaxmutator.proxy;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.owasp.webscarab.model.Request;

/**
 * Plugin for the proxy running on the worker node in the Selenium grid. When
 * the worker node runs the test and requests the .js file, the proxy replaces
 * the response with the mutant file. This mutant file is sent from the client
 * to the worker using the Selenium grid's upload functionality and stored in
 * the "tmp" folder.
 *
 * @author filipe
 *
 */
public class SeleniumGridRewriterPlugin extends RewriterPlugin {

	public static final String SEL_GRID_PROXY_IP = "127.0.0.1";
	public static final String SEL_GRID_PROXY_PORT = "42795";
	public static final String SEL_GRID_PROXY_ADDRESS = SEL_GRID_PROXY_IP + ":" + SEL_GRID_PROXY_PORT;

	public SeleniumGridRewriterPlugin() {
		this(System.getProperty("java.io.tmpdir"));
	}

	public SeleniumGridRewriterPlugin(String root) {
		super(root);
	}

	@Override
	public String getPluginName() {
		return "SeleniumGridRewriterPlugin";
	}

	@Override
	protected String checkIfRequestIsForTargetJsFile(final Request request) throws UnsupportedEncodingException {
		// Get the .js filename from the cookie header
		final String[] cookies = request.getHeaders("Cookie");
		if (cookies == null) {
			return null;
		}
		final String jsMutantRegex = "jsMutantFilename=[^;]+";
		final Pattern jsMutantPattern = Pattern.compile(jsMutantRegex);
		final Matcher jsMutantMatcher = jsMutantPattern.matcher(cookies[0]);
		String filename = null;
		if (jsMutantMatcher.find()) {
			filename = jsMutantMatcher.group().split("=")[1];
		}

		// Check if the .js file is being requested in the url
		final String url = URLEncoder.encode(request.getURL().toString(), "utf-8");
		final Pattern findMutantInURLPattern = Pattern.compile(filename);
		final Matcher findMutantInURLMatcher = findMutantInURLPattern.matcher(url);
		if (findMutantInURLMatcher.find()) {
			return filename;
		}

		return null;
	}

	@Override
	protected BufferedInputStream findMutantFile(Request request, String regex, String mutantExt)
			throws FileNotFoundException {
		try {
			final String filename = regex + mutantExt;
			return new BufferedInputStream(new FileInputStream(
					this.findTransferredMutatedFile(filename, new File(this.mDirname)).getAbsolutePath()));
		} catch (final IOException e) {
			e.printStackTrace();
		}

		throw new FileNotFoundException();
	}

	private File findTransferredMutatedFile(String filename, File root) throws IOException {
		final Collection<File> files = FileUtils.listFiles(root, null, true);

		for (final Iterator<File> iterator = files.iterator(); iterator.hasNext();) {
			final File file = iterator.next();
			if (file.isDirectory()) {
				return this.findTransferredMutatedFile(filename, file);
			} else {
				final Pattern findFilePattern = Pattern.compile(filename);
				final Matcher findFileMatcher = findFilePattern.matcher(file.getName());
				if (findFileMatcher.find()) {
					return file;
				}
			}
		}
		return null;
	}
}
