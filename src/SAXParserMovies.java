import java.io.*;
import java.util.*;

import javax.xml.parsers.*;

import org.w3c.dom.Document;
import org.xml.sax.*;

import org.xml.sax.helpers.DefaultHandler;

public class SAXParserMovies extends DefaultHandler {

    List<Movie> myMovies;

    private String tempVal;

    //to maintain context
    private Movie tempMov;

    Set<Movie> duplicates = new HashSet<>();

    private StringBuilder charactersBuffer = new StringBuilder();
    private boolean inDirectorElement = false;
    private boolean insideYear = false;
    private boolean ignoreYearCharacters = false;

    public SAXParserMovies() {
        myMovies = new ArrayList<Movie>();
    }

    public void runUtils() {
        parseDocument();
        printData();
    }

    private void parseDocument() {

        //get a factory
        SAXParserFactory spf_movie = SAXParserFactory.newInstance();
        SAXParserFactory spf_cast = SAXParserFactory.newInstance();
        try {

            // Mains parser
            SAXParser sp_movie = spf_movie.newSAXParser();

            //parse the file and make sure the encoding is ISO-8859-1
            InputStream inputStream = new FileInputStream("./xml/mains243.xml");
            Reader reader = new InputStreamReader(inputStream, "ISO-8859-1");
            InputSource inputSource = new InputSource(reader);
            sp_movie.parse(inputSource, this);
            //sp_movie.parse("./mains243.xml", this);

        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    /**
     * Iterate through the list and print
     * the contents
     */
    private void printData() {

        System.out.println("No. of Movies '" + myMovies.size() + "'.");

        Iterator<Movie> it = myMovies.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().toString());
        }
    }

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
//        //reset
//        tempVal = "";
//        if (qName.equalsIgnoreCase("film")) {
//            //create a new instance of employee
//            tempMov = new Movie();
//            //tempMov.setCat(attributes.getValue("cat"));
//        }
        charactersBuffer.setLength(0); // Clear the characters buffer
        if (qName.equalsIgnoreCase("film")) {
            tempMov = new Movie();
        } else if (qName.equalsIgnoreCase("dir")) {
            inDirectorElement = true;
        } else if (qName.equalsIgnoreCase("year")) {
            insideYear = true; // Set flag to true when entering a year tag
        } else if (insideYear && qName.equalsIgnoreCase("released")) {
            ignoreYearCharacters = true; // Set flag to ignore characters when inside released tag
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        charactersBuffer.append(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {

        tempVal = charactersBuffer.toString().trim(); // Use the charactersBuffer content

        if (qName.equalsIgnoreCase("film")) {
            // Add the complete movie to the list
            if (!duplicates.contains(tempMov)) {
                myMovies.add(tempMov);
                duplicates.add(tempMov);
            } else {
                System.out.println("Duplicated movie found: " + tempMov.toString());
            }

        } else if (qName.equalsIgnoreCase("t")) {
            tempMov.setTitle(tempVal);
        } else if (qName.equalsIgnoreCase("fid")) {
            tempMov.setId(tempVal);
        } else if (qName.equalsIgnoreCase("released")) {
            // Set the year from the <released> tag and reset the flag
            try {
                tempMov.setYear(Integer.parseInt(tempVal.trim()));
            } catch (NumberFormatException e) {
                tempMov.setYear(-1);
            }
            ignoreYearCharacters = false;
        } else if (qName.equalsIgnoreCase("year")) {
            if (!ignoreYearCharacters) {
                try {
                    tempMov.setYear(Integer.parseInt(tempVal.trim()));
                } catch (NumberFormatException e) {
                    tempMov.setYear(-1); // or some error value to indicate parsing failed
                }
            }
            // Reset the flag
            ignoreYearCharacters = false;
        } else if (qName.equalsIgnoreCase("cat")) {
            tempMov.setCat(tempVal);
        } else if (qName.equalsIgnoreCase("dirn") && inDirectorElement) {
            // Assuming you want the name of the director, not the code
            tempMov.getDirectors().add(tempVal);
            inDirectorElement = false;
        }
        charactersBuffer.setLength(0);

    }

    public static void validateXML() {
        try {
            // Create a factory builder
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            // Enable DTD validation
            factory.setValidating(true);

            // Create a document builder
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Set error handler for validation errors
            builder.setErrorHandler(new ErrorHandler() {
                @Override
                public void warning(SAXParseException exception) throws SAXException {
                    System.out.println("WARNING: " + exception.getMessage());
                }

                @Override
                public void error(SAXParseException exception) throws SAXException {
                    System.out.println("ERROR: " + exception.getMessage());
                    throw exception; // Throw exception to stop parsing on errors
                }

                @Override
                public void fatalError(SAXParseException exception) throws SAXException {
                    System.out.println("FATAL ERROR: " + exception.getMessage());
                    throw exception; // Throw exception to stop parsing on fatal errors
                }
            });

            // Parse the XML file
            Document document = builder.parse("./xml/mains243.xml");

            // If no exception was thrown, the XML is well-formed and valid against the DTD
            System.out.println("mains243.xml is valid.");

        } catch (SAXException e) {
            // Handles the situation where the XML file is NOT valid
            System.out.println("Validation error: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SAXParserMovies spe = new SAXParserMovies();
        spe.runUtils();
        validateXML();
    }

}