/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.namphibian.pgdilato;

import java.io.IOException;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.internal.util.ReflectHelper;

/**
 *
 * @author adm_neil
 */
public class PostgresqlUserDefinedType extends AbstractParameterizedMutableUserType{
    private static final long serialVersionUID = 1L;

	private static final String CLASS_TYPE = "classType";
        private static final String PGSQL_TYPE = "userType";
	private static final int[] SQL_TYPES = new int[] {  Types.STRUCT   };

	private Class<? extends SQLData>classType;
        private String pgsqlType;
	private int sqlType = Types.STRUCT; // before any guessing

	@Override
	public void setParameterValues(Properties params) {
                
		String classTypeName = params.getProperty(CLASS_TYPE);
		try {
			this.classType = ReflectHelper.classForName(classTypeName, this.getClass());
		} catch (ClassNotFoundException cnfe) {
			throw new HibernateException("classType not found", cnfe);
		}
		
		this.sqlType = Types.JAVA_OBJECT;
                this.pgsqlType = params.getProperty(PGSQL_TYPE);

	}

	@Override
	public Object assemble(Serializable cached, Object owner) throws HibernateException {
		return this.deepCopy(cached);
	}

	@Override
	public Object deepCopy(Object value) throws HibernateException {
                return value;
	}



	@Override
	public boolean equals(Object x, Object y) throws HibernateException {
		return Objects.equals(x, y);
	}

	@Override
	public int hashCode(Object x) throws HibernateException {
		return Objects.hashCode(x);
	}

	@Override
	public boolean isMutable() {
		return true;
	}

	@Override
	public Object nullSafeGet(ResultSet rs, String[] names,	SessionImplementor session, Object owner) throws HibernateException, SQLException {
            
            final Object persistClass =rs.getObject(names[0], this.classType);
            if( rs.wasNull() )
              return null;
        
            return persistClass;	
            
           /* Object obj = null;
                String jsonValueString = rs.getString(names[0]);
               
		if (!rs.wasNull()) {
			try {
				obj = MAPPER.readValue(jsonValueString, this.classType);
			} catch (IOException e) {
				throw new HibernateException("unable to read object from result set", e);
			}
		}
		return obj;*/
	}

	@Override
	public void nullSafeSet(PreparedStatement statement, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
            if (value == null) 
            {  

                    statement.setNull(index, sqlType , this.pgsqlType);  

            } 
            else 
            {  

               // final Object persistClass = this.classType.getClass(). value;    

                statement.setObject(index, this.classType.cast(value), sqlType);  

            }  
		/*if (value == null) {
			st.setNull(index, this.sqlType);
		} else {
			try {
				//st.setObject(index, MAPPER.writeValueAsString(value), this.sqlType);
                            st.setString(index, MAPPER.writeValueAsString(value));
			} catch (JsonProcessingException e) {
				throw new HibernateException("unable to set object to result set", e);
			} catch (IOException ex) 
                        {
                            	throw new HibernateException("unable to set object to result set", ex);
                        }
		}*/
	}

	@Override
	public Object replace(Object original, Object target, Object owner) throws HibernateException {
		return this.deepCopy(original);
	}

	@Override
	public Class<?> returnedClass() {
		return this.classType;
	}

	@Override
	public int[] sqlTypes() {
		return SQL_TYPES;
	}
}
