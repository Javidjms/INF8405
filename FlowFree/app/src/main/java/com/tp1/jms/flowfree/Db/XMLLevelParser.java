package com.tp1.jms.flowfree.Db;



import android.content.Context;
import android.widget.Toast;

import com.tp1.jms.flowfree.Model.Flow;
import com.tp1.jms.flowfree.Model.Level;
import com.tp1.jms.flowfree.Model.Position;
import com.tp1.jms.flowfree.R;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


/**
 * This class is a custom XML Parser which will parse a specific xml file which contents all level details
 * Created by JMS on 01/02/2016.
 */
public class XMLLevelParser {

    private static final String LEVELS_PATH = "levels.xml";
    private static final String LEVEL_TAG = "level";
    private static final String LEVEL_ID_ATTR = "id";
    private static final String LEVEL_NUMBER_ATTR = "number";
    private static final String LEVEL_SIZE_TAG = "size";
    private static final String LEVEL_FLOW_TAG = "flow";
    private static final String LEVEL_FLOW_START_TAG = "start";
    private static final String LEVEL_FLOW_END_TAG = "end";
    private static final String LEVEL_FLOW_X_ATTR = "x";
    private static final String LEVEL_FLOW_Y_ATTR = "y";

    /**
     * Read all level on the xml file and return an arraylist of levels
     * @param context context of the current activity who load this class
     * @return arraylist of levels
     */
    public static ArrayList<Level> readLevels(Context context) {
        ArrayList<Level> levels = new ArrayList<Level>();
        int[] colors =context.getResources().getIntArray(R.array.flow_colors);
        InputStream inputstream = null;
        try {
            inputstream = context.getAssets().open(LEVELS_PATH);
        } catch (IOException e) {
            Toast.makeText(context, "Error while reading XML Levels File: " + LEVELS_PATH, Toast.LENGTH_SHORT).show();
        }

        try {
            //Build and parse the inputstream xml content
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputstream);
            //Get the first node level
            NodeList nodelist = doc.getElementsByTagName(LEVEL_TAG);
            for (int i = 0; i < nodelist.getLength(); i++) {
                Element levelElement = (Element) nodelist.item(i);
                int id = Integer.parseInt(levelElement.getAttribute(LEVEL_ID_ATTR));//Get the id of the level
                int number = Integer.parseInt(levelElement.getAttribute(LEVEL_NUMBER_ATTR));//Get the number of the level
                int size = Integer.parseInt(levelElement.getElementsByTagName(LEVEL_SIZE_TAG).item(0).getFirstChild().getNodeValue());//Get the size of the level

                NodeList flowslist = levelElement.getElementsByTagName(LEVEL_FLOW_TAG);//Get all flows
                ArrayList<Flow> flows = new ArrayList<Flow>();
                for (int j = 0; j < flowslist.getLength(); j++) {
                    Element flowElement = (Element) flowslist.item(j);
                    Element startElement = (Element) flowElement.getElementsByTagName(LEVEL_FLOW_START_TAG).item(0);
                    int xs = Integer.parseInt(startElement.getAttribute(LEVEL_FLOW_X_ATTR)) ;
                    int ys = Integer.parseInt(startElement.getAttribute(LEVEL_FLOW_Y_ATTR)) ;
                    Position start = new Position(xs,ys); //Get the startpoint of the flow

                    Element endElement = (Element) flowElement.getElementsByTagName(LEVEL_FLOW_END_TAG).item(0);
                    int xe = Integer.parseInt(endElement.getAttribute(LEVEL_FLOW_X_ATTR)) ;
                    int ye = Integer.parseInt(endElement.getAttribute(LEVEL_FLOW_Y_ATTR)) ;
                    Position end = new Position(xe,ye);
                    Flow flow = new Flow(start,end,colors[j]);//Get the endpoint of the flow
                    flows.add(flow);
                }
                Level level = new Level(id,flows,size,number);//Instance one complete level
                levels.add(level);//Add level to the arraylist
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return levels;
    }
}

