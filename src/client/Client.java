package client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.plaf.synth.SynthSeparatorUI;

import server.Message;

public class Client extends Thread
{
	// Connection info
	private Socket socket;
	private int portMin = 4473;
	private int portMax = 4483;
	private String IP = "localhost";
	
	// Objects
	private ObjectOutputStream outToServer;
	private ObjectInputStream inFromServer;
	private Incoming inputStream;
	private Outgoing outputStream;
	
	private String recipient;
	
	// Other
	private PrintWriter writer;
	private ClientUI ui;
	
	public Client(ClientUI ui)
	{
		this.ui = ui;
		this.run();
	}
	
	public void initiate()
	{
		ui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ui.setVisible(true);
		
		createSocket();
		System.out.println("Connected!");
		
		establishStreams();
		System.out.println("Streams set up, attempting to sign in");
		
		outputStream.addMessage((new Message(null, null, ui.getUserName(), null, null)));
		
	}
	
	private void createSocket()
	{
		boolean tryingToCreateSocket = true;
		int port = portMin;
		while (tryingToCreateSocket)
		{
			try
			{
				System.out.println("Connecting to " + IP + " at " + port);
				socket = new Socket(IP, port);
				tryingToCreateSocket = false;
			}
			catch (UnknownHostException e)
			{
				if (port >= portMax)
					port = portMin;
				else
					port++;
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	private void establishStreams()
	{
		System.out.println("Trying to establish streams");
		
		outputStream = new Outgoing(socket, this);
		outputStream.start();
		
		inputStream = new Incoming(socket, this);
		inputStream.start();
	}
	
	private void reviveStreams()
	{
		if(!outputStream.isAlive()|| !inputStream.isAlive())
			createSocket();
		
		if (!outputStream.isAlive())
		{
			System.out.println("Client: Trying to revive outputStream");
			outputStream = new Outgoing(socket, this);
			outputStream.start();
		}
		
		if (!inputStream.isAlive())
		{
			System.out.println("Client: Trying to revive inputStream");
			inputStream = new Incoming(socket, this);
			inputStream.start();
			
		}
	}
	
	public void run()
	{
		initiate();
		
		while (!Thread.interrupted())
		{		
			if (!inputStream.isAlive() || !outputStream.isAlive())
			{
				System.out.println("Client: A stream has died!");
				reviveStreams();
			}
			
			try
			{
				Thread.sleep(50);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void setUsers(LinkedList<String> users) // Ran every time a message is received
	{
		
	}
	
	public void addFriend(String text)
	{
		text = text.replace("/add ", "");
		
		File file = new File("files/" + ui.getUserName() + "/friends/" + text + ".txt");
		if (!file.exists())
		{
			try
			{
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			catch (IOException e)
			{
				System.out.println("Abandon ship");
			}
		}
		ui.updateContacts();
	}
	
	public void removeFriend(String text)
	{
		File file = new File("files/" + ui.getUserName() + "/friends/" + text + ".txt");
		if (file.exists())
		{
			file.delete();
		}
		ui.updateContacts();
	}
	
	public void updateTextFile(String file, String text) // Whenever we receive or send new messages
	{
		try
		{
			System.out.println("Updating text file files/friends/" + file + ".txt");
			writer = new PrintWriter("files/friends/" + file + ".txt");
			writer.print(text);
			
		}
		catch (FileNotFoundException e)
		{
			System.out.println("Stranger attempting dialogue");
		}
	}
	
	public void newRecipient(String newRecipient) // For when we switch between conversations using "/send"
	{
		// In case we're making friends for once
		newRecipient = newRecipient.replace("/send ", "/add ");
		addFriend(newRecipient);
		newRecipient = newRecipient.replace("/add ", "");
		
		// Set this recipient as our new recipient for sending messages
		this.recipient = newRecipient;
		
		// Load chat for this recipient
		String text = new String();
		try
		{
			BufferedReader bufferedReader = new BufferedReader(
					new FileReader(new File("files/" + ui.getUserName() + "/friends/" + newRecipient + ".txt")));
			for (String line; (line = bufferedReader.readLine()) != null; text += line)
				;
			bufferedReader.close();
		}
		catch (IOException e)
		{
			System.out.println("Failed reading newRecipient chat");
		}
		
		ui.updateChat(text);
	}
	
	public void sendMessage(String text, File file)
	{
		if (text.contains("/add "))
		{
			addFriend(text);
			text = "";
			file = null;
		}
		if (text.contains("/remove "))
		{
			removeFriend(text);
			text = "";
			file = null;
		}
		
		if ((!text.isEmpty() || file != null))
			outputStream.addMessage(new Message(text, file, ui.getUserName(), recipient, null));
	}
	
	public static void main(String[] args)
	{
		Client client = new Client(new ClientUI());
	}
}
