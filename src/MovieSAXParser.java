
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

public class MovieSAXParser extends DefaultHandler {
    List<Movie> myMovies;
    private String tempVal;
    //to maintain context
    private Movie tempMovie;
    private String currentDirector;
    private int size;

    public MovieSAXParser() {
        myMovies = new ArrayList<Movie>();
    }

    public void runExample() {
        parseDocument();
    }

    private void parseDocument() {

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse("mains243.xml", this);

        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    public List<Movie> getData() {
        return myMovies;
    }

    /**
     * Iterate through the list and print
     * the contents
     */
    private void printData() {
        Iterator<Movie> it = myMovies.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().toString());
        }
        System.out.println("No of Movies '" + myMovies.size() + "'.");
    }

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("film")) {
            tempMovie = new Movie();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (qName.equalsIgnoreCase("film")) {
            myMovies.add(tempMovie);
        } else if (qName.equalsIgnoreCase("t")) {
            tempMovie.setTitle(tempVal);
        } else if (qName.equalsIgnoreCase("year")) {
            try{
                tempMovie.setYear(Integer.parseInt(tempVal));
            } catch(Exception e) {
                tempMovie.setYear(0);
            }
        } else if (qName.equalsIgnoreCase("fid")) {
            tempMovie.setId(tempVal);
        } else if (qName.equalsIgnoreCase("dirn")){
            tempMovie.setDirector(tempVal);
        } else if (qName.equalsIgnoreCase("cat")){
            tempMovie.addGenre(tempVal);
        }
    }

    public int length(){ return size; }
}
