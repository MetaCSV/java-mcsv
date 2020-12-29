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

import com.github.jferard.javamcsv.MetaCSVMetaData;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;

public class MetaCSVResultSetMetaData implements ResultSetMetaData {
    private MetaCSVMetaData metaData;
    private List<String> header;
    private Map<String, Integer> indexByLabel;

    public MetaCSVResultSetMetaData(MetaCSVMetaData metaData,
                                    List<String> header,
                                    Map<String, Integer> indexByLabel) {
        this.metaData = metaData;
        this.header = header;
        this.indexByLabel = indexByLabel;
    }

    @Override
    public int getColumnCount() throws SQLException {
        return this.header.size();
    }

    @Override
    public boolean isAutoIncrement(int column) throws SQLException {
        return false;
    }

    @Override
    public boolean isCaseSensitive(int column) throws SQLException {
        return true;
    }

    @Override
    public boolean isSearchable(int column) throws SQLException {
        return false;
    }

    @Override
    public boolean isCurrency(int column) throws SQLException {
        String s = this.metaData.getTypeName(column - 1);
        if (s == null) {
            return false;
        }
        return s.startsWith("currency");
    }

    @Override
    public int isNullable(int column) throws SQLException {
        return ResultSetMetaData.columnNullableUnknown;
    }

    @Override
    public boolean isSigned(int column) throws SQLException {
        return true;
    }

    @Override
    public int getColumnDisplaySize(int column) throws SQLException {
        return 0;
    }

    @Override
    public String getColumnLabel(int column) throws SQLException {
        return this.getColumnName(column);
    }

    @Override
    public String getColumnName(int column) throws SQLException {
        return this.header.get(column - 1);
    }

    @Override
    public String getSchemaName(int column) throws SQLException {
        return "";
    }

    @Override
    public int getPrecision(int column) throws SQLException {
        return 0;
    }

    @Override
    public int getScale(int column) throws SQLException {
        return 0;
    }

    @Override
    public String getTableName(int column) throws SQLException {
        return "";
    }

    @Override
    public String getCatalogName(int column) throws SQLException {
        return "";
    }

    @Override
    public int getColumnType(int column) throws SQLException {
        String type = metaData.getTypeName(column - 1);
        if (type == null || type.equals("any")) {
            return Types.JAVA_OBJECT;
        } else if (type.equals("text")) {
            return Types.VARCHAR;
        } else if (type.equals("boolean")) {
            return Types.BOOLEAN;
        } else if (type.endsWith("decimal")) {
            return Types.DECIMAL;
        } else if (type.endsWith("float")) {
            return Types.DOUBLE;
        } else if (type.equals("date") || type.equals("datetime")) {
            return Types.DATE;
        } else if (type.endsWith("integer")) {
            return Types.INTEGER;
        }
        throw new SQLException();
    }

    @Override
    public String getColumnTypeName(int column) throws SQLException {
        return metaData.getTypeName(column - 1);
    }

    @Override
    public boolean isReadOnly(int column) throws SQLException {
        return true;
    }

    @Override
    public boolean isWritable(int column) throws SQLException {
        return false;
    }

    @Override
    public boolean isDefinitelyWritable(int column) throws SQLException {
        return false;
    }

    @Override
    public String getColumnClassName(int column) throws SQLException {
        return metaData.getType(column - 1).getCanonicalName();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isAssignableFrom(getClass())) {
            return iface.cast(this);
        }
        throw new SQLException("Cannot unwrap to :" + iface.getName());
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isAssignableFrom(getClass());
    }
}
