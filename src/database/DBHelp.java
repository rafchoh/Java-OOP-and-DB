package database;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import javax.swing.JComboBox;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

//import javax.swing.JComboBox;

public class DBHelp {

	static Connection connection = null;
	static PreparedStatement state = null;
	static Model model = null;
	static ResultSet result = null;
	private static Properties settings = null;
	
		
	public static Model getBookData() {
		
		connection = getConnection();
		String sql = "select * from book";
		try {
			state = connection.prepareStatement("SELECT * FROM BOOK");
			result = state.executeQuery();
			model = new Model(result);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return model;	
	}//end getBookData
	
    public static Model getUserData() {
		
		connection = getConnection();
		String sql = "select * from user";
		try {
			state = connection.prepareStatement("SELECT * FROM USER");
			result = state.executeQuery();
			model = new Model(result);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return model;	
	}//end getBookData
	
    public static Model getLendData() {
		
		connection = getConnection();
		String sql = "select * from lend";
		try {
			state = connection.prepareStatement("SELECT * FROM LEND");
			result = state.executeQuery();
			model = new Model(result);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return model;	
	}//end getLendData
    

  public static void fillBCombo(JComboBox<String> comboB) {
	    // for bookCombobox in Lend
  		connection = getConnection();
  		String sql = "select bookName, author from book";
  		try {
  			state = connection.prepareStatement(sql);
  			result = state.executeQuery();
  			comboB.removeAllItems();    // iztriva za da ne se poluchavat povtarqshti se elementi sled update na tablicata
  			while(result.next()) {
  				String item = result.getObject(1).toString() + " " + result.getObject(2).toString();
  			    comboB.addItem(item);
  		    } //end while
  		} catch (SQLException e) {
  			e.printStackTrace();
  		}		
  	}  //end method fillCombo
  
  public static void fillNCombo(JComboBox<String> comboN) {
		// for nameCombobox in Lend 
		connection = getConnection();
		String sql = "select firstname, lastname from user";	
		try {
			state = connection.prepareStatement(sql);
			result = state.executeQuery();
			comboN.removeAllItems();    // iztriva za da ne se poluchavat povtarqshti se elementi sled update na tablicata
			while(result.next()) {
				String item = result.getObject(1).toString() + " " + result.getObject(2).toString();
			    comboN.addItem(item);
		    } //end while
		} catch (SQLException e) {
			e.printStackTrace();
		}		
	}  //end method fillCombo
  
  public static void fillLNCombo(JComboBox<String> comboLN) {
		// for searchCombobox in Lend
		connection = getConnection();
		String sql = "select id, name from lend";	
		try {
			state = connection.prepareStatement(sql);
			result = state.executeQuery();
			comboLN.removeAllItems();    // iztriva za da ne se poluchavat povtarqshti se elementi sled update na tablicata
			while(result.next()) {
				String item = result.getObject(1).toString() + " " +  result.getObject(2).toString();
			    comboLN.addItem(item);
		    } //end while
		} catch (SQLException e) {
			e.printStackTrace();
		}		
	}  //end method fillCombo
  
  public static void fillBBCombo(JComboBox<String> comboBB) {
	    // for bookCombobox in Lend
		connection = getConnection();
		String sql = "select id, bookName from book";
		try {
			state = connection.prepareStatement(sql);
			result = state.executeQuery();
			comboBB.removeAllItems();    // iztriva za da ne se poluchavat povtarqshti se elementi sled update na tablicata
			while(result.next()) {
				String item = result.getObject(1).toString() + " " + result.getObject(2).toString();
			    comboBB.addItem(item);
		    } //end while
		} catch (SQLException e) {
			e.printStackTrace();
		}		
	}  //end method fillCombo
  
  public static void fillUNCombo(JComboBox<String> comboUN) {
		// for searchCombobox in Lend
		connection = getConnection();
		String sql = "select id, firstName from user";	
		try {
			state = connection.prepareStatement(sql);
			result = state.executeQuery();
			comboUN.removeAllItems();    // iztriva za da ne se poluchavat povtarqshti se elementi sled update na tablicata
			while(result.next()) {
				String item = result.getObject(1).toString() + " " +  result.getObject(2).toString();
			    comboUN.addItem(item);
		    } //end while
		} catch (SQLException e) {
			e.printStackTrace();
		}		
	}  //end method fillCombo
  
    //connection to the DataBase
    public static Connection getConnection(){
        Connection connection = null;
        
            try {
                Properties props = getSettings();
                Class.forName("org.h2.Driver");
                connection = DriverManager.getConnection(props.get(CONNECTION).toString(), props);
            } catch ( ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
        return connection;
    }    
       
    public static final String CONFIG_PATH = "src\\db.json";
    public static final String CONNECTION = "string"; //just a key
    
    /**
     * This method fetches database settings.
     * It is designed to read the database settings from a file.
     *
     * @return a instance of {@code Properties} object */
    private static Properties getSettings(){
        if (settings == null){
            Properties props = new Properties();
            String sysadmin = null;
            String pass = null;
            String connString = null;

            // zadavam kak sa bili zapisani na vunshniq file
            final String USER = "user";
            final String PASSWORD = "password";

            try {
                FileReader reader = new FileReader(CONFIG_PATH);
                JSONObject jObj = (JSONObject) new JSONParser().parse(reader);
                reader.close();
                connString = jObj.get(CONNECTION).toString();
                sysadmin = jObj.get(USER).toString();
                pass = jObj.get(PASSWORD).toString();
            } catch (ParseException | IOException e ){
                e.printStackTrace();
            } finally {
                if (sysadmin != null) {
                    props.put(USER, sysadmin);
                }
                if (pass != null) {
                    props.put(PASSWORD, pass);
                }
                if (connString != null){
                    props.put(CONNECTION, connString);
                }
                settings = props;
            }
        }
        return settings;
    }
	
}
