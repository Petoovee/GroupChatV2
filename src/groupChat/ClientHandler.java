package groupChat;

import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler extends Thread
{
	private Socket socket;
	private User user;
	private Client client;
	private ObjectOutputStream outToClient;
}
