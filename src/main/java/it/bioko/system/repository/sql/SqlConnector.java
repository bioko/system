package it.bioko.system.repository.sql;

import it.bioko.system.repository.sql.query.SqlOperator;
import it.bioko.system.repository.sql.translator.SqlTypesTranslator;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class SqlConnector {	
	
	protected SqlTypesTranslator _typesTranslator;
	
	
	public SqlConnector() {
		try {
			_typesTranslator = new SqlTypesTranslator(this.getClass());
		} catch (Exception e) {
			System.out.println("[EASY MAN] Probably you brokens type translate annotations on MySQLConnector");
		} 
	}
	
	public abstract Connection getConnection() throws SQLException;
	
	public SqlTypesTranslator getTypesTranslator() {
		return _typesTranslator;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	////////////////////////// DB implementation specific stuff /////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////
	public String getCreateTableTail() {
		return "";
	}
	
	public boolean tableExist(String tableName) throws SQLException {
		Connection connection = getConnection();
		DatabaseMetaData meta = connection.getMetaData();

		ResultSet result = meta.getTables(null, null, tableName, new String[] {"TABLE"});
		boolean exist = result.first();
	
		result.close();
		connection.close();
		
		return exist;
	}
	
	public abstract Long getLastInsertId(Connection con) throws SQLException;

	public void emptyTable(String tableName) throws SQLException {
		Connection con = getConnection();
		Statement st = con.createStatement();
		st.execute("TRUNCATE TABLE "+tableName);
		st.close();
		con.close();
	}

	public abstract String createQueryFragment(String fieldName, SqlOperator operator, boolean negateOperator);

	
}
