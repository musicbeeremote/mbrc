package kelsos.mbremote.Network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import android.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class NetworkManager extends Service {

	private Socket _cSocket = new Socket();
	private PrintWriter _output;
	private BufferedReader _input;
	private AnswerHandler _handler;
	private final IBinder _mBinder = new LocalBinder();

	public class LocalBinder extends Binder {
		public NetworkManager getService()
		{
			return NetworkManager.this;
		}
	}
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return _mBinder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		_cSocket = new Socket();
		_handler = new AnswerHandler();
	}

	public void onStart(Intent intent, int startId)
	{
		super.onStart(intent, startId);
		Toast.makeText(this, "SocketService Started", Toast.LENGTH_LONG).show();
		Runnable connect = new connectSocket();
		new Thread(connect).start();
	}

	public void sendData(String sendData) {
		try {
			_output.println(sendData);
		} catch (Exception e) {
			Log.e("SendData", "Failed", e);
		}
	}

	class connectSocket implements Runnable {

		public void run(){
			SocketAddress socketAddress = new InetSocketAddress("192.168.110.100", 3000);
			try {
				_cSocket.connect(socketAddress);
				_output = new PrintWriter(new BufferedWriter(
						new OutputStreamWriter(_cSocket.getOutputStream())),true);
				_input = new BufferedReader(new InputStreamReader(
						_cSocket.getInputStream()));
				while (_cSocket.isConnected()) {
					try {
						Log.d("ServerInput", "next stop");
						_handler.answerProcessor(_input.readLine());
					}catch (IOException e)
					{
						Log.e("MessageListening", "Failure",e);
					}
				}
			}
			catch (Exception e) {
				Log.e("Socket Connection", "Failure",e);
			}
		} 
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		try{
			_cSocket.close();
		}catch (IOException e) {
			Log.e("Socket Close", "Failure",e);
		}
		_cSocket=null;
	}
}
