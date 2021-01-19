package com.example.studienarbeit_geocaching_app_albert_hahn;

import android.os.Environment;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

/**
 * XML class that extends GeocacheMap retrieves filename and GeocacheModel list
 * and creates a xml structured file that will be exported as gpx with name and location of geocaches
 */

public class XmlHelper extends GeocacheMap  {

    /**
     * creates a gpx file with a XML layout
     * @param filename that is been given by the user
     * @param list GeocacheModel list to look up for location data and name
     * @return a gpx file that can be used in applications with gpx support
     */

    public boolean createGPX(String filename, List <GeocacheModel> list)
    {

        filename = filename + ".gpx";

        try {

            // Gets the external storage path of Download
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"Download" +"/"+filename);
            file.createNewFile();

            // creates new file on given path
            FileOutputStream fileos = new FileOutputStream(file);
            // Initialize xml serializer to write a xml document
            XmlSerializer xmlSerializer = Xml.newSerializer();
            StringWriter writer = new StringWriter();
            xmlSerializer.setOutput(writer);
            xmlSerializer.startDocument("UTF-8", false);

            /**
             * Hardcoded gpx file structure
             */
            xmlSerializer.startTag(null, "gpx");
            xmlSerializer.attribute(null, "xmlns","http://www.topografix.com/GPX/1/1");
            xmlSerializer.attribute(null, "creator","USERNAME");
            xmlSerializer.attribute(null, "version","1.1");
            xmlSerializer.attribute(null, "xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");
            xmlSerializer.attribute(null, "xsi:schemaLocation","http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd");

            if(list!=null) {
                // takes location data, geocache name and username and writes it into the XML document
                for (int i = 0; i < list.size(); i++) {

                    xmlSerializer.startTag(null, "wpt");
                    xmlSerializer.attribute(null, "lat",String.valueOf(list.get(i).getlatitude()));
                    xmlSerializer.attribute(null, "lon",String.valueOf(list.get(i).getlongitude()));

                    xmlSerializer.startTag(null, "name");
                    xmlSerializer.text(list.get(i).getName());
                    xmlSerializer.endTag(null, "name");

                    xmlSerializer.startTag(null, "UserName");
                    xmlSerializer.text(list.get(i).getUserName());
                    xmlSerializer.endTag(null, "UserName");

                    xmlSerializer.endTag(null, "wpt");
                }
            }
            else{
                return false;
            }
            // end of the document
            xmlSerializer.endTag(null, "gpx");
            xmlSerializer.endDocument();
            xmlSerializer.flush();
            // writes it onto the file that was created above
            String dataWrite = writer.toString();
            fileos.write(dataWrite.getBytes());
            fileos.close();
            Log.d("XML", "Successful");
            return true;
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        catch (IllegalStateException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

}
