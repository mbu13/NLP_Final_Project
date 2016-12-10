package Interface;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.border.Border;

import Helpers.SentimentEmojis;
import Interface.UserInterface.ChatMessage;

public class EmojiSelectionPane extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String path = "src/Assets/emojis/";
	
	private String[] emojis;
	private JButton[] btns;
	private SentimentEmojis sentimentEmojis;
	private Random randomGenerator;
	private UserInterface parent;
	
	private JButton btn1;
	private JButton btn2;
	private JButton btn3;
	private JButton btn4;
	private JButton btn5;
	
	public EmojiSelectionPane(int width, UserInterface parent) {
		emojis = new String[5];
		btns = new JButton[5];
		this.parent = parent;
		
		sentimentEmojis = new SentimentEmojis();
		randomGenerator = new Random();
		
		this.setSize(new Dimension(180, 40));
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setUndecorated(true);
		this.getContentPane().setBackground( Color.WHITE );
		
		btn1 = new JButton();
		btn2 = new JButton();
		btn3 = new JButton();
		btn4 = new JButton();
		btn5 = new JButton();
		btns[0] = btn1;
		btns[1] = btn2;
		btns[2] = btn3;
		btns[3] = btn4;
		btns[4] = btn5;

		btns[0].addActionListener(new ActionListener()
		{
		  public void actionPerformed(ActionEvent e)
		  {
			  parent.addEmoji(parent.currentUser, emojis[0]);;
		  }
		});
		
		btns[1].addActionListener(new ActionListener()
		{
		  public void actionPerformed(ActionEvent e)
		  {
			  parent.addEmoji(parent.currentUser, emojis[1]);;
		  }
		});
		
		btns[2].addActionListener(new ActionListener()
		{
		  public void actionPerformed(ActionEvent e)
		  {
			  parent.addEmoji(parent.currentUser, emojis[2]);;
		  }
		});
		
		btns[3].addActionListener(new ActionListener()
		{
		  public void actionPerformed(ActionEvent e)
		  {
			  parent.addEmoji(parent.currentUser, emojis[3]);;
		  }
		});
		
		btns[4].addActionListener(new ActionListener()
		{
		  public void actionPerformed(ActionEvent e)
		  {
			  parent.addEmoji(parent.currentUser, emojis[4]);;
		  }
		});
		
		for(int i = 0; i < 5; ++i) {
			btns[i].setPreferredSize(new Dimension(30, 30));
			btns[i].setBackground(Color.WHITE);
			Border emptyBorder = BorderFactory.createEmptyBorder();
			btns[i].setBorder(emptyBorder);
			this.add(btns[i]);
		}
		
		update(new String[] {"happy", "neutral", "sad", "happy", "happy"});
	}
	
	public void update(String[] sentiments) {
		HashSet<String> used = new HashSet<>();
		for(int i = 0; i < sentiments.length; ++i) {
			emojis[i] = getRandomEmojiBySentiment(sentiments[i], used);
			
			Image img;

			try {
				img = ImageIO.read(new FileInputStream(emojis[i]));
				btns[i].setIcon(new ImageIcon(img));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	private String getRandomEmojiBySentiment(String sentiment, HashSet<String> used) {
		List<String> emojiNames = sentimentEmojis.getEmojiNames(sentiment);
		
		int index = randomGenerator.nextInt(emojiNames.size());
		
		while(used.contains(emojiNames.get(index))) {
			index = randomGenerator.nextInt(emojiNames.size());
		}
		used.add(emojiNames.get(index));
		
		return path + emojiNames.get(index);
	}
}
