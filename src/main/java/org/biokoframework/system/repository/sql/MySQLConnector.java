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

package org.biokoframework.system.repository.sql;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.biokoframework.system.repository.sql.query.SqlOperator;
import org.biokoframework.system.repository.sql.translator.annotation.Translate;
import org.biokoframework.system.repository.sql.translator.annotation.Translators;
import org.biokoframework.system.repository.sql.translator.annotation.impl.*;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import javax.inject.Inject;
import javax.inject.Named;
import java.sql.*;


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

	private static final Logger LOGGER = Logger.getLogger(MySQLConnector.class);
	
	private String url;

    @Inject
	public MySQLConnector(@Named("dbUrl") String dbUrl, @Named("dbName") String dbName, @Named("dbUser") String dbUser,
                          @Named("dbPassword") String dbPassword, @Named("dbPort") String dbPort) {
		configureConnectionURL(dbUrl, dbName, dbUser, dbPassword, dbPort);		
	}
	
	private void configureConnectionURL(String dbUrl, String dbName, String dbUser, String dbPassword, String dbPort) {
		try {
		    Class.forName("com.mysql.jdbc.Driver");
		    url = StringUtils.replace(dbUrl, "${dbName}", dbName);
		    url = StringUtils.replace(url, "${dbUser}", dbUser);
		    url = StringUtils.replace(url, "${dbPassword}", dbPassword);
		    url = StringUtils.replace(url, "${dbPort}", dbPort);
		} catch (Exception e) {
		    e.printStackTrace();
		}
	}
	
    public Connection getConnection() throws SQLException {
        Connection connectionInstance;
        try {
//			if (_connectionInstance == null || _connectionInstance.isClosed()) {
				connectionInstance = DriverManager.getConnection(url);
//			}
		} catch (SQLException exception) {
			if (exception.getMessage().contains("Unknown database")) {
				createDatabase();
			} else if (exception.getMessage().contains("Access denied")) {
				grantUser();
			}
			LOGGER.error("Connection creation", exception);
			exception.printStackTrace();
			throw exception;
		}
		return connectionInstance;
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