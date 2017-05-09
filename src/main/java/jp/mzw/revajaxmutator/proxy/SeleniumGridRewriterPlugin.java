package jp.mzw.revajaxmutator.proxy;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

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
	protected BufferedInputStream findMutantFile(Request request, String regex, String mutantExt)
			throws FileNotFoundException {
		try {
			return new BufferedInputStream(new FileInputStream(
					this.findTransferredMutatedFile(mutantExt, new File(this.mDirname)).getAbsolutePath()));
		} catch (final IOException e) {
			e.printStackTrace();
		}

		throw new FileNotFoundException();
	}

	private File findTransferredMutatedFile(String mutantExt, File root) throws IOException {
		final Collection<File> files = FileUtils.listFiles(root, null, true);

		for (final Iterator<File> iterator = files.iterator(); iterator.hasNext();) {
			final File file = iterator.next();
			if (file.isDirectory()) {
				return this.findTransferredMutatedFile(mutantExt, file);
			} else if (file.getName().endsWith(mutantExt)) {
				return file;
			}
		}

		return null;
	}
}
