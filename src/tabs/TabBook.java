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

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import database.DBHelp;
import database.Model;

public class TabBook extends JPanel implements RefreshDB{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Connection conn = null;
	PreparedStatement state = null;
	JTable table1 = new JTable(); // Create Table
	JScrollPane scroller = new JScrollPane(table1);
	int id = -1;	
	static ResultSet result = null;

	// Books		 
	JPanel upPanel = new JPanel();
	JPanel midPanel = new JPanel();
	JPanel downPanel = new JPanel();	
	
	JLabel authorL = new JLabel("Автор:");
	JLabel bookNameL = new JLabel("Заглавие:");
	JLabel yearL = new JLabel("Година:");
	JLabel pagesL = new JLabel("Брой страници:");
	
	JTextField authorTF = new JTextField();
	JTextField bookNameTF = new JTextField();
	JTextField yearTF = new JTextField();
	JTextField pagesTF = new JTextField();
	
	JButton addBtn = new JButton("Добавяне");
	JButton deleteBtn = new JButton("Изтриване");	
	JButton editBtn = new JButton("Редактиране");
	JButton updateBtn = new JButton("Обнови таблицата");
	
	JComboBox<String> sNameBCombo = new JComboBox<String>();
	JButton searchBBtn = new JButton("Търсене");
	
	public TabBook() {
		super(new GridBagLayout());
		register();
		var c = new GridBagConstraints();
		c.insets = new Insets(10,10,10,10);
		c.gridy = -1;
		c.gridx = 0;
		
		// upPanel
		upPanel.setLayout(new GridLayout(4,2));				
		
		upPanel.add(authorL);
		upPanel.add(authorTF);
		upPanel.add(bookNameL);
		upPanel.add(bookNameTF);
		upPanel.add(yearL);
		upPanel.add(yearTF);
		upPanel.add(pagesL);
		upPanel.add(pagesTF);
			
		// midPanel		
		
		midPanel.add(addBtn);
		midPanel.add(deleteBtn);
		midPanel.add(editBtn);
		midPanel.add(sNameBCombo);
		midPanel.add(searchBBtn);
		midPanel.add(updateBtn);
			
		addBtn.addActionListener(a -> addBAction());
		deleteBtn.addActionListener(a -> deleteBAction());
		editBtn.addActionListener(a -> editBAction());	
		updateBtn.addActionListener(new UpdateBAction());
		searchBBtn.addActionListener(new SearchBAction());
		
		// downPanel
		
		downPanel.add(scroller);
		scroller.setPreferredSize(new Dimension(450,160));
		
		table1.addMouseListener(new TableListener());	
		
		onUpdate();
		add(upPanel,c);
		add(midPanel,c);
		add(downPanel,c);
	}// end TabBook
	
	public void onUpdate() {
		table1.setModel(DBHelp.getBookData()); // Update View
		DBHelp.fillBBCombo(sNameBCombo);
	}	
	
	
	public void clearForm() {
		authorTF.setText("");
		bookNameTF.setText("");
		yearTF.setText("");
		pagesTF.setText("");
	}// end clearForm
		
	private void addBAction() {
		conn = DBHelp.getConnection();
		String sql = "insert into book values(null,?,?,?,?)";
		try {
			state = conn.prepareStatement(sql);
			state.setString(1, authorTF.getText());
			state.setString(2, bookNameTF.getText());
			state.setInt(3, Integer.parseInt(yearTF.getText()));
			state.setInt(4, Integer.parseInt(pagesTF.getText()));				
			state.execute();
			
			this.performUpdate(this);// Update View
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	    clearForm();
	}//end class AddAction
	
	private void deleteBAction() {
		conn = DBHelp.getConnection();
		String sql = "delete from book where id=?";
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
	}//end DeleteAction
	
	private void editBAction() {
		
		int row = table1.getSelectedRow();
		id = Integer.parseInt(table1.getValueAt(row, 0).toString());
		
		conn = DBHelp.getConnection();
		String sql = "update book set author=?, bookName=?, year=?, pages=? where id=?"; 
		try {
			state = conn.prepareStatement(sql);
			state.setString(1, authorTF.getText());
			state.setString(2, bookNameTF.getText());
			state.setInt(3, Integer.parseInt(yearTF.getText()));
			state.setInt(4, Integer.parseInt(pagesTF.getText()));
			state.setInt(5, id);
			state.execute();
			
			this.performUpdate(this);// Update View
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}			
	    clearForm();
	}//end EditAction
	
	class SearchBAction implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			String item = sNameBCombo.getSelectedItem().toString();
			String[] content = item.split(" ");
			int bookId = Integer.parseInt(content[0]);
	
			conn = DBHelp.getConnection();
			String sql = "select * from book where id=?";
			try {
				state = conn.prepareStatement(sql);
				state.setInt(1, bookId);
				result = state.executeQuery();
				table1.setModel(new Model(result));
				
				DBHelp.fillBBCombo(sNameBCombo);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}		
	}// end SearchAction
	
	class UpdateBAction implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			table1.setModel(DBHelp.getBookData()); // Update View
		}		
	}// end UpdateAction
	
	class TableListener implements MouseListener{

		@Override
		public void mouseClicked(MouseEvent e) {
			int row = table1.getSelectedRow();
			id = Integer.parseInt(table1.getValueAt(row, 0).toString());
			
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
		
	}//end TableListener
	
}
