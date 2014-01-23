package it.bioko.system.repository.memory;

import it.bioko.system.repository.core.RepositoryException;
import it.bioko.system.repository.sql.SqlConnector;
import it.bioko.system.repository.sql.query.SqlOperator;
import it.bioko.system.repository.sql.translator.annotation.Translate;
import it.bioko.system.repository.sql.translator.annotation.Translators;
import it.bioko.system.repository.sql.translator.annotation.impl.BooleanTranslator;
import it.bioko.system.repository.sql.translator.annotation.impl.DoubleTranslator;
import it.bioko.system.repository.sql.translator.annotation.impl.HSQLDBDateTimeTranslator;
import it.bioko.system.repository.sql.translator.annotation.impl.HSQLDBIDTranslator;
import it.bioko.system.repository.sql.translator.annotation.impl.HSQLDBStringTranslator;
import it.bioko.system.repository.sql.translator.annotation.impl.LocalDateTranslator;
import it.bioko.utils.domain.DomainEntity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;


@Translators(
		collection = { 
			@Translate(from = String.class, using = HSQLDBStringTranslator.class),
			@Translate(from = Long.class, to = "bigint"),
			@Translate(from = Integer.class, to = "int"),
			@Translate(from = Boolean.class, using = BooleanTranslator.class),
			@Translate(from = DateTime.class, using = HSQLDBDateTimeTranslator.class),
			@Translate(from = LocalDate.class, using = LocalDateTranslator.class),
			@Translate(from = Double.class, using = DoubleTranslator.class),
			@Translate(from = Float.class, to = "float")
		},
		idTranslator = HSQLDBIDTranslator.class
	)

public class HsqldbMemConnector extends SqlConnector {

	private static HsqldbMemConnector _instance=null;

	private HsqldbMemConnector() throws RepositoryException {
		try {
			Class.forName("org.hsqldb.jdbc.JDBCDriver" );
		} catch (ClassNotFoundException e) {
			throw new RepositoryException(e);
		}
	}
	
	@Override
	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection("jdbc:hsqldb:mem:mymemdb", "SA", "");
	}
	
	public static HsqldbMemConnector getInstance() throws RepositoryException {
//		return new HsqldbMemConnector();
		if (_instance==null) {
			_instance = new HsqldbMemConnector();
//			try {
//				System.out.println(">>>> piallo le tabelle >>>>>");
//				Statement st = _instance.getConnection().createStatement();
//				st.execute("DROP SCHEMA PUBLIC CASCADE");
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
		
		return _instance;
	}
	
	
	@Override
	public boolean tableExist(String tableName) throws SQLException {		
		Connection con = getConnection();		
		PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) AS n FROM information_schema.tables WHERE table_schema='PUBLIC' AND table_name like ?");
		ps.setString(1, tableName.toUpperCase());		
		ResultSet rs = ps.executeQuery();
		rs.next();
		boolean retVal=false;
		if (rs.getInt("n")==1)
			retVal = true;
			
		rs.close();
		ps.close();
		con.close();
		
		return retVal;
	}

	@Override
	public Long getLastInsertId(Connection con) throws SQLException {		
		Statement st = con.createStatement();
		ResultSet rs = st.executeQuery("CALL IDENTITY()");
		rs.next();
		Long id = rs.getLong(1);
		
//		System.out.println(">> last id: >> "+id);
		
		rs.close();
		st.close();
		
		
		return id;
		
	}

	@Override
	public void emptyTable(String tableName) throws SQLException {
		Connection con = getConnection();
		Statement st = con.createStatement();
		st.execute("TRUNCATE TABLE "+tableName);
		st.execute("ALTER TABLE "+tableName+" ALTER COLUMN "+DomainEntity.ID+ " RESTART WITH 1");
		
		st.close();
		con.close();
	}

	@Override
	public String createQueryFragment(String fieldName, SqlOperator operator, boolean negateOperator) {
		StringBuilder builder = new StringBuilder();
		if (operator.equals(SqlOperator.ILIKE)) {
			if (negateOperator)
				throw new UnsupportedOperationException();
			
			builder.append("UPPER(").append(fieldName).append(") LIKE UPPER(?)");
		}
		else if (operator.equals(SqlOperator.SLIKE)) {
			if (negateOperator)
				throw new UnsupportedOperationException();
			
			builder.append(fieldName).append(" LIKE ?");
		}
		
		
		else {			
			builder.append(fieldName).append(" ").append(operator.toString(negateOperator)).append(" ?").toString();
			
		}
		
		return builder.toString();		
	}
	
	
	

}
