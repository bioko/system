package it.bioko.system.repository.sql;

import it.bioko.system.repository.sql.query.SqlOperator;
import it.bioko.system.repository.sql.translator.annotation.Translate;
import it.bioko.system.repository.sql.translator.annotation.Translators;
import it.bioko.system.repository.sql.translator.annotation.impl.BooleanTranslator;
import it.bioko.system.repository.sql.translator.annotation.impl.DoubleTranslator;
import it.bioko.system.repository.sql.translator.annotation.impl.ISODateTimeTranslator;
import it.bioko.system.repository.sql.translator.annotation.impl.LocalDateTranslator;
import it.bioko.system.repository.sql.translator.annotation.impl.MySQLIDTranslator;
import it.bioko.system.repository.sql.translator.annotation.impl.StringTranslator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;


@Translators(
		collection = { 
			@Translate(from = String.class, using = StringTranslator.class),
			@Translate(from = Long.class, to = "bigint"),
			@Translate(from = Integer.class, to = "int"),
			@Translate(from = Boolean.class, using = BooleanTranslator.class),
			@Translate(from = DateTime.class, using = ISODateTimeTranslator.class),
			@Translate(from = LocalDate.class, using = LocalDateTranslator.class),
			@Translate(from = Double.class, using = DoubleTranslator.class),
			@Translate(from = Float.class, to = "float")
		},
		idTranslator = MySQLIDTranslator.class
	)

public class MySQLConnector extends SqlConnector {

	private String _url;
	private Connection _connectionInstance;
	
	

	public MySQLConnector(String dbUrl, String dbName, String dbUser, String dbPassword, String dbPort) {		
		configureConnectionURL(dbUrl, dbName, dbUser, dbPassword, dbPort);		
	}
	
	private void configureConnectionURL(String dbUrl, String dbName, String dbUser, String dbPassword, String dbPort) {
		try {
		    Class.forName("com.mysql.jdbc.Driver");
		    _url = StringUtils.replace(dbUrl, "${dbName}", dbName);
		    _url = StringUtils.replace(_url, "${dbUser}", dbUser);
		    _url = StringUtils.replace(_url, "${dbPassword}", dbPassword);
		    _url = StringUtils.replace(_url, "${dbPort}", dbPort);
		} catch (Exception e) {
		    e.printStackTrace();
		}
	}
	
    public Connection getConnection() throws SQLException {
		try {
//			if (_connectionInstance == null || _connectionInstance.isClosed()) {
				_connectionInstance = DriverManager.getConnection(_url);
//			}
		} catch (SQLException exception) {
			if (exception.getMessage().contains("Unknown database")) {
				createDatabase();
			} else if (exception.getMessage().contains("Access denied")) {
				grantUser();
			}
			Logger.getLogger("engagedServer").error("Connection creation", exception);
			exception.printStackTrace();
			throw exception;
		}
		return _connectionInstance;
    }
		

	private void createDatabase() {
    	System.out.println("[EASY MAN]! Create the database");    	
	}
	
	private void grantUser() {
		System.out.println("[EASY MAN]! Setup grants for user on database");
	}

	@Override
	public String getCreateTableTail() {
		return "engine='InnoDB' character set=utf8";
	}

	@Override
	public Long getLastInsertId(Connection con) throws SQLException {		
		Statement st = con.createStatement();
		ResultSet rs = st.executeQuery("SELECT LAST_INSERT_ID()");
		rs.next();
		return rs.getLong(1);
		
	}

	@Override
	public String createQueryFragment(String fieldName, SqlOperator operator, boolean negateOperator) {
		StringBuilder builder = new StringBuilder();
		if (operator.equals(SqlOperator.ILIKE)) {
			if (negateOperator)
				throw new UnsupportedOperationException();			
			builder.append(fieldName).append(" LIKE ?");
		} 
		if (operator.equals(SqlOperator.SLIKE)) {
			if (negateOperator)
				throw new UnsupportedOperationException();			
			builder.append(fieldName).append(" BINARY LIKE ?");
		} 
		
		else {			
			builder.append(fieldName).append(" ").append(operator.toString(negateOperator)).append(" ?").toString();
			
		}
		
		return builder.toString();		
	}

}