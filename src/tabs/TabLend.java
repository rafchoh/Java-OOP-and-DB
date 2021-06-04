package tabs;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import database.DBHelp;
import database.Model;
import tabs.TabBook.SearchBAction;

public class TabLend extends JPanel implements RefreshDB{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;	
	
	Connection conn = null;
	PreparedStatement state = null;	
	JTable table3 = new JTable(); // Create Table
	JScrollPane scroller = new JScrollPane(table3);
	int id = -1;	
	static ResultSet result = null;
	
	static // Lend		
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	Date date = new Date();
	
	Calendar cal = Calendar.getInstance();	
	
	JPanel upLPanel = new JPanel();
	JPanel midLPanel = new JPanel();
	JPanel downLPanel = new JPanel();
		
	JLabel bookL = new JLabel("Книга:");
	JLabel nameL = new JLabel("Име:");
	JLabel dateTakenL = new JLabel("Дата на вземане:");
	JLabel dateReturnedL = new JLabel("Дата на връщане:");
	
	JComboBox<String> bookCombo = new JComboBox<String>();
	JComboBox<String> nameCombo = new JComboBox<String>();	
		
	JTextField dateTakenTF = new JTextField(sdf.format(new Date()));
	JTextField dateReturnedTF = new JTextField();		
	
	JButton addLBtn = new JButton("Вземане");
	JButton deleteLBtn = new JButton("Връщане");	
	JButton editLBtn = new JButton("Редактиране");
	JButton updateLBtn = new JButton("Обнови таблицата");
	
	JComboBox<String> sNameLCombo = new JComboBox<String>();
	JButton searchLBtn = new JButton("Търсене");
	
	
	public TabLend() {
		super(new GridBagLayout());
		register();
		var c = new GridBagConstraints();
		c.insets = new Insets(10,10,10,10);
		c.gridy = -1;
		c.gridx = 0;

		// upPanel
        upLPanel.setLayout(new GridLayout(4,2));				
				
		upLPanel.add(bookL);
		upLPanel.add(bookCombo);
		upLPanel.add(nameL);
		upLPanel.add(nameCombo);
		upLPanel.add(dateTakenL);		
		upLPanel.add(dateTakenTF);
		dateTakenTF.setEditable(false);
		upLPanel.add(dateReturnedL);		
		cal.setTime(date);
		cal.add(Calendar.DATE, 30);
		dateReturnedTF = new JTextField(sdf.format(cal.getTime()));		
		upLPanel.add(dateReturnedTF);
		dateReturnedTF.setEditable(false);

		// midPanel		
		
		midLPanel.add(addLBtn);
		midLPanel.add(deleteLBtn);		
		midLPanel.add(editLBtn);
		midLPanel.add(sNameLCombo);
		midLPanel.add(searchLBtn);
		midLPanel.add(updateLBtn);
		
        addLBtn.addActionListener(a -> addLAction());
		deleteLBtn.addActionListener(a -> deleteLAction());
		editLBtn.addActionListener(a -> editLAction());
		searchLBtn.addActionListener(new SearchLAction());
		updateLBtn.addActionListener(new UpdateLAction());
		
		// downPanel
		
		downLPanel.add(scroller);
		scroller.setPreferredSize(new Dimension(450,160));
		
		table3.addMouseListener(new TableListener());
		
		onUpdate();
		add(upLPanel,c);
		add(midLPanel,c);
		add(downLPanel,c);
	}// end TabLend
	
	public void onUpdate() {
		table3.setModel(DBHelp.getLendData()); // Update View
		DBHelp.fillBCombo(bookCombo);
		DBHelp.fillNCombo(nameCombo);
		DBHelp.fillLNCombo(sNameLCombo);
	}	
	
	private void addLAction() {
		conn = DBHelp.getConnection();
		String sql = "insert into lend values(null,?,?,?,?)";
		try {
			state = conn.prepareStatement(sql);
			state.setString(1, nameCombo.getSelectedItem().toString());
			state.setString(2, bookCombo.getSelectedItem().toString());
			state.setString(3, dateTakenTF.getText());
			state.setString(4, dateReturnedTF.getText());				
			state.execute();
			
			this.performUpdate(this);// Update View			
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void deleteLAction() {
	    conn = DBHelp.getConnection();
		String sql = "delete from lend where id=?";
		try {
			state = conn.prepareStatement(sql);
			state.setInt(1, id);
			state.execute();
			id = -1; 
			
			this.performUpdate(this);// Update View
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}			
	}	//end DeleteAction			
	
	private void editLAction() {
		
		int row = table3.getSelectedRow();
		id = Integer.parseInt(table3.getValueAt(row, 0).toString());
		
		conn = DBHelp.getConnection();
		String sql = "update lend set name=?, book=?, dateTaken=?, dateReturned=? where id=?";
		try {
			state = conn.prepareStatement(sql);				
			state.setString(1, nameCombo.getSelectedItem().toString());
			state.setString(2, bookCombo.getSelectedItem().toString());
			state.setString(3, dateTakenTF.getText());
			state.setString(4, dateReturnedTF.getText());	
			state.setInt(5, id);
			state.execute();

			this.performUpdate(this);// Update View
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}			
	}//end EditAction	
	
	
		
	class SearchLAction implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			String item = sNameLCombo.getSelectedItem().toString();
			String[] content = item.split(" ");
			int lendId = Integer.parseInt(content[0]);
	
			conn = DBHelp.getConnection();
			String sql = "select * from lend where id=?";
			try {
				state = conn.prepareStatement(sql);
				state.setInt(1, lendId);
				result = state.executeQuery();
				table3.setModel(new Model(result));

			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}		
	}// end SearchAction
	
	class UpdateLAction implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			table3.setModel(DBHelp.getLendData()); // Update View
		}		
	}// end UpdateAction
	
	class TableListener implements MouseListener{

		@Override
		public void mouseClicked(MouseEvent e) {
			int row = table3.getSelectedRow();
			id = Integer.parseInt(table3.getValueAt(row, 0).toString());
			
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}// end TableListener

	
}// end Constructor
