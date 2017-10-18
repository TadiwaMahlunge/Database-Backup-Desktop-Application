package dbcw;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class DatabaseReader {
	DatabaseMetaData dbmd;
	Connection connection;
	String[] tables = {"TABLE"};
	
	/**
	 * This is used to read the database for an established database connection.
	 * @param connection This is the connection object for a given database connection.
	 */
	DatabaseReader(Connection connection){ 
		try {
			this.connection = connection;
			dbmd = connection.getMetaData();
		}	
		catch (Exception exc){
			System.out.println("Constructor Exception: " + exc.getMessage());
		}
	}
	
	/**
	 * This will use the established database connection to read all the information needed to create tables
	 * @return A string of all the information that could be used to create a back up of the database tables.
	 */
	public String getStringToCreateTables(){
		String createTables = new String("");
		List<String> stringList = new LinkedList<>();
		
		try{
			ResultSet tableRS = dbmd.getTables(null, null, null, tables);
			
			while(tableRS.next()){
				stringList.add("CREATE TABLE " + tableRS.getString(3) + " ( \n" );
				
				ResultSet columnRS = dbmd.getColumns(null, null, tableRS.getString(3), null); 
				
				while(columnRS.next())
					stringList.add("\t" + columnRS.getString(4) + "\t" + columnRS.getString(6) + "(" + columnRS.getString(7) + "),\n");	
				
				ResultSet primaryKeyRS = dbmd.getPrimaryKeys(null, null, tableRS.getString(3));
				stringList.add("\tPRIMARY KEY (");
				
				while (primaryKeyRS.next())
						stringList.add(" "+ primaryKeyRS.getString(4) + ",");
				
				stringList.add("\b ),");
				
				ResultSet foreignKeyRS = dbmd.getImportedKeys(null, null, tableRS.getString(3));
				
				while (foreignKeyRS.next()) 
						stringList.add("\tFOREIGN KEY (" + foreignKeyRS.getString(8) + ") REFERENCES " + foreignKeyRS.getString(7) + "(" + foreignKeyRS.getString(8) + "),");
				
				stringList.add("\b\n);\n\n");		
			}
		}
		catch(Exception e){
			System.out.println("readFile Exc: " +e.getMessage() + "\n" + e.toString());
		}
		
		for (String s : stringList)
			createTables += s;
		
		return createTables;
	}
	
	/**
	 * This will use the established database connection to read all the information needed to 
	 * populate tables full of current back up information
	 * @return A string of all the information that could be used to populate a back up of the
	 *  database tables's current information.
	 */
	public String getStringToPopulateTables(){
		String populateTables = new String("");
		List<String> stringList = new LinkedList<>();
		
		try{
			ResultSet tableRS = dbmd.getTables(null, null, null, tables);
			
			while (tableRS.next()){
				Statement statement = connection.createStatement();
				ResultSet tableDataRS = statement.executeQuery("select * from " + tableRS.getString(3));
				ResultSetMetaData tableMetaData = tableDataRS.getMetaData();
			
				String[] columnSchema = new String[tableMetaData.getColumnCount()];
				ResultSet columnRS = dbmd.getColumns(null, null, tableRS.getString(3), null);
				int iterator = 0;
				while(columnRS.next()){
					if (columnRS.getString(6).length() < 5)
						columnSchema[iterator] = columnRS.getString(6);
					else
						columnSchema[iterator] = columnRS.getString(6).substring(0, 7);
					iterator++;
				}
				
				while (tableDataRS.next()){
					stringList.add("INSERT INTO " + tableRS.getString(3) + " VALUES ( ");
					for (int i = 1; i <= tableMetaData.getColumnCount(); i++){
						if (!columnSchema[i-1].equals("VARCHAR"))
							stringList.add(tableDataRS.getString(i) + ",");
						else if (columnSchema[i-1].equals("VARCHAR"))
							stringList.add("'"+ tableDataRS.getString(i) + "' ,");
					}
					stringList.add("\b );\n");
				}
			}
		}
		catch (Exception exc){ 
			System.out.println("populateTablesExc: " + exc.getMessage() + "\n" + exc.toString());
		}
	
		for (String s : stringList)
			populateTables += s;
		
		return populateTables;
	}
}
