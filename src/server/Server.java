package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server
{
	// Connection info
	private int portMin = 4473;
	private int portMax = 4483;
	private int port = portMin;
	
	private ServerSocket serverSocket;
	private Socket socket;
	
	public void initiate()
	{
		boolean tryingToCreateSocket = true;
		while (tryingToCreateSocket)
		{
			try
			{
				serverSocket = new ServerSocket(port);
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
		
		while (true)
		{
			try
			{
				socket = serverSocket.accept();
			}
			catch (IOException e)
			{
				System.out.println("Accept failed! Message lost!");
			}
			
		}
	}
	
	public static void main(String[] args)
	{
		Server server = new Server();
		server.run();
	}
}
