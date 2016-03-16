package jp.mzw.revajaxmutator.tracer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import jp.mzw.ajaxmutator.generator.MutationFileInformation;

public class MutationResultParser {
	String mFilename;
	String mDirname;
	
    private List<String> mMutationTitles;
    private Map<String, List<MutationFileInformation>> mMutationFiles;

    
	public MutationResultParser(String dirname, String filename) {
		mDirname = dirname;
		mFilename = filename;
		
		mMutationTitles = new ArrayList<String>();
		mMutationFiles = new HashMap<String, List<MutationFileInformation>>();
	}
	
    public void parse() throws IOException {
        List<String> lines = FileUtils.readLines(new File(mDirname, mFilename));
        String title = null;
        for (String line: lines) {
            String[] elms = line.split(",");
            if (elms.length == 2) {
                title = elms[0];
                mMutationTitles.add(title);
                mMutationFiles.put(title, new ArrayList<MutationFileInformation>());
                continue;
            }
            mMutationFiles.get(title).add(new MutationFileInformation(
                    elms[0], elms[2], MutationFileInformation.State.fromString(elms[1])));
        }
    }

    /**
     * @return return of names that represents classes of mutations.
     */
    public List<String> getListOfMutationName() {
        return mMutationTitles;
    }

    /**
     * @param name String that represents an class of mutation. See {@link #getListOfMutationName()}.
     * @return List of mutation file which belong to given class.
     */
    public List<MutationFileInformation> getMutationFileInformationList(String name) {
        return mMutationFiles.get(name);
    }

    public int getNumberOfUnkilledMutants() {
        int total = 0;
        for (List<MutationFileInformation> fileInfoList: mMutationFiles.values()) {
            for (MutationFileInformation fileInfo: fileInfoList) {
                if (fileInfo.getState() == MutationFileInformation.State.NON_EQUIVALENT_LIVE) {
                    total++;
                }
            }
        }
        return total;
    }

}
