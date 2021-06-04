package main;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import tabs.TabBook;
import tabs.TabLend;
import tabs.TabUser;

public class Frame extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;	
	
	// Create Tabs
    JTabbedPane tabs = new JTabbedPane();   
		
	// My Frame
	public Frame() {
		this.setSize(500, 600);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);		
		
		tabs.add("K���� ������",new TabLend());
		tabs.add("��������",new TabUser());
		tabs.add("�����",new TabBook());
		
		this.add(tabs);
		
		pack();
		this.setVisible(true);		
	}// end Frame	
}// end class MyFrame