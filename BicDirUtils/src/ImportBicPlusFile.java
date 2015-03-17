import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.pabk.sql.lang.sap.Identifier;
import org.pabk.sql.lang.sap.Insert;
import org.pabk.sql.lang.sap.SchemaName;
import org.pabk.sql.lang.sap.TableName;

public class ImportBicPlusFile {
	public static void main (String[] a) throws ClassNotFoundException, SQLException, IOException {
		BufferedReader reader = new BufferedReader(new FileReader (a[0]));
		Class.forName(a[1]);
		Connection con = DriverManager.getConnection(a[2], a[3], a[4]);
		String line = reader.readLine();
		String[] header = line.split("\t");
		while((line = reader.readLine()) != null) {
			String[] values = line.split("\t", header.length);
			TableName tableName = new TableName(new SchemaName(new Identifier(a[3])), new Identifier(a[5]));
			Insert insert = new Insert(tableName, values, header);
			ImportBicPlusFile.insert(con, insert);
		}
		reader.close();
		con.close();
	}
	
	
	public static int insert (Connection con, Insert insert) throws SQLException {
		String sql = insert.toSQLString(null);
		PreparedStatement ps = con.prepareStatement(sql);
		insert.getPreparedBuffer().setAll(ps);
		int status = ps.executeUpdate();
		ps.close();
		return status;
	}
	
}