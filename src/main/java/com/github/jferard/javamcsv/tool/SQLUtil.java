/*
 * java-mcsv - A MetaCSV library for Java
 *     Copyright (C) 2020-2021 J. Férard <https://github.com/jferard>
 *
 * This file is part of java-mcsv.
 *
 * java-mcsv is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * java-mcsv is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 *  for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses />.
 */

package com.github.jferard.javamcsv.tool;

import com.github.jferard.javamcsv.DataType;

import java.sql.SQLException;
import java.sql.Types;

public class SQLUtil {
    public static int dataTypeToSQLType(DataType dataType) throws SQLException {
        int t = 0;
        switch (dataType) {
            case OBJECT:
                t = Types.JAVA_OBJECT;
                break;
            case TEXT:
                t = Types.VARCHAR;
                break;
            case BOOLEAN:
                t = Types.BOOLEAN;
                break;
            case CURRENCY_DECIMAL:
            case DECIMAL:
            case PERCENTAGE_DECIMAL:
                t =  Types.DECIMAL;
                break;
            case FLOAT:
            case PERCENTAGE_FLOAT:
                t =  Types.DOUBLE;
                break;
            case DATE:
                t =  Types.DATE;
                break;
            case DATETIME:
                t =  Types.TIMESTAMP;
                break;
            case CURRENCY_INTEGER:
            case INTEGER:
                t = Types.INTEGER;
                break;
        }
        return t;
    }

    public static DataType sqlTypeToDataType(int sqlType) {
        DataType dt;
        switch (sqlType) {
            case Types.BOOLEAN:
                dt = DataType.BOOLEAN;
                break;
            case Types.BIT:
            case Types.TINYINT:
            case Types.SMALLINT:
            case Types.INTEGER:
            case Types.BIGINT:
                dt = DataType.INTEGER;
                break;
            case Types.FLOAT:
            case Types.REAL:
            case Types.DOUBLE:
                dt  =DataType.FLOAT;
                break;
            case Types.NUMERIC:
            case Types.DECIMAL:
                dt = DataType.DECIMAL;
                break;
            case Types.CHAR:
            case Types.VARCHAR:
            case Types.LONGVARCHAR:
            case Types.NCHAR:
            case Types.NVARCHAR:
            case Types.LONGNVARCHAR:
                dt = DataType.TEXT;
                break;
            case Types.DATE:
                dt = DataType.DATE;
                break;
            case Types.TIME:
            case Types.TIMESTAMP:
                dt = DataType.DATETIME;
                break;
            case Types.BINARY:
            case Types.VARBINARY:
            case Types.LONGVARBINARY:
            case Types.NULL:
            case Types.OTHER:
            case Types.JAVA_OBJECT:
            case Types.DISTINCT:
            case Types.STRUCT:
            case Types.ARRAY:
            case Types.BLOB:
            case Types.CLOB:
            case Types.REF:
            case Types.DATALINK:
            case Types.ROWID:
            case Types.NCLOB:
            case Types.SQLXML:
            default:
                dt = DataType.OBJECT;
        }
        return dt;
    }
}
