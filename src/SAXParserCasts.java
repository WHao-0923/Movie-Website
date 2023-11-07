import java.io.*;
import java.util.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;

public class SAXParserCasts extends DefaultHandler {

    private StringBuilder charactersBuffer = new StringBuilder();
    List<Cast> myCasts;

    Set<Cast> duplicates = new HashSet<>();

    private String tempVal;

    //to maintain context
    private Cast tempCast;

    public SAXParserCasts() {
        myCasts = new ArrayList<Cast>();
    }

    public void runUtils() {
        parseDocument();
        printData();
    }

    private void parseDocument() {

        //get a factory
        SAXParserFactory spf_cast = SAXParserFactory.newInstance();
        try {
            // Cast parser
            SAXParser sp_cast = spf_cast.newSAXParser();

            //parse the file and make sure the encoding is ISO-8859-1
            InputStream inputStream = new FileInputStream("./xml/casts124.xml");
            Reader reader = new InputStreamReader(inputStream, "ISO-8859-1");
            InputSource inputSource = new InputSource(reader);
            sp_cast.parse(inputSource, this);

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

        System.out.println("No. of Casts '" + myCasts.size() + "'.");

        Iterator<Cast> it = myCasts.iterator();
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
        if (qName.equalsIgnoreCase("filmc")) {
            tempCast = new Cast();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        charactersBuffer.append(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {

        tempVal = charactersBuffer.toString().trim(); // Use the charactersBuffer content

        if (qName.equalsIgnoreCase("filmc")) {
            if (!duplicates.contains(tempCast)){
                // Add the complete movie to the list
                myCasts.add(tempCast);
                duplicates.add(tempCast);
            } else {
                System.out.println("Duplicate cast found: " + tempCast.toString());
            }

        } else if (qName.equalsIgnoreCase("f")) {
            tempCast.setFilm_id(tempVal);
        } else if (qName.equalsIgnoreCase("a")) {
            tempCast.getStage_name().add(tempVal);
        }
        charactersBuffer.setLength(0);

    }

    public static void main(String[] args) {
        SAXParserCasts spe = new SAXParserCasts();
        spe.runUtils();
    }

}