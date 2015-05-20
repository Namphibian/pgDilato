/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.namphibian.pgdilato;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;


/**
 *
 * @author adm_neil
 * @param <T>
 */
public abstract class AbstractUDTUserType<T> extends AbstractMutableUserType  implements Serializable{ 
    private static final int SQL_TYPES = Types.STRUCT;      
    abstract protected String getDBUserType();
    private Class<T> instance;
    
    public AbstractUDTUserType(Class<T> udtClass) throws InstantiationException, IllegalAccessException 
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
        final T persistClass =resultSet.getObject(names[0], this.instance);
        if( resultSet.wasNull() )
              return null;
        
        return persistClass;
    } 
  
    @Override
    public void nullSafeSet(PreparedStatement statement, Object value, int index, SessionImplementor si) throws HibernateException, SQLException {
        if (value == null) 
        {  
  
            statement.setNull(index, SQL_TYPES , getDBUserType());  
  
        } 
        else 
        {  
  
            final T persistClass = (T) value;    
            
            statement.setObject(index, persistClass, SQL_TYPES);  

        }  
    }
  

}
