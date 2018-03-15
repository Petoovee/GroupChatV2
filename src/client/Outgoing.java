package client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.LinkedList;

import server.Message;

public class Outgoing extends Thread
{
	private ObjectOutputStream outputStream;
	private LinkedList<Message> MessageToSend = new LinkedList<Message>();
	
	/*
	 * Works through the list of messages to send
	 */
	
	public Outgoing(ObjectOutputStream outputStream, Client client)
	{
		this.outputStream = outputStream;
		System.out.println("Output stream set up");
	}
	
	public void run()
	{
		while (!Thread.interrupted())
		{
			// Sending
			if (!MessageToSend.isEmpty())
			{
				try
				{
					outputStream.writeObject(MessageToSend.removeFirst());
					System.out.println("Message sent!");
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			else
				sleep();
		}
	}
	
	private void sleep()
	{
		try
		{
			Thread.sleep(150);
		}
		catch (InterruptedException e)
		{
			System.out.println("No rest for the triggered");
		}
	}
	
	public void addMessage(Message message)
	{
		MessageToSend.addLast(message);
	}
}
