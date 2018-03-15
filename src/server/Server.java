package server;

import java.io.IOException;
import java.net.ServerSocket;

public class Server
{
	// Connection info
	private int portMin = 4473;
	private int portMax = 4483;
	private int port = portMin;
	
	public void initiate()
	{
		boolean tryingToCreateSocket = true;
		while (tryingToCreateSocket)
		{
			try
			{
				ServerSocket serverSocket = new ServerSocket(port);
				System.out.println("Server started on port " +port);
				tryingToCreateSocket = false;
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
	}
	
	public void run()
	{
		initiate();
		
		while (!Thread.interrupted())
		{
			try
			{
				socket = serverSocket.accept();
				establishStreams(socket);
				
			}
			catch (IOException e)
			{
				System.out.println("Accept failed! Message lost!");
			}
			
		}
	}
}
