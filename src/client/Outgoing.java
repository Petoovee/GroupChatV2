package client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;

import server.Message;

public class Outgoing extends Thread
{
	private ObjectOutputStream outputStream;
	private LinkedList<Message> MessageToSend = new LinkedList<Message>();
	private Socket socket;
	private Client client;
	
	/*
	 * Works through the list of messages to send
	 */
	
	public Outgoing(Socket socket, Client client)
	{
		this.socket = socket;
		this.client = client;
		
		System.out.println("OutputThread constructed");
	}
	
	private void constructStream()
	{
		try
		{
			outputStream = new ObjectOutputStream(socket.getOutputStream());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void run()
	{
		System.out.println("OutputThread started");
		
		constructStream();
		while (!Thread.interrupted())
		{
			while (MessageToSend.isEmpty())
			{
				sleep();
			}
			
			// Sending
			try
			{
				outputStream.writeObject(MessageToSend.removeFirst());
				System.out.println("OutputThread: Message sent!");
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
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
