package groupChat;

import java.io.File;
import java.util.LinkedList;

public class Message
{
	private String text;
	private String to;
	private String from;
	private File file;
	private LinkedList<String> users;
	
	public Message(String text, File file, String to, String from, LinkedList<String> users)
	{
		this.text = text;
		this.file = file;
		this.users = users;
		this.to = to;
		this.from = from;
	}
	
	public String getTo()
	{
		return to;
	}
	
	public String getFrom()
	{
		return from;
	}
	
	public LinkedList<String> getUsers()
	{
		return users;
	}
	
	public String getText()
	{
		return text;
	}
	
	public void setText(String text)
	{
		this.text = text;
	}
	
	public File getFile()
	{
		return file;
	}
}
