package com.kelsos.mbrc.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.Others.Const;
import com.kelsos.mbrc.Others.DelayTimer;
import com.kelsos.mbrc.Others.SettingsManager;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.enums.SocketAction;
import com.kelsos.mbrc.enums.SocketServiceEventType;
import com.kelsos.mbrc.events.RawSocketDataEvent;
import com.kelsos.mbrc.messaging.NotificationService;
import com.squareup.otto.Bus;

import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

@Singleton
public class SocketService
{

	private Bus bus;
	private SettingsManager settingsManager;
	private NotificationService notificationService;

	private static int _numberOfTries;
	public static final int MAX_ALLOWED_RETRIES = 8;

	private Socket _cSocket;

	private PrintWriter _output;

	private Thread _connectionThread;

	private DelayTimer _connectionTimer;

	@Inject
	public SocketService(SettingsManager settingsManager, NotificationService notificationService, Bus bus)
	{
		this.bus = bus;
		this.settingsManager = settingsManager;
		this.notificationService = notificationService;

		_connectionTimer = new DelayTimer(2500, timerFinishEvent);
		_numberOfTries = 0;
		SocketManager(SocketAction.SOCKET_START);
	}

	private DelayTimer.TimerFinishEvent timerFinishEvent = new DelayTimer.TimerFinishEvent()
	{
		public void onTimerFinish()
		{
			_connectionThread = new Thread(new socketConnection());
			_connectionThread.start();
			_numberOfTries++;
		}
	};

	private boolean connectionThreadExistsAndIsAlive()
	{
		return _connectionThread != null && _connectionThread.isAlive();
	}

	public void SocketManager(SocketAction action)
	{
		switch (action)
		{
			case SOCKET_RESET:

				if (socketExistsAndIsConnected())
				{
					try
					{
						if (_output != null)
						{
							_output.flush();
							_output.close();
							_output = null;
						}
						_cSocket.close();
						_cSocket = null;
					} catch (IOException ignore)
					{

					}
				}
				if (_connectionThread != null) _connectionThread.interrupt();
				_connectionThread = null;
				_connectionTimer.start();
				break;
			case SOCKET_START:
				if (socketExistsAndIsConnected() || connectionThreadExistsAndIsAlive())
					return;
				else if (!socketExistsAndIsConnected() && connectionThreadExistsAndIsAlive())
				{
					_connectionThread.interrupt();
					_connectionThread = null;
				}
				_connectionTimer.start();
				break;
			case RETRY_COUNTER_RESET:
				_numberOfTries = 0;
				break;
			case SOCKET_STOP:
				if (socketExistsAndIsConnected())
				{
					try
					{
						if (_output != null)
						{
							_output.flush();
							_output.close();
							_output = null;
						}
						_cSocket.close();
						_cSocket = null;
					} catch (IOException ignore)
					{

					}
				}
				if (_connectionThread != null) _connectionThread.interrupt();
				_connectionThread = null;
				_numberOfTries = 0;
				_connectionTimer.stop();
				break;
		}
	}

	/**
	 * Returns true if the socket is not null and it is connected, false in any other case.
	 *
	 * @return Boolean
	 */
	private boolean socketExistsAndIsConnected()
	{
		return _cSocket != null && _cSocket.isConnected();
	}

	public void sendData(String data)
	{
		try
		{
			if (socketExistsAndIsConnected())
				_output.println(data + Const.NEWLINE);
		} catch (Exception ignored)
		{
		}
	}

	public void informEventBus(final RawSocketDataEvent event)
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				bus.post(event);
			}
		}).start();
	}

	private class socketConnection implements Runnable
	{
		public void run()
		{
			SocketAddress socketAddress = settingsManager.getSocketAddress();
			informEventBus(new RawSocketDataEvent(SocketServiceEventType.SOCKET_EVENT_HANDSHAKE_UPDATE, "false"));
			if (null == socketAddress) return;
			BufferedReader _input;
			try
			{
				_cSocket = new Socket();
				_cSocket.connect(socketAddress);
				_output = new PrintWriter(new BufferedWriter(new OutputStreamWriter(_cSocket.getOutputStream()), 4096), true);
				_input = new BufferedReader(new InputStreamReader(_cSocket.getInputStream()), 4096);

				String socketStatus = String.valueOf(_cSocket.isConnected());

				informEventBus(new RawSocketDataEvent(SocketServiceEventType.SOCKET_EVENT_STATUS_CHANGE, socketStatus));
				while (_cSocket.isConnected())
				{
					try
					{
						final String incoming = _input.readLine();
						if (incoming == null) throw new IOException();
							informEventBus(new RawSocketDataEvent(SocketServiceEventType.SOCKET_EVENT_PACKET_AVAILABLE, incoming));
					} catch (IOException e)
					{
						_input.close();
						_cSocket.close();
						throw e;
					}
				}
			} catch (SocketTimeoutException e)
			{
				notificationService.showToastMessage(R.string.notification_connection_timeout);
			} catch (SocketException e)
			{
				final String exceptionMessage = e.toString().substring(26);
				notificationService.showToastMessage(exceptionMessage);
			} catch (IOException ignored)
			{
			} catch (NullPointerException ignored)
			{
			} finally
			{
				if (_output != null)
				{
					_output.flush();
					_output.close();
				}
				_cSocket = null;

				informEventBus(new RawSocketDataEvent(SocketServiceEventType.SOCKET_EVENT_STATUS_CHANGE, "false"));
				if (_numberOfTries < MAX_ALLOWED_RETRIES) SocketManager(SocketAction.SOCKET_RESET);
			}
		}
	}
}
