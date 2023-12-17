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

public class StageSAXParser extends DefaultHandler {

    private List<Star> myStageStars;

    private String tempVal;

    //to maintain context
    private Star tempStar;

    public StageSAXParser() {
        myStageStars = new ArrayList<Star>();
    }

    public void runExample() {
        parseDocument();
        //printData();
    }

    private void parseDocument() {

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse("casts124.xml", this);

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

        System.out.println("No of Movies '" + myStageStars.size() + "'.");

        Iterator<Star> it = myStageStars.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().toString());
        }
    }

    public List<Star> getData() {
        return myStageStars;
    }

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("m")) {
            tempStar = new Star();

        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        // Adds new Stage Name Star to array
        if (qName.equalsIgnoreCase("m")) {
            //add it to the list
            myStageStars.add(tempStar);
        } else if (qName.equalsIgnoreCase("a")) {   // Stage Name found
            tempStar.setStageName(tempVal);
        } else if (qName.equalsIgnoreCase("f")) {    // Movie acted in found
            tempStar.setMovieId(tempVal);
        }
    }
}