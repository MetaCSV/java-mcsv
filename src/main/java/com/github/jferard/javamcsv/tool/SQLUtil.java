/*
 * java-mcsv - A MetaCSV library for Java
 *     Copyright (C) 2020 J. Férard <https://github.com/jferard>
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
        switch (dataType) {
            case ANY:
                return Types.JAVA_OBJECT;
            case TEXT:
                return Types.VARCHAR;
            case BOOLEAN:
                return Types.BOOLEAN;
            case CURRENCY_DECIMAL:
            case DECIMAL:
            case PERCENTAGE_DECIMAL:
                return Types.DECIMAL;
            case FLOAT:
            case PERCENTAGE_FLOAT:
                return Types.DOUBLE;
            case DATE:
            case DATETIME:
                return Types.DATE;
            case CURRENCY_INTEGER:
            case INTEGER:
                return Types.INTEGER;
        }
        throw new SQLException();
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
            case Types.TIMESTAMP:
                dt = DataType.DATE;
                break;
            case Types.TIME:
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
                dt = DataType.ANY;
        }
        return dt;
    }
}
