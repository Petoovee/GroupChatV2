package client;

import java.io.IOException;
import java.io.ObjectInputStream;

public class Incoming extends Thread
{
	private ObjectInputStream inputStream;
	private Message receivedMessage;
	private Client client;
	
	/*
	 * Waits for a message, when one is received, handle it properly, adding it to the right file and updating users
	 */
	
	public Incoming(ObjectInputStream inputStream, Client client)
	{
		this.inputStream = inputStream;
		this.client = client;
		System.out.println("Input stream set up");
	}
	
	public void run()
	{
		while (!Thread.interrupted())
		{
			try
			{
				System.out.println("Trying to receive message");
				receivedMessage = (Message) inputStream.readObject();
				System.out.println("Received message!");
				handleMessage(receivedMessage);
				System.out.println("Message handled");
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
