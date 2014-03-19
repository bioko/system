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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.biokoframework.system.repository.sql.query.SqlOperator;
import org.biokoframework.system.repository.sql.translator.SqlTypesTranslator;

public abstract class SqlConnector {

    private static final Logger LOGGER = Logger.getLogger(SqlConnector.class);
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
        Connection connection = null;
        ResultSet result = null;
        try {
			connection = getConnection();
			DatabaseMetaData meta = connection.getMetaData();

			result = meta.getTables(null, null, tableName, new String[] {"TABLE"});
			boolean exist = result.first();

			result.close();
			connection.close();
			return exist;
		} catch (SQLException exception) {
			 closeDumbSql(connection, null, result);
            LOGGER.error("Sql Exception", exception);
            return false;
		}

	}
	
	public abstract Long getLastInsertId(Connection con) throws SQLException;

	public void emptyTable(String tableName) throws SQLException {
		Connection con = null;
		Statement st = null;
		try {
			con = getConnection();
			st = con.createStatement();
			st.execute("TRUNCATE TABLE "+tableName);
			st.close();
			con.close();
		} catch (SQLException exception) {
			LOGGER.error("Sql Exception", exception);
			closeDumbSql(con, st, null);
		}
	}

	public abstract String createQueryFragment(String fieldName, SqlOperator operator, boolean negateOperator);

	    private void closeDumbSql(Connection connection, Statement statement, ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException closeException) { }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException closeException) { }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException closeException) { }
        }
    }

}
