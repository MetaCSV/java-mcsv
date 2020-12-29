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

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

public class ToolUtil {
    public static Map<Integer, String> nameBySQLType = new HashMap<Integer, String>();

    static {
        nameBySQLType.put(Types.ARRAY, "ARRAY");
        nameBySQLType.put(Types.BIGINT, "BIGINT");
        nameBySQLType.put(Types.BINARY, "BINARY");
        nameBySQLType.put(Types.BIT, "BIT");
        nameBySQLType.put(Types.BLOB, "BLOB");
        nameBySQLType.put(Types.BOOLEAN, "BOOLEAN");
        nameBySQLType.put(Types.CHAR, "CHAR");
        nameBySQLType.put(Types.CLOB, "CLOB");
        nameBySQLType.put(Types.DATALINK, "DATALINK");
        nameBySQLType.put(Types.DATE, "DATE");
        nameBySQLType.put(Types.DECIMAL, "DECIMAL");
        nameBySQLType.put(Types.DISTINCT, "DISTINCT");
        nameBySQLType.put(Types.DOUBLE, "DOUBLE");
        nameBySQLType.put(Types.FLOAT, "FLOAT");
        nameBySQLType.put(Types.INTEGER, "INTEGER");
        nameBySQLType.put(Types.JAVA_OBJECT, "JAVA_OBJECT");
        nameBySQLType.put(Types.LONGNVARCHAR, "LONGNVARCHAR");
        nameBySQLType.put(Types.LONGVARBINARY, "LONGVARBINARY");
        nameBySQLType.put(Types.LONGVARCHAR, "LONGVARCHAR");
        nameBySQLType.put(Types.NCHAR, "NCHAR");
        nameBySQLType.put(Types.NCLOB, "NCLOB");
        nameBySQLType.put(Types.NULL, "NULL");
        nameBySQLType.put(Types.NUMERIC, "NUMERIC");
        nameBySQLType.put(Types.NVARCHAR, "NVARCHAR");
        nameBySQLType.put(Types.OTHER, "OTHER");
        nameBySQLType.put(Types.REAL, "REAL");
        nameBySQLType.put(Types.REF, "REF");
        nameBySQLType.put(Types.ROWID, "ROWID");
        nameBySQLType.put(Types.SMALLINT, "SMALLINT");
        nameBySQLType.put(Types.SQLXML, "SQLXML");
        nameBySQLType.put(Types.STRUCT, "STRUCT");
        nameBySQLType.put(Types.TIME, "TIME");
        nameBySQLType.put(Types.TIMESTAMP, "TIMESTAMP");
        nameBySQLType.put(Types.TINYINT, "TINYINT");
        nameBySQLType.put(Types.VARBINARY, "VARBINARY");
        nameBySQLType.put(Types.VARCHAR, "VARCHAR");
    }
}
