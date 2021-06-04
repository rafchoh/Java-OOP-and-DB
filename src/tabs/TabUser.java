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

public class TabUser extends JPanel implements RefreshDB{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	Connection conn = null;
	PreparedStatement state = null;
	JTable table2 = new JTable(); // Create Table
	JScrollPane scroller = new JScrollPane(table2);
	int id = -1;	
	static ResultSet result = null;

	// Users	
		JPanel upUPanel = new JPanel();
		JPanel midUPanel = new JPanel();
		JPanel downUPanel = new JPanel();
			
		JLabel firstNameL = new JLabel("Име:");
		JLabel lastNameL = new JLabel("Фамилия:");
		JLabel ageL = new JLabel("Възраст:");
		JLabel addressL = new JLabel("Адрес:");
		
		JTextField firstNameTF = new JTextField();
		JTextField lastNameTF = new JTextField();
		JTextField ageTF = new JTextField();
		JTextField addressTF = new JTextField();
		
		JButton addUBtn = new JButton("Добавяне");
		JButton deleteUBtn = new JButton("Изтриване");	
		JButton editUBtn = new JButton("Редактиране");
		JButton updateUBtn = new JButton("Обнови таблицата");
		
		JComboBox<String> sNameUCombo = new JComboBox<String>();
		JButton searchUBtn = new JButton("Търсене");
		
		public TabUser() {			
			super(new GridBagLayout());
			register();
			var c = new GridBagConstraints();
			c.insets = new Insets(10,10,10,10);
			c.gridy = -1;
			c.gridx = 0;
			
			// upPanel
			upUPanel.setLayout(new GridLayout(4,2));				
			
			upUPanel.add(firstNameL);
			upUPanel.add(firstNameTF);
			upUPanel.add(lastNameL);
			upUPanel.add(lastNameTF);
			upUPanel.add(ageL);
			upUPanel.add(ageTF);
			upUPanel.add(addressL);
			upUPanel.add(addressTF);
				
			// midPanel	
			
			midUPanel.add(addUBtn);
			midUPanel.add(deleteUBtn);
			midUPanel.add(editUBtn);
			midUPanel.add(sNameUCombo);
			midUPanel.add(searchUBtn);
			midUPanel.add(updateUBtn);

			addUBtn.addActionListener(a -> addUAction());
			deleteUBtn.addActionListener(a -> deleteUAction());
			editUBtn.addActionListener(a -> editUAction());			
			searchUBtn.addActionListener(new SearchUAction());
			updateUBtn.addActionListener(new UpdateUAction());
						
			// downPanel
			
			downUPanel.add(scroller);
			scroller.setPreferredSize(new Dimension(450,160));
			
			table2.addMouseListener(new TableListener());
			
			onUpdate();
			add(upUPanel,c);
			add(midUPanel,c);
			add(downUPanel,c);
		}// end TabUser
		
		public void onUpdate() {
			table2.setModel(DBHelp.getUserData()); // Update View			
			DBHelp.fillUNCombo(sNameUCombo);
		}
		
		public void clearForm() {
			firstNameTF.setText("");
			lastNameTF.setText("");
			ageTF.setText("");
			addressTF.setText("");
		}// end clearForm
			
		private void addUAction() {
			conn = DBHelp.getConnection();
			String sql = "insert into user values(null,?,?,?,?)";
			try {
				state = conn.prepareStatement(sql);
				state.setString(1, firstNameTF.getText());
				state.setString(2, lastNameTF.getText());
				state.setByte(3, Byte.parseByte(ageTF.getText()));
				state.setString(4, addressTF.getText());					
				state.execute();					
			
				this.performUpdate(this);// Update View
			}catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    clearForm();
		}//end AddAction
		
		private void deleteUAction() {
			conn = DBHelp.getConnection();
			String sql = "delete from user where id=?";
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
		
		private void editUAction() {
			
			int row = table2.getSelectedRow();
			id = Integer.parseInt(table2.getValueAt(row, 0).toString());
			
			conn = DBHelp.getConnection();
			String sql = "update user set firstName=?, lastName=?, age=?, address=? where id=?";
			try {
				state = conn.prepareStatement(sql);
				state.setString(1, firstNameTF.getText());
				state.setString(2, lastNameTF.getText());
				state.setByte(3, Byte.parseByte(ageTF.getText()));
				state.setString(4, addressTF.getText());				
				state.setInt(5, id);
				state.execute();
				
				this.performUpdate(this);// Update View
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}			
		    clearForm();
		}//end DeleteAction	
		
		
		
		class SearchUAction implements ActionListener{

			@Override
			public void actionPerformed(ActionEvent e) {
				String item = sNameUCombo.getSelectedItem().toString();
				String[] content = item.split(" ");
				int bookId = Integer.parseInt(content[0]);
		
				conn = DBHelp.getConnection();
				String sql = "select * from user where id=?";
				try {
					state = conn.prepareStatement(sql);
					state.setInt(1, bookId);
					result = state.executeQuery();
					table2.setModel(new Model(result));			
					
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}			
		}// end SearchAction
		
		class UpdateUAction implements ActionListener{

			@Override
			public void actionPerformed(ActionEvent e) {
				table2.setModel(DBHelp.getUserData()); // Update View
			}		
		}// end UpdateAction
		
		class TableListener implements MouseListener{

			@Override
			public void mouseClicked(MouseEvent e) {
				int row = table2.getSelectedRow();
				id = Integer.parseInt(table2.getValueAt(row, 0).toString());
				
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