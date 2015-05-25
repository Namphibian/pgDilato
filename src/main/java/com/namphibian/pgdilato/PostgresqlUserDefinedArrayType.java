/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.namphibian.pgdilato;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Struct;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
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
public class PostgresqlUserDefinedArrayType extends AbstractParameterizedMutableUserType{
    private static final long serialVersionUID = 1L;

	private static final String CLASS_TYPE = "classType";
        private static final String PGSQL_TYPE = "userType";
        private static final String CONVERTER_TYPE = "converterType";
	private static final int[] SQL_TYPES = new int[] {  Types.STRUCT   };

	private Class<?> classType;
        private PgStructToClassInterface converter;
        private String pgsqlType;
	private int sqlType = Types.STRUCT; // before any guessing
        /*public PostgresqlUserDefinedArrayType(Class<?> udtClass) throws InstantiationException, IllegalAccessException 
        {
            //instance = (T) ((Class)((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments()[0]).newInstance();   
               classType = udtClass;

        }*/
	@Override
	public void setParameterValues(Properties params) {
                
		String classTypeName = params.getProperty(CLASS_TYPE);
		try {
			this.classType = ReflectHelper.classForName(classTypeName, this.getClass());
		} catch (ClassNotFoundException cnfe) {
			throw new HibernateException("classType not found", cnfe);
		}
                String converterTypeName = params.getProperty(CONVERTER_TYPE);
		try {
                    try {
                        converter =  (PgStructToClassInterface) ReflectHelper.classForName(converterTypeName, this.getClass()).newInstance();
                        //this.classType = ReflectHelper.classForName(classTypeName, this.getClass());
                    } catch (InstantiationException ex) {
                        Logger.getLogger(PostgresqlUserDefinedArrayType.class.getName()).log(Level.SEVERE, null, ex);
                        throw new HibernateException(ex.getMessage());
                    } catch (IllegalAccessException ex) {
                        Logger.getLogger(PostgresqlUserDefinedArrayType.class.getName()).log(Level.SEVERE, null, ex);
                        throw new HibernateException(ex.getMessage());
                    }
		} catch (ClassNotFoundException cnfe) {
			throw new HibernateException("classType not found", cnfe);
		}
		
		this.sqlType = Types.OTHER;
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
	public Object nullSafeGet(ResultSet resultSet, String[] names,	SessionImplementor session, Object owner) throws HibernateException, SQLException {
            
            Array sqlArray = resultSet.getArray(names[0]);
            if( resultSet.wasNull() )
            {
                  return null;
                          //Collections.EMPTY_LIST;
            }
        
            
            Struct[] list = (Struct[]) sqlArray.getArray();
            ArrayList<Object> result = new ArrayList<>( list.length );
           

                for(Struct d: list)
                {
                                  
                    
                    result.add(converter.getDataFromStructure(d));

                    

                }
   
            
           
            return result;
	}

	@Override
	public void nullSafeSet(PreparedStatement statement, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
            Connection connection = session.connection();
            List<Object> list = (List<Object>) value;
            Object[] array = list == null ? null : list.toArray();
            try
            {
                statement.setArray(index, connection.createArrayOf(this.pgsqlType, array));
            } 
            catch(Exception e)
            {
                throw new HibernateException("Error during persisting of data: "+e.getLocalizedMessage());
            }
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
