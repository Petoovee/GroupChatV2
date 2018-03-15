package client;

import java.awt.Label;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileSystemView;

public class ClientUI extends JFrame
{
	private String name;
	
	private Client client;
	private File file;
	
	// UI stuff
	private JPanel contentPane = new JPanel();
	private JLabel lblOnline;
	private JLabel lblOnlone;
	private Label userLabel;
	private JButton btnImage;
	private JButton sendButton;
	private final TextArea contactList = new TextArea();
	private final TextArea chatArea = new TextArea();
	private final TextArea onlineList = new TextArea();
	private final TextArea messageField = new TextArea();
	private final JLabel lblNewLabel = new JLabel("New label");
	
	/**
	 * Create the frame.
	 */
	public ClientUI()
	{
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 658, 565);
		setContentPane(contentPane);
		
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		
		sendButton = new JButton("Send");
		sendButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				sendButtonPressed();
			}
		});
		sendButton.setBounds(397, 426, 114, 82);
		contentPane.add(sendButton);
		
		btnImage = new JButton("Image");
		btnImage.setBounds(523, 426, 105, 82);
		contentPane.add(btnImage);
		
		userLabel = new Label();
		userLabel.setAlignment(Label.CENTER);
		userLabel.setBounds(10, -2, 346, 35);
		contentPane.add(userLabel);
		
		lblOnlone = new JLabel("Contacts");
		lblOnlone.setHorizontalAlignment(SwingConstants.CENTER);
		lblOnlone.setBounds(423, -2, 177, 30);
		contentPane.add(lblOnlone);
		
		lblOnline = new JLabel("Online users");
		lblOnline.setHorizontalAlignment(SwingConstants.CENTER);
		lblOnline.setBounds(420, 169, 180, 35);
		contentPane.add(lblOnline);
		contactList.setBounds(423, 36, 177, 127);
		
		contentPane.add(contactList);
		chatArea.setBounds(105, 37, 312, 364);
		
		contentPane.add(chatArea);
		onlineList.setBounds(423, 210, 177, 164);
		
		contentPane.add(onlineList);
		messageField.setBounds(105, 426, 274, 82);
		
		contentPane.add(messageField);
		lblNewLabel.setBounds(10, 39, 89, 87);
		
		contentPane.add(lblNewLabel);
		
		JLabel label = new JLabel("New label");
		label.setBounds(10, 426, 89, 82);
		contentPane.add(label);
		
	}
	
	public void sendButtonPressed()
	{
		client.sendMessage(messageField.getText(), file);
		messageField.setText("");
	}
	
	public void updateChat(String text)
	{
		chatArea.setText(text);
	}
	
	public void setUserName()
	{
		name = JOptionPane.showInputDialog(contentPane, "Choose a screen name:", "Screen name selection",
				JOptionPane.PLAIN_MESSAGE);
		userLabel.setText(name);
	}
	
	public String getUserName()
	{
		if (name == null)
			setUserName();
		name = "" + userLabel.getText();
		return name;
	}
	
	public void updateUsers(String userList)
	{
		onlineList.setText(userList);
	}
	
	public void updateContacts()
	{
		File friendsFolder = new File("files/" +getUserName() +"/friends");
		File[] listOfFriends = friendsFolder.listFiles();
		contactList.setText("");

		for (int i = 0; i < listOfFriends.length; i++)
		{
			if (listOfFriends[i].isFile() && listOfFriends[i].getName().contains(".txt"))
			{
				contactList.append(listOfFriends[i].getName().replace(".txt", "") +"\n");
			}
		}	
	}
	
	public void imageButtonPressed()
	{
		
		JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		
		int returnValue = jfc.showOpenDialog(null);
		
		if (returnValue == JFileChooser.APPROVE_OPTION)
		{
			file = jfc.getSelectedFile();
		}
	}
	
}
