package kelsos.mbremote.Network;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AnswerHandler{
	private Context context;
	private DocumentBuilderFactory dbf;
	private DocumentBuilder db;
	private Document doc;
	
	//Intents
	public final static String VOLUME_DATA = "kelsos.mbremote.actions.VOLUME_DATA";
	
	public AnswerHandler(){
		dbf = DocumentBuilderFactory.newInstance();
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setContext(Context context)
	{
		this.context=context;
	}
	
	public void answerProcessor(String answer){
		try {
			String[] replies = answer.split("\0");
			for(int i = 0; i<replies.length; i++)
			{
				doc = db.parse(new ByteArrayInputStream(replies[i].getBytes("UTF-8")));
				Node xmlNode = doc.getFirstChild();
				Log.d("NodeName:",xmlNode.getNodeName());
				if (xmlNode.getNodeName().contains("playPause"))
				{
					Log.d("Reply Received","<playPause>");
				}
				else if (xmlNode.getNodeName().contains("next"))
				{
					Log.d("Reply Received","<next>");
				}
				else if (xmlNode.getNodeName().contains("volume"))
				{
					Intent volumeDataIntent = new Intent();
					volumeDataIntent.setAction(VOLUME_DATA);
					volumeDataIntent.putExtra("data", Integer.parseInt(xmlNode.getTextContent()));
					//volumeDataIntent.setClassName("kelsos.mbremote", "kelsos.mbremote.AndroidRemoteforMusicBeeActivity");
					
					context.sendBroadcast(volumeDataIntent);
				}
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
