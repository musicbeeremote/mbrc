package kelsos.mbremote.Network;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import android.util.Log;

public class AnswerHandler {
	private DocumentBuilderFactory dbf;
	private DocumentBuilder db;
	private Document doc;
	
	public AnswerHandler() {
		dbf = DocumentBuilderFactory.newInstance();
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void answerProcessor(String answer){
		try {
			doc = db.parse(new ByteArrayInputStream(answer.getBytes("UTF-8")));
			Node xmlNode = doc.getFirstChild();
			Log.d("NodeName:",xmlNode.getNodeName());
			if (xmlNode.getNodeName().contains("playpause"))
			{
				Log.d("Oh I am here","really");
			}
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
