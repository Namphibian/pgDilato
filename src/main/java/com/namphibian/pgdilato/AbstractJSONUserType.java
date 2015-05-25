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
import java.sql.SQLException;
import java.sql.Types;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;

/**
 *
 * @author adm_neil
 */
public abstract class AbstractJSONUserType<T> extends AbstractMutableUserType implements Serializable{
    private static final int SQL_TYPE = Types.LONGVARCHAR;
    private static final int[] SQL_TYPES = { SQL_TYPE };
    abstract protected T getDataFromJSONString(Object jsonValueString);

   
    @Override
   public int[] sqlTypes(){
      return SQL_TYPES;
   }
   
    @Override
    public Object deepCopy(Object value){
          return value;
    }
    

    

     
    @SuppressWarnings("unused")
    @Override
    public Object nullSafeGet(ResultSet resultSet, String[] names,
            SessionImplementor session, Object owner) 
        throws HibernateException, SQLException { 
        
        String jsonString = resultSet.getString(names[0]);
        if( resultSet.wasNull() )
              return null;
        
        return getDataFromJSONString(jsonString);
    } 

    @Override
    public void nullSafeSet(PreparedStatement statement, Object value,
        int index, SessionImplementor session) throws HibernateException, SQLException { 
        if( null == value )
        {
                statement.setNull(index,SQL_TYPE);
        }
        else
        {
            
        
            try 
            {
                    statement.setString(index, (new ObjectMapper()).writeValueAsString(value));
                    //statement.setObject(index,  (new ObjectMapper()).writeValueAsString(value),SQL_TYPE);
            }
            catch (IOException ex) 
            {
                Logger.getLogger(AbstractJSONUserType.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace();
                statement.setNull(index, SQL_TYPE);
            } 
        }
    } 

 
}
