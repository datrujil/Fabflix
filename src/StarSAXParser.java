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
import java.util.UUID;



public class StarSAXParser extends DefaultHandler {

    List<Star> myStars;
    public List<Star> allStageStars;
    private final Object lock = new Object();
    private String tempVal;
    //to maintain context
    private Star tempStar;
    private int size;

    public StarSAXParser() {
        myStars = new ArrayList<Star>();
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
            sp.parse("actors63.xml", this);

        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    public List<Star> getData(){
        return myStars;
    }

//    private void printData() {
//        Iterator<Star> it = myStars.iterator();
//        while (it.hasNext()) {
//            Star currentStar = it.next();
//            Iterator<Star> it2 = allStageStars.iterator();
//            while (it2.hasNext()){
//                Star currentStage = it2.next();
//                if (currentStage.getStageName().equals(currentStar.getStageName())){
//                    currentStar = Star.mergeStageAndStar(currentStage, currentStar);
//                }
//            }
//        }
//
//        int count = 0;
//        Iterator<Star> finalStars = myStars.iterator();
//        while (finalStars.hasNext()) {
//            Star currStar = finalStars.next();
//            if (!currStar.getMoviesIdActed().isEmpty() && !currStar.getName().equals(" ")){
//                System.out.println(currStar.toString());
//                count++;
//            }
//        }
//        System.out.println(count);
//    }


    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("actor")) {
            tempStar = new Star();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        // Adds new Stage Name Star to array
        if (qName.equalsIgnoreCase("actor")) {
            String uniqueID = generateUniqueID();
            tempStar.setId(uniqueID);
            myStars.add(tempStar);
            tempStar = new Star();
        }
        if (qName.equalsIgnoreCase("stagename")) {   // Stage Name found
            tempStar.setStageName(tempVal);
        } else if (qName.equalsIgnoreCase("dob")) {    // Movie acted in found
            try{
                tempStar.setBirthYear(Integer.parseInt(tempVal));
            } catch(Exception e) {
                tempStar.setBirthYear(0);
            }
        } else if (qName.equalsIgnoreCase("familyname")){
            tempStar.setName(tempVal);
        } else if (qName.equalsIgnoreCase("firstname")){
            tempStar.setName(tempVal);
        }
    }

    public static String generateUniqueID() {
        // Generate a random UUID
        String randomUUID = UUID.randomUUID().toString().replace("-", "");

        // Get the current timestamp (in milliseconds)
        long timestamp = System.currentTimeMillis();

        // Concatenate components and take the last 10 characters
        String uniqueID = randomUUID.substring(0, Math.min(4, randomUUID.length()))
                + String.valueOf(timestamp)
                + randomUUID.substring(Math.max(0, randomUUID.length() - (10 - 4)), randomUUID.length());

        // Ensure the final ID is exactly 10 characters
        if (uniqueID.length() > 10) {
            uniqueID = uniqueID.substring(0, 10);
        }

        return uniqueID;
    }

    public int length(){ return size; }

}
