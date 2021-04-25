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

import com.github.jferard.javamcsv.MetaCSVDataException;
import com.github.jferard.javamcsv.MetaCSVParseException;
import com.github.jferard.javamcsv.MetaCSVReadException;
import com.github.jferard.javamcsv.MetaCSVReader;
import com.github.jferard.javamcsv.TestHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.List;

public class MetaCSVResultSetMetaDataTest {
    private ResultSetMetaData metaData;

    @Before
    public void setUp()
            throws MetaCSVReadException, MetaCSVDataException, MetaCSVParseException, IOException,
            SQLException {
        ByteArrayInputStream is = TestHelper.utf8InputStream(
                "boolean,currency,date,datetime,float,integer,percentage,text\r\n" +
                        "T,$15,01/12/2020,NULL,\"10,000.5\",12 354,56.5%,Foo\r\n" +
                        "F,\"$-1,900.5\",NULL,2020-12-01 09:30:55,-520.8,-1 000,-12.8%,Bar\r\n");
        ByteArrayInputStream metaIs = TestHelper.utf8InputStream(
                "domain,key,value\r\n" +
                        "data,null_value,NULL\r\n" +
                        "data,col/0/type,boolean/T/F\r\n" +
                        "data,col/1/type,\"currency/pre/$/decimal/,/.\"\r\n" +
                        "data,col/2/type,date/dd\\/MM\\/yyyy\r\n" +
                        "data,col/3/type,datetime/yyyy-MM-dd HH:mm:ss\r\n" +
                        "data,col/4/type,\"float/,/.\"\r\n" +
                        "data,col/5/type,\"integer/ \"\r\n" +
                        "data,col/6/type,\"percentage/post/%/float/,/.\"\r\n");
        MetaCSVReader reader = MetaCSVReader.create(is, metaIs);
        ResultSet rs = Tool.readerToResultSet(reader);
        metaData = rs.getMetaData();
    }

    @Test
    public void testColumnCount() throws SQLException {
        Assert.assertEquals(8, metaData.getColumnCount());
    }

    @Test
    public void testHeader() throws SQLException {
        List<String> header =
                Arrays.asList(null, "boolean", "currency", "date", "datetime", "float", "integer",
                        "percentage", "text");
        for (int i = 1; i <= 8; i++) {
            Assert.assertEquals(header.get(i), metaData.getColumnName(i));
            Assert.assertEquals(header.get(i), metaData.getColumnLabel(i));
        }
    }

    @Test
    public void testColumnTypeName() throws SQLException {
        List<String> typeNames =
                Arrays.asList(null, "BOOLEAN", "CURRENCY_DECIMAL", "DATE", "DATETIME", "FLOAT",
                        "INTEGER",
                        "PERCENTAGE_FLOAT", "TEXT");
        for (int i = 1; i <= 8; i++) {
            Assert.assertEquals(typeNames.get(i), metaData.getColumnTypeName(i));
        }
    }

    @Test
    public void testColumnType() throws SQLException {
        List<Integer> types =
                Arrays.asList(null, Types.BOOLEAN, Types.DECIMAL, Types.DATE, Types.TIMESTAMP,
                        Types.DOUBLE, Types.INTEGER,
                        Types.DOUBLE, Types.VARCHAR);
        for (int i = 1; i <= 8; i++) {
            Assert.assertEquals((long) types.get(i), metaData.getColumnType(i));
        }
    }

    @Test
    public void testColumnClassName() throws SQLException {
        List<String> classNames =
                Arrays.asList(null, "java.lang.Boolean", "java.math.BigDecimal", "java.util.Date",
                        "java.util.Date", "java.lang.Double", "java.lang.Long", "java.lang.Double",
                        "java.lang.String");
        for (int i = 1; i <= 8; i++) {
            Assert.assertEquals(classNames.get(i), metaData.getColumnClassName(i));
        }
    }

    @Test
    public void testIsCurrency() throws SQLException {
        List<Boolean> cs =
                Arrays.asList(null, false, true, false, false, false, false, false, false);
        for (int i = 1; i <= 8; i++) {
            Assert.assertEquals(cs.get(i), metaData.isCurrency(i));
        }
    }

    @Test
    public void testIsSigned() throws SQLException {
        List<Boolean> cs =
                Arrays.asList(null, false, true, false, false, true, true, true, false);
        for (int i = 1; i <= 8; i++) {
            Assert.assertEquals(cs.get(i), metaData.isSigned(i));
        }
    }

    @Test
    public void testConstants() throws SQLException {
        Assert.assertFalse(metaData.isAutoIncrement(1));
        Assert.assertTrue(metaData.isCaseSensitive(1));
        Assert.assertFalse(metaData.isSearchable(1));
        Assert.assertEquals(ResultSetMetaData.columnNullableUnknown, metaData.isNullable(1));
        Assert.assertEquals(0, metaData.getColumnDisplaySize(1));
        Assert.assertEquals("", metaData.getSchemaName(1));
        Assert.assertEquals(0, metaData.getPrecision(1));
        Assert.assertEquals(0, metaData.getScale(1));
        Assert.assertEquals("", metaData.getTableName(1));
        Assert.assertEquals("", metaData.getCatalogName(1));
        Assert.assertTrue(metaData.isReadOnly(1));
        Assert.assertFalse(metaData.isWritable(1));
        Assert.assertFalse(metaData.isDefinitelyWritable(1));
    }

    @Test
    public void testUnwrap() throws SQLException {
        metaData.unwrap(MetaCSVResultSetMetaData.class);
        metaData.isWrapperFor(MetaCSVResultSetMetaData.class);
    }

    @Test(expected = SQLException.class)
    public void testWrongUnwrap() throws SQLException {
        metaData.unwrap(String.class);
    }

    @Test
    public void testWrongWrapperFor() throws SQLException {
        Assert.assertFalse(metaData.isWrapperFor(String.class));
    }
}