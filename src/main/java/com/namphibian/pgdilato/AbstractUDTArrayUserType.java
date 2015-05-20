/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.namphibian.pgdilato;

import java.io.Serializable;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Struct;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;

/**
 *
 * @author adm_neil
 * @param <T>
 */
public abstract class AbstractUDTArrayUserType<T>extends AbstractMutableUserType  implements Serializable { 
    private static final int SQL_TYPES = Types.STRUCT;      
    abstract protected String getDBUserType();
    private final Class<T> instance;
   
    abstract protected T getDataFromStructure(Struct structure) throws SQLException;//used in get
    public AbstractUDTArrayUserType(Class<T> udtClass) throws InstantiationException, IllegalAccessException 
    {
     //instance = (T) ((Class)((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments()[0]).newInstance();   
        instance = udtClass;
     
    }

   @Override
   public int[] sqlTypes()
   {
      return new int[] {SQL_TYPES};
   }

    @Override
    public Object deepCopy(Object value) throws HibernateException 
    {
        /*if (value == null) 
        {  

            return null;  

        }  
        Kryo kryoCopy = new Kryo();
        
        final T original = (T) value;    
        final T clone = kryoCopy.copy(original);
        
        
        return clone;  */
        return value;
    }
     
   @Override
    public Class returnedClass() 
    {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return this.instance;
    }
   
   @SuppressWarnings("unused")
    @Override
    public Object nullSafeGet
    (
            ResultSet resultSet, 
            String[] names,
            SessionImplementor session, 
            Object owner
    )   throws HibernateException, SQLException 
    { 

        Array sqlArray = resultSet.getArray(names[0]);
        if( resultSet.wasNull() )
        {
              return null;
                      //Collections.EMPTY_LIST;
        }
        
        
         Struct[] list = (Struct[]) sqlArray.getArray();
         ArrayList<T> result = new ArrayList<>( list.length );
         for(Struct d: list)
         {
            result.add( getDataFromStructure(d) );
         }
         return result;

    } 
  
    @Override
    public void nullSafeSet(PreparedStatement statement, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
             
        Connection connection = session.connection();
        List<T> list = (List<T>) value;
        Object[] array = list == null ? null : list.toArray();
        statement.setArray(index, connection.createArrayOf(getDBUserType(), array));
        
        

    }  
    
    
}
