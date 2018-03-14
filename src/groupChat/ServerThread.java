package groupChat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread extends Thread
{
	private int portMin = 4473;
	private int portMax = 4483;
	private int port = portMin;
	private boolean attemptingToStart = true;
	Socket socket;
	ServerSocket serverSocket;
	
	public void run()
	{
		while (attemptingToStart)
		{
			try
			{
				ServerSocket serverSocket = new ServerSocket(port);
				System.out.println("Server started.");
			}
			catch (IOException e)
			{
				System.out.println("Port " + port + " failed!");
				if (port <= portMax)
				{
					port++;
				}
				else
				{
					port = portMin;
				}
			}
		}
		
		while (!Thread.interrupted())
		{
			try
			{
				socket = serverSocket.accept();
				Clients.add(socket);
			}
			catch (IOException e)
			{
				System.out.println("Accept failed on port");
			}
			
		}
	}
}
