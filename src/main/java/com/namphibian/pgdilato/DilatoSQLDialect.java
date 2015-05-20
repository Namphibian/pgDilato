/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.namphibian.pgdilato;

import java.sql.Types;
import org.hibernate.dialect.PostgreSQL9Dialect;

/**
 *
 * @author adm_neil
 */
public class DilatoSQLDialect extends PostgreSQL9Dialect{
    
    public DilatoSQLDialect()
    {
        super();
        this.registerColumnType(Types.ARRAY, "integer[]");
        this.registerColumnType(Types.ARRAY, "text[]");
        this.registerColumnType(Types.ARRAY, "timestamp with time zone[]");
        this.registerColumnType(Types.LONGVARCHAR, "json");
        this.registerColumnType(Types.LONGVARCHAR, "jsonb");
        this.registerColumnType(Types.JAVA_OBJECT, "test_person");
    }
    
}
