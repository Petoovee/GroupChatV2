package server;

import java.io.File;
import java.io.Serializable;
import java.util.LinkedList;

public class Message implements Serializable
{
	/*
	 *  All objects are serializable in this class.
	 */
	
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
	
	public void setUsers(LinkedList<String> users)
	{
		this.users = users;
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
