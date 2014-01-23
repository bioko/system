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

package it.bioko.system.repository.sql.util;

import it.bioko.system.repository.sql.translator.SqlTypesTranslator;
import it.bioko.utils.domain.DomainEntity;
import it.bioko.utils.domain.annotation.field.ComponingFieldsFactory;
import it.bioko.utils.domain.annotation.field.Field;
import it.bioko.utils.fields.Fields;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class SqlStatementsHelper {

	public static <DE extends DomainEntity> PreparedStatement prepareRetrieveByForeignKey(Class<DE> entityClass, String tableName, Connection connection, String foreignKey) throws SQLException {
		StringBuilder sql = new StringBuilder().
				append("select * from ").append(tableName).
				append(" where ").append(foreignKey).			
				append(" = ?");
		

		return connection.prepareStatement(sql.toString());
	}
	
	public static <DE extends DomainEntity> PreparedStatement prepareRetrieveOneByForeignKey(Class<DE> entityClass, String tableName, Connection connection, String foreignKey) 
			throws SQLException {
		StringBuilder sql = new StringBuilder().
				append("select * from ").append(tableName).
				append(" where ").append(foreignKey).
				append(" = ?").
				append(" limit 1");

		return connection.prepareStatement(sql.toString());
	}

	public static <DE extends DomainEntity> ArrayList<DE> retrieveEntities(ResultSet result, Class<DE> entityClass, SqlTypesTranslator translator) throws SQLException {
		ArrayList<DE> entities = new ArrayList<DE>();

		Set<Entry<String, Field>> fields = new HashSet<Map.Entry<String,Field>>();
		try {
			fields = ComponingFieldsFactory.createWithAnnotation(entityClass).entrySet();
		} catch (Exception exception) {
			// THIS SHOULD NEVER HAPPEN
			Logger.getLogger("engagedServer").error("retrieving fields:", exception);
			exception.printStackTrace();
		}
		
		while (!result.isClosed() && result.next()) {
			Fields entityFields = Fields.empty();
			
			for (Entry<String, Field> aFieldEntry : fields) {
				String fieldName = aFieldEntry.getKey();
				entityFields.put(fieldName,	translator.convertFromDBValue(fieldName, result, aFieldEntry.getValue()));
			}
			entityFields.put(DomainEntity.ID, translator.convertFromDBValue(DomainEntity.ID, result, null));
			
			try {
				entities.add(entityClass.getConstructor(Fields.class).newInstance(entityFields));
			} catch (Exception exception) {
				Logger.getLogger("engagedServer").error("creating entity:", exception);
				// THIS SHOULD NEVER HAPPEN
				exception.printStackTrace();
			}
		}
		
		return entities;
	}
	
//	public static String retrieveId(ResultSet generatedKeys) throws SQLException {
//		if (!generatedKeys.first()) {
//			generatedKeys.close();
//			return null;
//		} else {
//			String id = generatedKeys.getString(1);
//			generatedKeys.close();
//			return id;
//		}
//	}

	public static PreparedStatement preparedCreateStatement(Class<? extends DomainEntity> entityClass, String tableName, Connection connection) throws SQLException {
		ArrayList<String> fieldNames = new ArrayList<String>();
		try {
			fieldNames = ComponingFieldsFactory.create(entityClass);
		} catch (Exception exception) {
			// TODO Auto-generated catch block
			exception.printStackTrace();
		}
		
		String[] parameters = new String[fieldNames.size()];
		Arrays.fill(parameters, "?");
		
		StringBuilder sql = new StringBuilder().append("insert into ").append(tableName).
				append("(").append(StringUtils.join(fieldNames, ",")).append(")").
				append(" values (").append(StringUtils.join(parameters,",")).append(");");
				
//		System.out.println(">> insert >> "+sql);

		return connection.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
	}

	public static PreparedStatement preparedUpdateStatement(Class<? extends DomainEntity> entityClass, String tableName, Connection connection) throws SQLException {
		ArrayList<String> fieldCouples = new ArrayList<String>();
		try {
			ArrayList<String> fieldNames = ComponingFieldsFactory.create(entityClass);
			for (String aFieldName : fieldNames) {
				fieldCouples.add(aFieldName + "=?");
			}
		} catch (Exception exception) {
			// TODO Auto-generated catch block
			exception.printStackTrace();
		}
		
		String[] parameters = new String[fieldCouples.size()];
		Arrays.fill(parameters, "?");
		
		StringBuilder sql = new StringBuilder().append("update ").append(tableName).
				append(" set ").append(StringUtils.join(fieldCouples, ", ")).
				append(" where ").append(DomainEntity.ID).append("=?;");

		return connection.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
	}

	public static PreparedStatement preparedRetrieveByIdStatement(Class<? extends DomainEntity> entityClass, String tableName, Connection connection) throws SQLException {
		StringBuilder sql = new StringBuilder().append("select * from ").append(tableName).
				append(" where ").append(DomainEntity.ID).append(" = ?;");
		
		return connection.prepareStatement(sql.toString());
	}

	public static PreparedStatement preparedRetrieveAllStatement(Class<? extends DomainEntity> entityClass, String tableName, Connection connection) throws SQLException {
		StringBuilder sql = new StringBuilder().append("select * from ").append(tableName).append(";");
		
		return connection.prepareStatement(sql.toString());
	}

	public static PreparedStatement preparedDeleteByIdStatement(Class<? extends DomainEntity> entityClass, String tableName, Connection connection) throws SQLException {
		StringBuilder sql = new StringBuilder().append("delete from ").append(tableName).
				append(" where ").append(DomainEntity.ID).append("=?;");

		return connection.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
	}

}
