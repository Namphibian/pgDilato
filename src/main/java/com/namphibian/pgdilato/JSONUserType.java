package com.namphibian.pgdilato;
import java.io.IOException;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;

//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
//import com.google.common.base.Objects;

/**
 * Hibernate {@link UserType} implementation to handle JSON objects
 *
 * @see https://docs.jboss.org/hibernate/orm/4.1/javadocs/org/hibernate/usertype/
 *      UserType.html
 */
public class JSONUserType implements UserType, ParameterizedType, Serializable {

	private static final long serialVersionUID = 1L;

	private static final ObjectMapper MAPPER = new ObjectMapper();

	private static final String CLASS_TYPE = "classType";

	private static final int[] SQL_TYPES = new int[] { Types.JAVA_OBJECT };

	private Class<?> classType;
	private int sqlType = Types.LONGVARCHAR; // before any guessing

	@Override
	public void setParameterValues(Properties params) {
		String classTypeName = params.getProperty(CLASS_TYPE);
		try {
			this.classType = ReflectHelper.classForName(classTypeName, this.getClass());
		} catch (ClassNotFoundException cnfe) {
			throw new HibernateException("classType not found", cnfe);
		}
		
		this.sqlType = Types.OTHER;

	}

	@Override
	public Object assemble(Serializable cached, Object owner) throws HibernateException {
		return this.deepCopy(cached);
	}

	@Override
	public Object deepCopy(Object value) throws HibernateException {
		/*Object copy = null;
		if (value != null) {

			try {
				return MAPPER.readValue(MAPPER.writeValueAsString(value), this.classType);
			} catch (IOException e) {
				throw new HibernateException("unable to deep copy object", e);
			}
		}
		return copy;*/
                return value;
	}

	@Override
	public Serializable disassemble(Object value) throws HibernateException {
		try {
			return MAPPER.writeValueAsString(value);
		/*} catch (JsonProcessingException e) {
			throw new HibernateException("unable to disassemble object", e);*/
		} 
                catch (IOException ex) 
                {
                    Logger.getLogger(JSONUserType.class.getName()).log(Level.SEVERE, null, ex);
                    throw new HibernateException("unable to disassemble object", ex);
                }   
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
		Object obj = null;
                String jsonValueString = rs.getString(names[0]);
               
		if (!rs.wasNull()) {
			try {
				obj = MAPPER.readValue(jsonValueString, this.classType);
			} catch (IOException e) {
				throw new HibernateException("unable to read object from result set", e);
			}
		}
		return obj;
	}

	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
		if (value == null) {
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