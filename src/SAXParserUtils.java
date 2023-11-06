import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;

public class SAXParserUtils extends DefaultHandler {

    List<Movie> myMovies;

    private String tempVal;

    //to maintain context
    private Movie tempMov;

    private StringBuilder charactersBuffer = new StringBuilder();
    private boolean inDirectorElement = false;
    private boolean insideYear = false;
    private boolean ignoreYearCharacters = false;

    public SAXParserUtils() {
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
            // Cast parser
            SAXParser sp_cast = spf_cast.newSAXParser();

            //parse the file and also register this class for call backs
            sp_movie.parse("./mains243.xml", this);
            sp_cast.parse("./casts124.xml", this);

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
            myMovies.add(tempMov);
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

    public static void main(String[] args) {
        SAXParserUtils spe = new SAXParserUtils();
        spe.runUtils();
    }

}