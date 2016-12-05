package Interface;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import Helpers.SentimentAnalyzer;
import Helpers.UserMessage;

public class UserInterface {
	
	private final int width = 350, height = 550;
	private JPanel mid;
	private JScrollPane scroll;
	private final String currentUser = "mattbu";
	private final String chatName = "NLP Skwad";
	private List<UserMessage> messages;
	private SentimentAnalyzer sa;
	private EmojiSelectionPane emojiFrame;
	private boolean emojiPaneVisible;
	private Stack<String> latestMessages;

	public UserInterface(List<UserMessage> messages) {
		JFrame guiFrame = new JFrame();
		this.messages = new ArrayList<>();
		emojiFrame = new EmojiSelectionPane(width);
		emojiFrame.setVisible(false);
		emojiPaneVisible = false;
		
		latestMessages = new Stack<>();
		
		// Init sentiment analyzer
    	sa = new SentimentAnalyzer();
		SentimentAnalyzer.init();
        
        //make sure the program exits when the frame closes
        guiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        guiFrame.setTitle("Messenger");
        guiFrame.setSize(width,height);
        guiFrame.getContentPane().setBackground(Color.WHITE);
        
        guiFrame.setLocationRelativeTo(null);
        guiFrame.setResizable(false);
        
        // Set banner
        final JPanel banner = new JPanel();
        banner.setPreferredSize(new Dimension(width, 50));
        banner.setBackground(new Color(248, 248, 248));
        banner.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(229, 229, 229)));
        banner.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        JLabel label = new JLabel("   " + chatName);
        label.setPreferredSize(new Dimension(width - 30, 40));
		label.setFont(new Font("Calibri", Font.PLAIN, 18));
		label.setVerticalAlignment(SwingConstants.CENTER);
		banner.add(label);
        
        // Set middle scroll pane
        mid = new JPanel();
        mid.setLayout(new BoxLayout(mid, BoxLayout.Y_AXIS));
        mid.setBackground(Color.white);
        mid.setBounds(0, 50, width, height - 110);
        scroll = new JScrollPane(mid,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        scroll.getViewport().setBorder(null);
        scroll.setViewportBorder(null);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        
        populate(messages);
        
		JScrollBar vertical = scroll.getVerticalScrollBar();
		vertical.setValue( vertical.getMaximum() );
        
        // Set bottom
        final JPanel bottom = new JPanel();
        bottom.setPreferredSize(new Dimension(width, 60));
        bottom.setLayout(new FlowLayout(FlowLayout.LEFT));
        bottom.setBackground(Color.WHITE);
        bottom.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(229, 229, 229)));
        
        // Set text field
        JTextField textField = new JTextField();
        textField.setPreferredSize(new Dimension(width - 20, 20));
        textField.setBorder(null);
        
        /* Action listener */
        Action action = new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
            	String text = textField.getText();
            	mid.add(new ChatMessage(currentUser, text));
            	scroll.revalidate();
            	scroll.repaint();

            	JScrollBar v = scroll.getVerticalScrollBar();
            	v.setValue( v.getMaximum());
            	
            	messages.add(new UserMessage(currentUser, text));
            	latestMessages.push(text);
            	
            	int i = 5;
            	String[] newSentiments = new String[5];
            	Arrays.fill(newSentiments, "happy");
            	while(latestMessages.size() > 0 && i > 0) {
	        		int mainSentiment = SentimentAnalyzer.findSentiment(latestMessages.pop());
    	        
	        		if (mainSentiment == 2 || mainSentiment > 4 || mainSentiment < 0) {
	        	        newSentiments[5 - i] = "neutral";
	        	    }
	        	    else if (mainSentiment > 2) {
	        	    	newSentiments[5 - i] = "happy";
	        	    }
	        	    else {
	        	    	newSentiments[5 - i] = "sad";
	        	    }
	        		--i;
    	        }
            	
            	emojiFrame.update(newSentiments);
        		
        		textField.setText("");
            }
        };
        textField.addActionListener(action);
        bottom.add(textField, BorderLayout.NORTH);
        
        /* Set bottom icons */
        ImageIcon image = new ImageIcon("src/Assets/bottom_icons.png");
		
		JLabel options = new JLabel();
		//ImageIcon imageIcon = new ImageIcon(image.getImage().getScaledInstance(40, 40, Image.SCALE_DEFAULT));
		options.setIcon(image);
		bottom.add(options, BorderLayout.SOUTH);
		
		JButton emojiButton = new JButton();
		emojiButton.setPreferredSize(new Dimension(20, 20));
		emojiButton.setBackground(Color.WHITE);

		Image img;
		try {
			img = ImageIO.read(new FileInputStream("src/Assets/emojis/happy1.png"));
			emojiButton.setIcon(new ImageIcon(img));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		emojiButton.addActionListener(new ActionListener()
		{
			  public void actionPerformed(ActionEvent e)
			  {
				  emojiFrame.setLocation(guiFrame.getX() + width / 2 - 8, guiFrame.getY() + guiFrame.getHeight());
				  if(!emojiPaneVisible) {
					  emojiFrame.setVisible(true);
					  emojiPaneVisible = true;
				  }
				  else {
					  emojiFrame.setVisible(false);
					  emojiPaneVisible = false;
				  }
				  
			  }
			});
	    
		emojiButton.setVisible(true);
		bottom.add(emojiButton);
        
        guiFrame.add(bottom, BorderLayout.SOUTH);
        guiFrame.add(scroll);
        guiFrame.add(banner, BorderLayout.NORTH);
        guiFrame.setVisible(true);
	}
	
	public void populate(List<UserMessage> dataset) {
		for(UserMessage um : dataset) {
			System.out.println(um.getName() + " " + um.getMessage());
			mid.add(new ChatMessage(um.getName(), um.getMessage()));
			this.messages.add(um);
			latestMessages.push(um.getMessage());
		}
		
	}
	
	public List<UserMessage> getUserMessages() {
		return Collections.unmodifiableList(messages);
	}
	
	class ChatMessage extends JPanel{

		private static final long serialVersionUID = 1L;

		public ChatMessage(String username, String txt) {
			this.setPreferredSize(new Dimension(width - 20, 50));
			
			this.setBackground(Color.white);
			LineBorder line;
			
			if(username.equals(currentUser)) {
				this.setLayout(new FlowLayout(FlowLayout.RIGHT));
				line = new LineBorder(new Color(229, 229, 229), 10, true); // color, thickness, rounded
			} else {
				this.setLayout(new FlowLayout(FlowLayout.LEFT));
				line = new LineBorder(new Color(0, 132, 255), 10, true); // color, thickness, rounded
			}
			
			JLabel label = new JLabel(txt);
			
			label.setFont(new Font("Tahoma", Font.PLAIN, 15));
			
		    label.setBorder(line);
		    label.setVerticalAlignment(SwingConstants.CENTER);
		    
		    if(username.equals(currentUser)) {
		    	label.setForeground(Color.BLACK);
		    	label.setBackground(new Color(229, 229, 229));
		    } else {
		    	label.setForeground(Color.white);
		    	label.setBackground(new Color(0, 132, 255));
		    }
			
			label.setOpaque(true);
			
			ImageIcon image = new ImageIcon("src/Assets/" + username + ".png");
			
			JLabel prof = new JLabel();
			ImageIcon imageIcon = new ImageIcon(image.getImage().getScaledInstance(40, 40, Image.SCALE_DEFAULT));
			prof.setIcon(imageIcon);
			
			if(!username.equals(currentUser)) {
				this.add(prof);
				this.add(label);
			} else {
				this.add(label);
				this.add(prof);
			}
			
		}
	}
	
}
