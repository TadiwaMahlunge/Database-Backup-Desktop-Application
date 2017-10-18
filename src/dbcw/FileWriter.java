package dbcw;
import java.io.PrintWriter;
import java.sql.*;

public class FileWriter {
	Connection connection;
	DatabaseReader dbReader;
	PrintWriter printWriter;
	
	/**
	 * This class will take a .db file URL and create a back up file for the database
	 * @param URL The URL of the database file being backed up.
	 */
	FileWriter(String URL) {
		try{
			String writeFileURL = new String(URL.substring(0 , URL.length()-3) + "_backup.txt");
			printWriter = new PrintWriter(writeFileURL, "UTF-8");
			Class.forName("org.sqlite.JDBC");
			System.out.println("Driver loaded.");

	  	connection = DriverManager.getConnection("jdbc:sqlite:"+URL);
	  	System.out.println("Database Connected");
	  	
	  	dbReader = new DatabaseReader(connection);
		}
		catch(Exception exc){
			System.out.println("Constructor Exception: " + exc.getMessage());
		}
	}

	/**
	 * Will write all read database information to the back up file and will return true if the read was successful.
	 * @return Returns true if the write was successful.
	 */
	public boolean writeToFile(){
		boolean successful = false;
		printWriter.println(dbReader.getStringToCreateTables());
		printWriter.println(dbReader.getStringToPopulateTables());
		printWriter.close();
		try {
			connection.close();
			successful = true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return successful;
	}
}
