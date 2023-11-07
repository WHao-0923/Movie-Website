import java.io.*;
import java.util.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;

public class SAXParserStars extends DefaultHandler {

    private StringBuilder charactersBuffer = new StringBuilder();
    List<Star> myStars;

    Set<Star> duplicates = new HashSet<>();

    private String tempVal;

    //to maintain context
    private Star tempStar;

    public SAXParserStars() {
        myStars = new ArrayList<Star>();
    }

    public void runUtils() {
        parseDocument();
        printData();
    }

    private void parseDocument() {

        //get a factory
        SAXParserFactory spf_star = SAXParserFactory.newInstance();
        try {
            // Cast parser
            SAXParser sp_star = spf_star.newSAXParser();
            //parse the file and make sure the encoding is ISO-8859-1
            InputStream inputStream = new FileInputStream("./xml/actors63.xml");
            Reader reader = new InputStreamReader(inputStream, "ISO-8859-1");
            InputSource inputSource = new InputSource(reader);
            sp_star.parse(inputSource, this);
            //parse the file and also register this class for call backs



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

        System.out.println("No. of Stars '" + myStars.size() + "'.");

        Iterator<Star> it = myStars.iterator();
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
        if (qName.equalsIgnoreCase("actor")) {
            tempStar = new Star();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        charactersBuffer.append(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {

        tempVal = charactersBuffer.toString().trim(); // Use the charactersBuffer content

        if (qName.equalsIgnoreCase("actor")) {
            // Add the complete movie to the list
            if (!duplicates.contains(tempStar))
            {
                myStars.add(tempStar);
                duplicates.add(tempStar);
            }
            else {
                System.out.println("Duplicate star found: " + tempStar.toString());
            }
        } else if (qName.equalsIgnoreCase("stagename")) {
            tempStar.setStagename(tempVal);
        } else if (qName.equalsIgnoreCase("familyname")) {
            tempStar.setLast_name(tempVal);
        } else if (qName.equalsIgnoreCase("firstname")) {
            tempStar.setFirst_name(tempVal);
        } else if (qName.equalsIgnoreCase("dob")) {
            try{
                tempStar.setDob(Integer.parseInt(tempVal));
            } catch (NumberFormatException e){
                tempStar.setDob(-1);
            }

        }
        charactersBuffer.setLength(0);

    }

    public static void main(String[] args) {
        SAXParserStars spe = new SAXParserStars();
        spe.runUtils();
    }

}