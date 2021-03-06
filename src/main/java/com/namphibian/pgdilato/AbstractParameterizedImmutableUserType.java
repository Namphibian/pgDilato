/**
 * Copyright (c) 2014-2015 Eclectic Logic LLC
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.namphibian.pgdilato;

import java.io.Serializable;

import org.hibernate.HibernateException;

/**
 * Adapted from https://forum.hibernate.org/viewtopic.php?t=946973 and
 * http://blog.xebia.com/2009/11/09/understanding-and-writing-hibernate-user-types/
 * 
 * @author Neil Franken.
 *
 */
public abstract class AbstractParameterizedImmutableUserType extends AbstractParameterizedUserType {

    @Override
    public boolean isMutable() {
        return false;
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
    @Override
      public Object deepCopy(Object obj) throws  HibernateException
    {
        return obj;
    }
    @Override
    public int hashCode(Object obj) throws HibernateException
    {
        return obj.hashCode();
    }
}
