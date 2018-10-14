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
                    "    <link rel=\"stylesheet\" type=\"text/css\" href=\"../css/main.css\"/>\n" +
                    "</head><body>\n" +
                    "<h1>List of Trading Cards</h1>\n";

    private static final String tableHead = "<table>";
    private static final String templateEnd = "List Created: " + new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date()) + "</body></html>";

    private static final String[] listOfFiles = getFileNamesFromDirectory();

    public static void main(String[] args) throws IOException {



        // create file or use existing file in filesystem
        createTargetFile();
        // add document header as first part of the file content
        addTemplateComponent(templateBegin, false);


        int counterAll = 0;
        for (final String listOfFile : listOfFiles) {

            final String sourceFile = pathSource + listOfFile + ".html";
            counterAll = appendFileContent(sourceFile, listOfFile, counterAll);
        }
        addTemplateComponent(templateEnd, true);
    }

    private static void formatFile( String[] listOfFiles){

        final File folder = new File(pathSource);
        final File[] listOfFilesInDirectory = folder.listFiles();


        final List<String> result = new ArrayList<String>() {
        };

        assert listOfFilesInDirectory != null;
        Arrays.sort(listOfFilesInDirectory);
        for (File aListOfFilesInDirectory : listOfFilesInDirectory) {
            if (aListOfFilesInDirectory.isFile()) {
                System.out.println("File: " + aListOfFilesInDirectory.getName());


            }
        }

    }



    private static String[] getFileNamesFromDirectory() {
        //File directory = new File("./");
        //System.out.println(directory.getAbsolutePath());
        final File folder = new File(pathSource);
        final File[] listOfFilesInDirectory = folder.listFiles();


        final List<String> result = new ArrayList<String>() {
        };

        assert listOfFilesInDirectory != null;
        Arrays.sort(listOfFilesInDirectory);
        for (File aListOfFilesInDirectory : listOfFilesInDirectory) {
            if (aListOfFilesInDirectory.isFile()) {
                System.out.println("File: " + aListOfFilesInDirectory.getName().substring(0,aListOfFilesInDirectory.getName().lastIndexOf('.')));
                result.add(aListOfFilesInDirectory.getName().substring(0, aListOfFilesInDirectory.getName().lastIndexOf('.')));
            } else if (aListOfFilesInDirectory.isDirectory()) {
               System.out.println("Directory: " + aListOfFilesInDirectory.getName());
            }
        }

        return result.toArray(new String[0]);
    }


    /**
     * Method to create ar re use a file under the given target location (path in the files System)
     *
     * @throws IOException Java IO Exception
     */
    private static void createTargetFile() throws IOException {
        final File myFile = new File(FileGenerator.generatedFileLocation);

        if (myFile.createNewFile()) {
            System.out.println("A new File under: " + FileGenerator.generatedFileLocation + " was created ");
        } else {
            System.out.println("An existing File under: " + FileGenerator.generatedFileLocation + " was replaced ");
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
        out.flush();
        out.close();
    }

    private static int appendFileContent(final String source, final String name, int counterIn) throws IOException {
        final StringBuffer result = new StringBuffer("<h2>").append(name).append("</h2>").append('\n').append(tableHead);


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

            result.replace(offset, offset + 5, " [This Season: " + counter + " | Total: " + counterAll + "]</h2>");


            out.append(result.append('\n'));
            out.flush();
            return counterAll;

        }
    }
}


