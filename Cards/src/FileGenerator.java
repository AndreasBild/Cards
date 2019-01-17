import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class FileGenerator {


    //private static final String pathSource = "./content/";
    //private static final String pathOutput = "./output/";

    private static final String pathSource = "./Cards/content/";
    private static final String pathOutput = "./Cards/output/";


    private static final String generatedFileLocation = pathOutput + "result.html";
    private static final String templateBegin =
            "<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head><meta charset=\"UTF-8\"><title>Juwan Howard Collection </title>\n" +
                    "      <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                    "      <meta name=\"Description\" content=\"List of all Basketball Trading Cards of former NBA Player Juwan Howard.Includes Autographs and serial numbered cards.\">\n" +
                    "    <link rel=\"stylesheet\" type=\"text/css\" href=\"../css/main.css\"/>\n" +
                    "</head><body>\n" +
                    "<h1><a name=\"top\">List of Trading Cards</a></h1>\n";

    private static final String tableHead = "<table>";
    private static final String templateEnd = "List Created: " + new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date()) + "</body></html>";

    private static final String[] nameOfInputFile = getFileNamesFromDirectory();

    public static void main(String[] args) throws IOException {

        //formatFile();

        // create file or use existing file in filesystem
        createTargetFile(generatedFileLocation);
        // add document header as first part of the file content
        addTemplateComponent(templateBegin, false);



        int counterAll = 0;
        for (final String fileName : nameOfInputFile) {

            final String sourceFile = pathSource + fileName + ".html";
            counterAll = appendFileContent(sourceFile, fileName, counterAll);

        }

        addTemplateComponent(templateEnd, true);
    }

    private static String createAnchorList() {

        StringBuilder internalAnchorList = new StringBuilder();

        for (String fileName : nameOfInputFile) {
            internalAnchorList.append(" | <a href=").append('#').append(fileName).append(">").append(fileName).append("</a> ");
        }
        internalAnchorList.append(" |");


        return internalAnchorList.toString();

    }
    private static void formatFile() {

        final File folder = new File(pathSource);
        final File[] listOfFilesInDirectory = folder.listFiles();

        assert listOfFilesInDirectory != null;
        Arrays.sort(listOfFilesInDirectory);
        for (File aListOfFilesInDirectory : listOfFilesInDirectory) {
            if (aListOfFilesInDirectory.isFile()) {
                final String fileName = aListOfFilesInDirectory.getName();
                System.out.println("FileName: " + fileName);
                try {
                    createTargetFile(pathOutput + fileName);
                    formatFileContent(pathSource + fileName, pathOutput + fileName);
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }

    }

    /**
     *
     * @return array of strings containing the names of files/subdirectories in the given base path.
     */
    private static String[] getFileNamesFromDirectory() {
        File directory = new File("./");
        System.out.println("################ AbsolutePath: " +directory.getAbsolutePath());
        final File folder = new File(pathSource);
        final File[] listOfFilesInDirectory = folder.listFiles();


        final List<String> result = new ArrayList<String>() {
        };

        assert listOfFilesInDirectory != null;
        Arrays.sort(listOfFilesInDirectory);
        for (File aListOfFilesInDirectory : listOfFilesInDirectory) {
            if (aListOfFilesInDirectory.isFile()) {
                System.out.println("File in Directory : " + aListOfFilesInDirectory.getName().substring(0, aListOfFilesInDirectory.getName().lastIndexOf('.')));
                result.add(aListOfFilesInDirectory.getName().substring(0, aListOfFilesInDirectory.getName().lastIndexOf('.')));
            } else if (aListOfFilesInDirectory.isDirectory()) {
                System.out.println("Subdirectory in Directory: " + aListOfFilesInDirectory.getName());
            }
        }

        return result.toArray(new String[0]);
    }


    /**
     * Method to create ar re use a file under the given target location (path in the files System)
     *
     * @throws IOException Java IO Exception
     */
    private static void createTargetFile(String fileName) throws IOException {
        final File myFile = new File(fileName);

        if (myFile.createNewFile()) {
            System.out.println("A new File under: " + fileName + " was created ");
        } else {
            System.out.println("An existing File under: " + fileName + " was replaced ");
        }
    }

    /**
     * Method for adding content to a given file. First a header with doctype definition is added. In a later step the rest of the content is appended at he end
     *
     * @param templateBegin  String, that contains the basic file header template
     * @param appendAtTheEnd boolean, that indicated if the provided content shall be added at the end or the beginning of the file
     * @throws IOException, thrown if a file operation did not work out as planned
     */
    private static void addTemplateComponent(String templateBegin, boolean appendAtTheEnd) throws IOException {
        PrintWriter printWriter = new PrintWriter(new java.io.FileWriter(FileGenerator.generatedFileLocation, appendAtTheEnd));

        final BufferedWriter out = new BufferedWriter(printWriter);

        out.append(templateBegin);
        out.append(createAnchorList());
        out.flush();
        out.close();
    }

    private static int appendFileContent(final String source, final String name, int counterIn) throws IOException {
        final String anchorName="<a name="+name+">"+name+"</a>";
        final StringBuffer result = new StringBuffer("<h2>").append(anchorName).append("</h2>").append('\n').append(tableHead);



        try (BufferedReader inputStream = new BufferedReader(new FileReader(source)); PrintWriter outputStream = new PrintWriter(new FileWriter(FileGenerator.generatedFileLocation, true))) {


            final BufferedWriter out = new BufferedWriter(outputStream);


            String line;
            int counterAll = counterIn; // keep track of all rows
            int counter = -1; // needs to be -1 because of the table header, we count only the content rows

            while ((line = inputStream.readLine()) != null) {
                //every line that is a row or column, goes to the resulting file
                if (line.contains("</t") || line.contains("<td") || line.contains("<tr") || line.contains("<th")) {
                    result.append(line.trim());
                    // we simply count the rows, but do not consider the first one as it is the table header
                    if (line.contains("<tr>")) {
                        ++counter;
                    }
                }
            }
            counterAll = counterAll + counter;


            //System.out.println(result);

            final int offset = result.lastIndexOf("</h2>");

            result.replace(offset, offset + 5, " [This Season: " + counter + " | Total: " + counterAll + "]</h2><a href=\"#top\">top</a>");


            out.append(result.append('\n'));
            out.flush();
            return counterAll;

        }
    }

    private static void formatFileContent(final String source, String target) throws IOException {
        final StringBuffer result = new StringBuffer();


        try (BufferedReader inputStream = new BufferedReader(new FileReader(source)); PrintWriter outputStream = new PrintWriter(new FileWriter(target, false))) {


            final BufferedWriter out = new BufferedWriter(outputStream);


            String line;
            int counter = -1; // needs to be -1 because of the table header, we count only the content rows

            while ((line = inputStream.readLine()) != null ) {
               if(line.isEmpty()){
                  return;
               }else if(line.contains("<table>")|| line.contains("</table>") || line.contains("<td>")||line.contains("</td>")||line.contains("<tr>")|| line.contains("<th>") ) {
                   result.append(line.trim()).append('\n');
               }else if(line.contains("</tr>")) {
                   result.append(line.trim()).append('\n');
                   ++counter;
               }else{
                   result.append(line.trim());
               }

            }


            System.out.println(result);
            final int offset;

            if (result.lastIndexOf("]</h2>")!=-1){
               offset=result.lastIndexOf("]</h2>");
               result.replace(offset, offset + 6, "]</h2>"+"\n");
            }else {

            offset = result.lastIndexOf("</h2>");
            result.replace(offset, offset + 5, " [Total: " + counter + "]</h2>"+"\n");
            }
            out.append(result.append('\n'));
            out.flush();


        }
    }
}


