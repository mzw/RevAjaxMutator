package jp.mzw.ajaxmutator.viewer;

import com.google.common.io.Files;
import jp.mzw.ajaxmutator.generator.MutationFileInformation;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class CellItemForMutant implements CellItem {
    private MutationFileInformation mutationFileInformation;

    public CellItemForMutant(MutationFileInformation mutationFileInformation) {
        this.mutationFileInformation = mutationFileInformation;
    }

    @Override
    public String getDisplayName() {
        return "#" + mutationFileInformation.getFileName().replace("mutant", "").replace(".diff", "");
    }

    public MutationFileInformation.State getState() {
        return mutationFileInformation.getState();
    }

    public void setState(MutationFileInformation.State state) {
        mutationFileInformation.setState(state);
    }

    public String getContent() {
        try {
            return Files.toString(new File(mutationFileInformation.getAbsolutePath()), Charset.defaultCharset());
        } catch (IOException e) {
            return "Failed to load " + mutationFileInformation.getAbsolutePath();
        }
    }
}
