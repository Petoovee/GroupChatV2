package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class Incoming extends Thread
{
	private ObjectInputStream inputStream;
	private Message receivedMessage;
	private Client client;
	private Socket socket;
	
	/*
	 * Waits for a message, when one is received, handle it properly, adding it to the right file and updating users
	 */
	
	public Incoming(Socket socket, Client client)
	{
		this.socket = socket;
		this.client = client;
		System.out.println("InputThread constructed");
	}
	
	public void constructStream()
	{
		try
		{
			inputStream = new ObjectInputStream(socket.getInputStream());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}		
	}
	
	public void run()
	{
		System.out.println("InputThread started");
		
		constructStream();
		System.out.println("InputThread: InputStream constructed");
		
		while (!Thread.interrupted())
		{
			try
			{
				System.out.println("InputThread: Trying to receive message");
				receivedMessage = (Message) inputStream.readObject();
				System.out.println("InputThread: Received message!");
				handleMessage(receivedMessage);
				System.out.println("InputThread: Message handled");
			}
			
			catch (IOException | ClassNotFoundException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void handleMessage(Message message)
	{
		client.updateTextFile(message.getFrom(), message.getText());
		client.setUsers(message.getUsers());
	}
	
}
