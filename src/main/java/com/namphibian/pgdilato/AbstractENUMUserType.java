/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.namphibian.pgdilato;


import org.hibernate.HibernateException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import org.hibernate.engine.spi.SessionImplementor;

/**
 *
 * @author adm_neil
 */
public class AbstractENUMUserType <T,E extends Enum<E>> extends AbstractParameterizedImmutableUserType{ 
private int sqlType;
private Class<E> clazz = null;
private HashMap<String, E> enumMap;
private HashMap<E, String> valueMap;

public AbstractENUMUserType(Class<E> clazz, E[] enumValues, String method, int sqlType) throws
       NoSuchMethodException, InvocationTargetException,
       IllegalAccessException
    {
        this.clazz = clazz;
        enumMap = new HashMap<String, E>(enumValues.length);
        valueMap = new HashMap<E, String>(enumValues.length);
        Method m = clazz.getMethod(method);

        for (E e: enumValues) 
        {

            @SuppressWarnings("unchecked")
            T value = (T)m.invoke(e);

            enumMap.put(value.toString(), e);
            valueMap.put(e, value.toString());
        }
        this.sqlType = sqlType;
    }
    public boolean isMutable()
    {
          return false;
    }

    public Object nullSafeGet(ResultSet rs, String[] names, Object owner)
    throws HibernateException, SQLException
    {
          String value = rs.getString(names[0]);
          if (!rs.wasNull()) {
                return enumMap.get(value);
          }
          return null;
    }

    public void nullSafeSet(PreparedStatement ps, Object obj, int index)
    throws HibernateException, SQLException
    {
            if (obj == null) {
                  ps.setNull(index, sqlType);
            } else {
                  ps.setObject(index, valueMap.get(obj), sqlType);
            }
    }


    public Class<E> returnedClass() {
            return clazz;
    }

    public int[] sqlTypes()
    {
            return new int[] {sqlType};
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] strings, SessionImplementor si, Object o) throws HibernateException, SQLException {
        String value = rs.getString(strings[0]);
        if (!rs.wasNull()) {
              return enumMap.get(value);
        }
        return null;
    }

    @Override
    public void nullSafeSet(PreparedStatement ps, Object obj, int index, SessionImplementor si) throws HibernateException, SQLException {
          if (obj == null) {
                ps.setNull(index, sqlType);
          } else {
                ps.setObject(index, valueMap.get(obj), sqlType);
          }
    }
}
