/*
 * Copyright (c) 2014																 
 *	Mikol Faro			<mikol.faro@gmail.com>
 *	Simone Mangano		<simone.mangano@ieee.org>
 *	Mattia Tortorelli	<mattia.tortorelli@gmail.com>
 *
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 */

package org.biokoframework.system.repository.memory;

import org.biokoframework.system.repository.sql.SqlConnector;
import org.biokoframework.system.repository.sql.query.SqlOperator;
import org.biokoframework.system.repository.sql.translator.annotation.Translate;
import org.biokoframework.system.repository.sql.translator.annotation.Translators;
import org.biokoframework.system.repository.sql.translator.annotation.impl.*;
import org.biokoframework.utils.domain.DomainEntity;
import org.biokoframework.utils.repository.RepositoryException;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.sql.*;


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
