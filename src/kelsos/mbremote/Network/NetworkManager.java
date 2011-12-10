package kelsos.mbremote.Network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import android.util.Log;

public class NetworkManager implements Runnable {

	private boolean _connected;
	private Socket _clientSocket;
	private PrintWriter _output;
	private BufferedReader _input;

	public void run() {
		try {
			this.connect("192.168.110.100", 3000);
			_connected = true;
			_output = new PrintWriter(new BufferedWriter(
					new OutputStreamWriter(_clientSocket.getOutputStream())),
					true);
			_input = new BufferedReader(new InputStreamReader(
					_clientSocket.getInputStream()));
			while (_connected) {
				try {
					this.sendData("test");
					Log.d("ServerInput", _input.readLine());
				} catch (Exception e) {
					Log.e("ClientActivity", "S: Error", e);
				}
			}
			close();
			Log.d("ClientActivity", "C: Closed.");
		} catch (Exception e) {
			Log.e("ClientActivity", "C: Error", e);
			_connected = false;
		}
	}

	public void sendData(String sendData) {
		try {
			_output.println(sendData);
		} catch (Exception e) {
			Log.e("SendData", "Failed", e);
		}
	}

	public void connect(String hostName, int portNumber) {
		try {
			Log.d("Connection Phase", "Connecting");
			InetAddress serverAddress = InetAddress.getByName(hostName);
			_clientSocket = new Socket(serverAddress, portNumber);
		} catch (Exception e) {
			Log.e("SocketConnect", "Connection Failed:", e);
			_connected = false;
		}
	}

	public void close() {
		try {
			if (_clientSocket.isConnected())
			{
				_input.close();
				_output.flush();
				_output.close();
				_clientSocket.close();
			}
		} catch (Exception e) {
			Log.e("SocketClosed", "Close Failed:", e);
		}
	}
}
