/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.namphibian.pgdilato;

import java.io.Serializable;
import org.hibernate.HibernateException;

/**
 *
 * @author adm_neil
 */
public abstract class AbstractMutableUserType extends AbstractUserType {

    @Override
    public boolean isMutable() {
        return true;
    }


    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return deepCopy(cached);
    }


    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) deepCopy(value);
    }


    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return deepCopy(original);
    }
    
}
