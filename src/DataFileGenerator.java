import java.io.File;
/**
 * TODO: fill me :)
 * @author akolkar
 *
 */
public class DataFileGenerator {
    /**
     * TODO: fill me :)
     * @author akolkar
     *
     */
    public static void main(String[] args) {
        DataFileGenerator dfg = new DataFileGenerator();
        dfg.createFiles(dfg.getFileNames());
    }

    private String[] getFileNames() {
        String [] fnames = {"./reviews.txt",
                            "./pterms.txt",
                            "./rterms.txt",
                            "./scores.txt"};
        return fnames;
    }

    private void createFiles(String[] fileNames) {
        for(String filename : fileNames)
            new File(filename);
    }


}
