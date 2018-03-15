package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class ClientHandler extends Thread
{
	
	// Objects
	private ObjectOutputStream outToClient;
	private ObjectInputStream inFromClient;
	
	// Messages
	private LinkedList<Message> MessageToSend;
	private LinkedList<Message> oldMessagesToSend;
	private LinkedList<String> users; // All users
	private Message receivedMessage;
	private Message currentMessageToSend;
	private String user; // Current user
	private String filepath = "files/clients/";
	
	// Connection info
	Socket socket;
	ServerSocket serverSocket;
	
	// Other
	private PrintWriter writer;
	
	public ClientHandler() // If we don't have any socket to give
	{
	}
	
	public ClientHandler(Socket socket) // If a handlerless client is found
	{
		this.socket = socket;
	}
	
	public ClientHandler(String user) // If a user has no connection
	{
		this.user = user;
	}
	
	public ClientHandler(Socket socket, String user) // If we by sheer luck happen to have both
	{
		this.socket = socket;
		this.user = user;
	}
	
	public void initiate() // Used to avoid cluttering the loop
	{
		establishStreams(socket);
		
		// Wait for users name, should be first message - overwrite even if we already
		// have their name, why not
		System.out.println("Trying to receive initial message");
		try
		{
			receivedMessage = (Message) inFromClient.readObject();
			System.out.println("Initial message received");
		}
		catch (ClassNotFoundException | IOException e)
		{
			
		}
		
		if (receivedMessage.getText() == null && receivedMessage.getFile() == null && receivedMessage.getTo() == null
				&& receivedMessage.getUsers() == null)
		{
			user = receivedMessage.getFrom();
			System.out.println("Username set to " + user);
		}
		
		// If needed, create a new file for the client
		File file = new File(filepath + user + ".txt");
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
	}
	
	public void run() // Main loop
	{
		initiate();
		while (!Thread.interrupted())
		{
			hystericalExistentialCrisis();
			
			try
			{
				// Receiving
				receivedMessage = (Message) inFromClient.readObject();
				
				// Send messages
				sendOldMessage();
				if (!MessageToSend.isEmpty())
				{
					currentMessageToSend = MessageToSend.removeFirst();
					currentMessageToSend.setUsers(getUsers());
					outToClient.writeObject(currentMessageToSend);
				}
				
			}
			catch (ClassNotFoundException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				saveOfflineMessage(currentMessageToSend);
			}
		}
	}
	
	public void readOfflineMessages() // Reads all the old messages and saves them in a usable LinkedList
	{
		// Empty old list of old messages
		oldMessagesToSend.clear();
		
		// Prepare the file if necessary
		File file = new File(filepath + user + ".txt");
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
		
		// Read the file
		BufferedReader bufferedReader;
		try
		{
			bufferedReader = new BufferedReader(new FileReader(new File(filepath + user + ".txt")));
			for (String line; (line = bufferedReader.readLine()) != null;)
			{
				String[] messageBuilder = line.split(",!,");
				oldMessagesToSend.add(new Message(messageBuilder[1], null, messageBuilder[3], messageBuilder[4], null));
			}
			bufferedReader.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public synchronized void saveOfflineMessage(Message message) // Reads all the old messages and saves them, including
																	// the last message
	{
		readOfflineMessages();
		oldMessagesToSend.add(message);
		
		// Write messages to file
		{
			// Assemble old messages to a text version
			for (int i = 0; i <= oldMessagesToSend.size(); i++)
			{
				String messageBuilder = oldMessagesToSend.get(i).getText() + ",!,";
				messageBuilder += "null" + ",!,";
				messageBuilder += oldMessagesToSend.get(i).getTo() + ",!,";
				messageBuilder += oldMessagesToSend.get(i).getFrom() + ",!,";
				messageBuilder += "null";
				
				// Save old messages again
				try
				{
					writer = new PrintWriter(filepath + oldMessagesToSend.get(i).getTo() + ".txt");
					writer.println(messageBuilder);
				}
				catch (FileNotFoundException e)
				{
					// The file is made a few cycles before saving here...
				}
			}
			
			// Save current message
			try
			{
				String messageBuilder = message.getText() + ",!,";
				messageBuilder += "null" + ",!,";
				messageBuilder += message.getTo() + ",!,";
				messageBuilder += message.getFrom() + ",!,";
				messageBuilder += "null";
				writer = new PrintWriter(filepath + message.getTo() + ".txt");
				writer.println(messageBuilder);
			}
			catch (FileNotFoundException e)
			{
				// The file is made a few cycles before saving here...
			}
		}
	}
	
	public void sendOldMessage() // Reads the old messages into the message queue
	{
		readOfflineMessages();
		for (int i = 0; i < oldMessagesToSend.size(); i++)
		{
			MessageToSend.add(oldMessagesToSend.get(i));
		}
	}
	
	public LinkedList<String> getUsers()
	{
		File clientsFolder = new File(filepath);
		File[] listOfUsers = clientsFolder.listFiles();
		users = new LinkedList<String>();
		
		for (int i = 0; i < listOfUsers.length; i++)
		{
			if (listOfUsers[i].isFile() && listOfUsers[i].getName().contains(".txt"))
			{
				users.add(listOfUsers[i].getName().replace(".txt", "") + "\n");
			}
		}
		return users;
	}
	
	public void sleep() // If no purpose in life is found, sleep
	{
		if (socket == null || socket.isClosed())
		{
			try
			{
				Thread.sleep(50);
			}
			catch (InterruptedException e)
			{
				System.out.println("No rest for the triggered");
			}
		}
	}
	
	public void hystericalExistentialCrisis() // If no purpose in life is found, end it
	{
		if (socket == null || socket.isClosed())
			return;
	}
	
	public void establishStreams(Socket socket) // Establish sockets (duh?)
	{
		int attemptsToEstablishStreams = 0;
		while (attemptsToEstablishStreams <= 10)
		{
			try
			{
				outToClient = new ObjectOutputStream(socket.getOutputStream());
				inFromClient = new ObjectInputStream(socket.getInputStream());
				attemptsToEstablishStreams = 11;
			}
			catch (IOException e)
			{
				System.out.println(
						"Input/Output streams failed, dropping socket in " + (10 - attemptsToEstablishStreams));
				e.printStackTrace();
			}
		}
		if (attemptsToEstablishStreams == 10)
		{
			System.out.println("Dropping socket, ending life for greater good");
			return;
		}
		else
			System.out.println("Streams established");
	}
}