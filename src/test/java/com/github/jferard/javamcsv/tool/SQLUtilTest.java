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
import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;
import java.sql.Types;

public class SQLUtilTest {
    @Test
    public void toSQL() throws SQLException {
        Assert.assertEquals(Types.JAVA_OBJECT, SQLUtil.dataTypeToSQLType(DataType.OBJECT));
    }

    @Test
    public void toDataBoolean() throws SQLException {
        Assert.assertEquals(DataType.BOOLEAN, SQLUtil.sqlTypeToDataType(Types.BOOLEAN));
    }

    @Test
    public void toDataTime() throws SQLException {
        Assert.assertEquals(DataType.DATETIME, SQLUtil.sqlTypeToDataType(Types.TIME));
    }
}