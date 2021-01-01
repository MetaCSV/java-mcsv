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
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class AbstractResultSetTest {
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
    public void testGetLabel() throws SQLException {
        rs.next();
        Assert.assertTrue(rs.getBoolean("boolean"));
        Assert.assertTrue((Boolean) rs.getObject("boolean"));
        Assert.assertEquals("true", rs.getString("boolean"));
        Assert.assertEquals(12354, rs.getShort("integer"));
        Assert.assertEquals(12354, rs.getInt("integer"));
        Assert.assertEquals(12354, rs.getInt("integer"));
        Assert.assertEquals(12354L, rs.getLong("integer"));
        Assert.assertEquals(12354L, rs.getObject("integer"));
    }

    @Test(expected = SQLException.class)
    public void testGetByteOverflow() throws SQLException {
        rs.next();
        rs.getByte("integer");
    }

    @Test
    public void testFeatureNotSupported0Arg()
            throws NoSuchMethodException, IllegalAccessException, SQLException {
        Class<?>[] parameterTypes = {};
        Object[] args = {};
        String[] methodNames = {"getCursorName", "isBeforeFirst",
                "isAfterLast", "isFirst", "isLast", "beforeFirst", "afterLast", "first", "last",
                "previous", "rowUpdated", "rowDeleted", "rowInserted", "insertRow", "updateRow",
                "deleteRow", "refreshRow", "cancelRowUpdates", "moveToInsertRow",
                "moveToCurrentRow"};
        testFeatureNotSupported(methodNames, parameterTypes, args);
    }

    @Test
    public void testFeatureNotSupported1ArgInt()
            throws NoSuchMethodException, IllegalAccessException, SQLException {
        Class<?>[] parameterTypes = {int.class};
        Object[] args = {1};
        String[] methodNames = {"getUnicodeStream", "absolute", "relative",
                "updateNull", "getRef", "getBlob", "getClob", "getArray", "getURL", "getRowId",
                "getNClob", "getSQLXML", "getNString", "getNCharacterStream"};
        testFeatureNotSupported(methodNames, parameterTypes, args);
    }

    @Test
    public void testFeatureNotSupported1ArgString()
            throws NoSuchMethodException, IllegalAccessException, SQLException {
        Class<?>[] parameterTypes = {String.class};
        Object[] args = {"boolean"};
        String[] methodNames =
                {"getUnicodeStream", "updateNull", "getRef", "getBlob", "getClob", "getArray",
                        "getURL", "getRowId", "getNClob", "getSQLXML", "getNString",
                        "getNCharacterStream"};
        testFeatureNotSupported(methodNames, parameterTypes, args);
    }

    @Test
    public void testFeatureNotSupported1ArgLabel()
            throws NoSuchMethodException, IllegalAccessException, SQLException {
        Class<?>[] parameterTypes = {String.class};
        Object[] args = {"boolean"};
        String[] methodNames = {"getUnicodeStream",
                "updateNull", "getRef", "getBlob", "getClob", "getArray", "getURL",
                "getNClob", "getSQLXML", "getNString", "getNCharacterStream"};
        testFeatureNotSupported(methodNames, parameterTypes, args);
    }

    @Test
    public void testFeatureNotSupported2ArgsIntReader()
            throws NoSuchMethodException, IllegalAccessException, SQLException {
        Class<?>[] parameterTypes = {int.class, Reader.class};
        Object[] args = {1, new StringReader("")};
        String[] methodNames =
                {"updateCharacterStream", "updateClob", "updateNClob", "updateNCharacterStream"};
        testFeatureNotSupported(methodNames, parameterTypes, args);
    }

    @Test
    public void testFeatureNotSupported2ArgsIntInputStream()
            throws NoSuchMethodException, IllegalAccessException, SQLException {
        Class<?>[] parameterTypes = {int.class, InputStream.class};
        Object[] args = {1, new ByteArrayInputStream(new byte[]{})};
        String[] methodNames =
                {"updateAsciiStream", "updateBinaryStream", "updateBlob"};
        testFeatureNotSupported(methodNames, parameterTypes, args);
    }

    @Test
    public void testFeatureNotSupported3ArgsIntReaderInt()
            throws NoSuchMethodException, IllegalAccessException, SQLException {
        Class<?>[] parameterTypes = {int.class, Reader.class, int.class};
        Object[] args = {1, new StringReader(""), 0};
        String[] methodNames = {"updateCharacterStream"};
        testFeatureNotSupported(methodNames, parameterTypes, args);
    }

    @Test
    public void testFeatureNotSupported3ArgsIntReaderLong()
            throws NoSuchMethodException, IllegalAccessException, SQLException {
        Class<?>[] parameterTypes = {int.class, Reader.class, long.class};
        Object[] args = {1, new StringReader(""), 0L};
        String[] methodNames =
                {"updateNClob", "updateClob", "updateNCharacterStream", "updateCharacterStream"};
        testFeatureNotSupported(methodNames, parameterTypes, args);
    }

    @Test
    public void testFeatureNotSupported2ArgsIntInputStreamLong()
            throws NoSuchMethodException, IllegalAccessException, SQLException {
        Class<?>[] parameterTypes = {int.class, InputStream.class, long.class};
        Object[] args = {1, new ByteArrayInputStream(new byte[]{}), 0L};
        String[] methodNames =
                {"updateAsciiStream", "updateBinaryStream", "updateBlob"};
        testFeatureNotSupported(methodNames, parameterTypes, args);
    }

    @Test
    public void testFeatureNotSupported2ArgsStringInputStreamLong()
            throws NoSuchMethodException, IllegalAccessException, SQLException {
        Class<?>[] parameterTypes = {String.class, InputStream.class, long.class};
        Object[] args = {"boolean", new ByteArrayInputStream(new byte[]{}), 0L};
        String[] methodNames =
                {"updateAsciiStream", "updateBinaryStream", "updateBlob"};
        testFeatureNotSupported(methodNames, parameterTypes, args);
    }

    public void testFeatureNotSupported(String[] methodNames, Class<?>[] parameterTypes,
                                        Object[] args)
            throws NoSuchMethodException, IllegalAccessException {
        for (String methodName : methodNames) {
            final Method m = ResultSet.class.getMethod(methodName, parameterTypes);
            try {
                m.invoke(rs, args);
                Assert.assertFalse(true);
            } catch (InvocationTargetException e) {
                Assert.assertEquals(SQLFeatureNotSupportedException.class,
                        e.getTargetException().getClass());
            }
        }
    }

    @Test
    public void testFeatureNotSupportedUpdateIndex()
            throws SQLException {
        rs.next();
        Assert.assertThrows(SQLFeatureNotSupportedException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                rs.updateBoolean(1, true);
            }
        });
        Assert.assertThrows(SQLFeatureNotSupportedException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                rs.updateByte(1, (byte) '0');
            }
        });
        Assert.assertThrows(SQLFeatureNotSupportedException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                rs.updateShort(1, (short) 10);
            }
        });
        Assert.assertThrows(SQLFeatureNotSupportedException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                rs.updateInt(1, 10);
            }
        });
        Assert.assertThrows(SQLFeatureNotSupportedException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                rs.updateLong(1, 10L);
            }
        });
        Assert.assertThrows(SQLFeatureNotSupportedException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                rs.updateFloat(1, 10.0f);
            }
        });
        Assert.assertThrows(SQLFeatureNotSupportedException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                rs.updateDouble(1, 10.0);
            }
        });
        Assert.assertThrows(SQLFeatureNotSupportedException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                rs.updateBigDecimal(1, BigDecimal.valueOf(10.0));
            }
        });
        Assert.assertThrows(SQLFeatureNotSupportedException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                rs.updateString(1, "foo");
            }
        });
        Assert.assertThrows(SQLFeatureNotSupportedException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                rs.updateBytes(1, new byte[]{});
            }
        });
        Assert.assertThrows(SQLFeatureNotSupportedException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                rs.updateDate(1, new Date(0));
            }
        });
        Assert.assertThrows(SQLFeatureNotSupportedException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                rs.updateTime(1, new Time(0));
            }
        });
        Assert.assertThrows(SQLFeatureNotSupportedException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                rs.updateTimestamp(1, new Timestamp(0));
            }
        });
        Assert.assertThrows(SQLFeatureNotSupportedException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                rs.updateAsciiStream(1, new ByteArrayInputStream(new byte[]{}), 10);
            }
        });
        Assert.assertThrows(SQLFeatureNotSupportedException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                rs.updateBinaryStream(1, new ByteArrayInputStream(new byte[]{}), 10);
            }
        });
    }

    @Test
    public void testFeatureNotSupportedUpdateLabel()
            throws SQLException {
        rs.next();
        Assert.assertEquals(1, rs.findColumn("boolean"));
        Assert.assertThrows(SQLFeatureNotSupportedException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                rs.updateBoolean("boolean", true);
            }
        });
        Assert.assertThrows(SQLFeatureNotSupportedException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                rs.updateByte("boolean", (byte) '0');
            }
        });
        Assert.assertThrows(SQLFeatureNotSupportedException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                rs.updateShort("boolean", (short) 10);
            }
        });
        Assert.assertThrows(SQLFeatureNotSupportedException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                rs.updateInt("boolean", 10);
            }
        });
        Assert.assertThrows(SQLFeatureNotSupportedException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                rs.updateLong("boolean", 10L);
            }
        });
        Assert.assertThrows(SQLFeatureNotSupportedException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                rs.updateFloat("boolean", 10.0f);
            }
        });
        Assert.assertThrows(SQLFeatureNotSupportedException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                rs.updateDouble("boolean", 10.0);
            }
        });
        Assert.assertThrows(SQLFeatureNotSupportedException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                rs.updateBigDecimal("boolean", BigDecimal.valueOf(10.0));
            }
        });
        Assert.assertThrows(SQLFeatureNotSupportedException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                rs.updateString("boolean", "foo");
            }
        });
        Assert.assertThrows(SQLFeatureNotSupportedException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                rs.updateBytes("boolean", new byte[]{});
            }
        });
        Assert.assertThrows(SQLFeatureNotSupportedException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                rs.updateDate("boolean", new Date(0));
            }
        });
        Assert.assertThrows(SQLFeatureNotSupportedException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                rs.updateTime("boolean", new Time(0));
            }
        });
        Assert.assertThrows(SQLFeatureNotSupportedException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                rs.updateTimestamp("boolean", new Timestamp(0));
            }
        });
        Assert.assertThrows(SQLFeatureNotSupportedException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                rs.updateAsciiStream("boolean", new ByteArrayInputStream(new byte[]{}));
            }
        });
        Assert.assertThrows(SQLFeatureNotSupportedException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                rs.updateAsciiStream("boolean", new ByteArrayInputStream(new byte[]{}), 10);
            }
        });
        Assert.assertThrows(SQLFeatureNotSupportedException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                rs.updateBinaryStream("boolean", new ByteArrayInputStream(new byte[]{}));
            }
        });
        Assert.assertThrows(SQLFeatureNotSupportedException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                rs.updateBinaryStream("boolean", new ByteArrayInputStream(new byte[]{}), 10);
            }
        });
        Assert.assertThrows(SQLFeatureNotSupportedException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                rs.updateCharacterStream("boolean", new StringReader(""));
            }
        });
        Assert.assertThrows(SQLFeatureNotSupportedException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                rs.updateCharacterStream("boolean", new StringReader(""), 10);
            }
        });
    }
}