/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.namphibian.pgdilato;

import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.Struct;

/**
 *
 * @author adm_neil
 */
public interface PgStructToClassInterface<T> {
    public  T getDataFromStructure(Struct structure) throws SQLException;
}
