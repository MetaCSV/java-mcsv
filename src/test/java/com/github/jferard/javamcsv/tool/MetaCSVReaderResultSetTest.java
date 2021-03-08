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
import org.junit.function.ThrowingRunnable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class MetaCSVReaderResultSetTest {
    private ResultSet rs;
    private Calendar c;

    @Before
    public void setUp()
            throws MetaCSVReadException, MetaCSVDataException, MetaCSVParseException, IOException {
        c = GregorianCalendar.getInstance(Locale.US);
        c.setTimeInMillis(0);
        c.set(2020, Calendar.DECEMBER, 1, 0, 0, 0);
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
        rs = Tool.readerToResultSet(reader);
    }

    @Test
    public void testFirstColObj() throws SQLException {
        Assert.assertTrue(rs.next());
        List<Object> firstCol =
                Arrays.<Object>asList(null, true, new BigDecimal("15"), c.getTime(), null, 10000.5,
                        12354L, 0.565, "Foo");
        for (int i = 1; i <= 8; i++) {
            Assert.assertEquals(firstCol.get(i), rs.getObject(i));
        }
    }

    @Test
    public void testFirstColByIndex() throws SQLException {
        Assert.assertTrue(rs.next());
        Assert.assertTrue(rs.getBoolean(1));
        Assert.assertEquals(BigDecimal.valueOf(15L), rs.getBigDecimal(2));
        Assert.assertEquals(c.getTime(), rs.getDate(3));
        Assert.assertEquals(null, rs.getDate(4));
        Assert.assertEquals(10000.5, rs.getDouble(5), 0.001);
        Assert.assertEquals(12354L, rs.getLong(6));
        Assert.assertEquals(0.565, rs.getDouble(7), 0.001);
    }

    @Test
    public void testInteger() throws SQLException {
        Assert.assertTrue(rs.next());
        Assert.assertThrows(SQLException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                rs.getByte(6);
            }
        });
        Assert.assertEquals(12354L, rs.getShort(6));
        Assert.assertEquals(12354L, rs.getInt(6));
        Assert.assertEquals(12354L, rs.getLong(6));
    }

    @Test
    public void testBigDecimal() throws SQLException {
        Assert.assertTrue(rs.next());
        Assert.assertEquals(BigDecimal.ZERO, rs.getBigDecimal(4));
        Assert.assertTrue(rs.wasNull());
        Assert.assertEquals(new BigDecimal(15L), rs.getBigDecimal(2));
    }

    @Test
    public void testGetConstants() throws SQLException {
        rs.next();
        Assert.assertEquals(ResultSet.FETCH_FORWARD, rs.getFetchDirection());
        Assert.assertEquals(0, rs.getFetchSize());
        Assert.assertEquals(ResultSet.TYPE_FORWARD_ONLY, rs.getType());
        Assert.assertEquals(ResultSet.CONCUR_READ_ONLY, rs.getConcurrency());
        Assert.assertEquals(ResultSet.HOLD_CURSORS_OVER_COMMIT, rs.getHoldability());
    }

    @Test
    public void testGetRow() throws SQLException {
        rs.next();
        Assert.assertEquals(1, rs.getRow());
        rs.next();
        Assert.assertEquals(2, rs.getRow());
    }

    @Test
    public void testSetConstants() throws SQLException {
        rs.next();
        rs.setFetchSize(10);
        Assert.assertThrows(SQLException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                rs.setFetchDirection(ResultSet.FETCH_REVERSE);
            }
        });
    }

    @Test
    public void testInteger2()
            throws SQLException, IOException, MetaCSVReadException, MetaCSVDataException,
            MetaCSVParseException {
        ByteArrayInputStream is = TestHelper.utf8InputStream(
                "integer,text\r\n" +
                        "1,\r\n");
        ByteArrayInputStream metaIs = TestHelper.utf8InputStream(
                "domain,key,value\r\n" +
                        "data,col/0/type,\"integer/ \"\r\n"
        );
        MetaCSVReader reader = MetaCSVReader.create(is, metaIs);
        rs = Tool.readerToResultSet(reader);
        rs.next();
        Assert.assertEquals(1, rs.getByte(1));
        Assert.assertFalse(rs.wasNull());
        Assert.assertEquals(1, rs.getShort(1));
        Assert.assertFalse(rs.wasNull());
        Assert.assertEquals(1, rs.getInt(1));
        Assert.assertFalse(rs.wasNull());
        Assert.assertEquals(1, rs.getLong(1));
        Assert.assertFalse(rs.wasNull());

        Assert.assertNull(rs.getObject(2));
        Assert.assertEquals(0, rs.getByte(2));
        Assert.assertTrue(rs.wasNull());
        Assert.assertEquals(0, rs.getShort(2));
        Assert.assertTrue(rs.wasNull());
        Assert.assertEquals(0, rs.getInt(2));
        Assert.assertTrue(rs.wasNull());
        Assert.assertEquals(0L, rs.getLong(2));
        Assert.assertTrue(rs.wasNull());
    }

    @Test
    public void testString() throws SQLException {
        Assert.assertTrue(rs.next());
        Assert.assertEquals("true", rs.getString(1));
        Assert.assertFalse(rs.wasNull());
        Assert.assertArrayEquals(new byte[]{'t', 'r', 'u', 'e'}, rs.getBytes(1));
        Assert.assertFalse(rs.wasNull());
        Assert.assertNull(rs.getString(4));
        Assert.assertTrue(rs.wasNull());
        Assert.assertNull(rs.getBytes(4));
        Assert.assertTrue(rs.wasNull());
    }

    @Test
    public void testStream() throws SQLException, IOException {
        rs.next();
        InputStream stream = rs.getAsciiStream(1);
        byte[] buf = getBytes(stream, 4);
        Assert.assertArrayEquals(new byte[]{'t', 'r', 'u', 'e'}, buf);
        Assert.assertFalse(rs.wasNull());

        InputStream stream2 = rs.getBinaryStream(1);
        byte[] buf2 = getBytes(stream2, 4);
        Assert.assertArrayEquals(new byte[]{'t', 'r', 'u', 'e'}, buf2);
        Assert.assertFalse(rs.wasNull());
    }

    @Test
    public void testNullStream() throws SQLException, IOException {
        rs.next();
        Assert.assertNull(rs.getAsciiStream(4));
        Assert.assertNull(rs.getBinaryStream(4));
    }

    @Test
    public void testDateAndTime() throws SQLException {
        rs.next();
        Assert.assertEquals(c.getTime(), rs.getDate(3));
        Assert.assertEquals(c.getTime(), rs.getTime(3));
        Assert.assertEquals(c.getTime(), rs.getTimestamp(3));
    }

    @Test
    public void testFindColumn() throws SQLException {
        rs.next();
        Assert.assertThrows(SQLException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                rs.findColumn("foo");
            }
        });
    }

    public byte[] getBytes(InputStream stream, int n) throws IOException {
        byte[] buf = new byte[n];
        int cs = 0;
        while (cs < n) {
            cs += stream.read(buf, cs, n-cs);
        }
        return buf;
    }

    @Test
    public void testFloat() throws SQLException {
        Assert.assertTrue(rs.next());
        Assert.assertEquals(0, rs.getFloat(4), 0.01);
        Assert.assertTrue(rs.wasNull());
        Assert.assertEquals(12354L, rs.getFloat(6), 0.01);
        Assert.assertFalse(rs.wasNull());
        Assert.assertEquals(15.0, rs.getFloat(2), 0.01);
        Assert.assertFalse(rs.wasNull());
    }

    @Test
    public void testFirstColByName() throws SQLException {
        Assert.assertTrue(rs.next());
        Assert.assertTrue(rs.getBoolean("boolean"));
        Assert.assertEquals(BigDecimal.valueOf(15L), rs.getBigDecimal("currency"));
        Assert.assertEquals(c.getTime(), rs.getDate("date"));
        Assert.assertEquals(null, rs.getDate("datetime"));
        Assert.assertEquals(10000.5, rs.getDouble("float"), 0.001);
        Assert.assertEquals(12354L, rs.getLong("integer"));
        Assert.assertEquals(0.565, rs.getDouble("percentage"), 0.001);
    }

    @Test
    public void testSecondCol() throws SQLException {
        Assert.assertTrue(rs.next());
        Assert.assertTrue(rs.next());
        Calendar c = GregorianCalendar.getInstance(Locale.US);
        c.setTimeInMillis(0);
        c.set(2020, Calendar.DECEMBER, 1, 9, 30, 55);
        List<Object> secondCol =
                Arrays.<Object>asList(null, false, new BigDecimal("-1900.5"), null, c.getTime(),
                        -520.8, -1000L,
                        -0.128, "Bar");
        for (int i = 1; i <= 8; i++) {
            Assert.assertEquals(secondCol.get(i), rs.getObject(i));
        }
        Assert.assertFalse(rs.next());
    }

    @Test
    public void testBoolean()
            throws MetaCSVReadException, MetaCSVDataException, MetaCSVParseException, IOException,
            SQLException {
        ByteArrayInputStream is = TestHelper.utf8InputStream(
                "boolean,integer,float,decimal,text\r\n" +
                        "T,1,1,1,1\r\n" +
                        "F,0,0,0,0\r\n" +
                        ",2,2,2,foo\r\n");
        ByteArrayInputStream metaIs = TestHelper.utf8InputStream(
                "domain,key,value\r\n" +
                        "data,null_value,\r\n" +
                        "data,col/0/type,boolean/T/F\r\n" +
                        "data,col/1/type,\"integer/ \"\r\n" +
                        "data,col/2/type,float//.\r\n" +
                        "data,col/3/type,decimal//.\r\n"
        );
        MetaCSVReader reader = MetaCSVReader.create(is, metaIs);
        rs = Tool.readerToResultSet(reader);
        rs.next();
        Assert.assertTrue(rs.getBoolean("boolean"));
        Assert.assertTrue(rs.getBoolean("integer"));
        Assert.assertTrue(rs.getBoolean("float"));
        Assert.assertTrue(rs.getBoolean("decimal"));
        Assert.assertTrue(rs.getBoolean("text"));
        rs.next();
        Assert.assertFalse(rs.getBoolean("boolean"));
        Assert.assertFalse(rs.wasNull());
        Assert.assertFalse(rs.getBoolean("integer"));
        Assert.assertFalse(rs.wasNull());
        Assert.assertFalse(rs.getBoolean("float"));
        Assert.assertFalse(rs.wasNull());
        Assert.assertFalse(rs.getBoolean("decimal"));
        Assert.assertFalse(rs.wasNull());
        Assert.assertFalse(rs.getBoolean("text"));
        Assert.assertFalse(rs.wasNull());
        rs.next();
        Assert.assertFalse(rs.getBoolean("boolean"));
        Assert.assertTrue(rs.wasNull());
        Assert.assertThrows(SQLException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                rs.getBoolean("integer");
            }
        });
        Assert.assertThrows(SQLException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                rs.getBoolean("float");
            }
        });
        Assert.assertThrows(SQLException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                rs.getBoolean("decimal");
            }
        });
        Assert.assertThrows(SQLException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                rs.getBoolean("text");
            }
        });
    }

    @Test
    public void testUnwrap() throws SQLException {
        rs.unwrap(MetaCSVReaderResultSet.class);
        rs.isWrapperFor(MetaCSVReaderResultSet.class);
    }

    @Test(expected = SQLException.class)
    public void testWrongUnwrap() throws SQLException {
        rs.unwrap(String.class);
    }

    @Test
    public void testWrongWrapperFor() throws SQLException {
        Assert.assertFalse(rs.isWrapperFor(String.class));
    }
}