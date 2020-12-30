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
    public void testFeatureNotSupported0Arg()
            throws NoSuchMethodException, IllegalAccessException, SQLException {
        rs.next();
        for (String methodName : new String[]{"getCursorName", "isBeforeFirst",
                "isAfterLast", "isFirst", "isLast", "beforeFirst", "afterLast", "first", "last",
                "previous", "rowUpdated", "rowDeleted", "rowInserted"}) {
            final Method m = ResultSet.class.getMethod(methodName);
            try {
                m.invoke(rs);
                Assert.assertFalse(true);
            } catch (InvocationTargetException e) {
                Assert.assertEquals(SQLFeatureNotSupportedException.class,
                        e.getTargetException().getClass());
            }
        }
    }

    @Test
    public void testFeatureNotSupported1Arg()
            throws NoSuchMethodException, IllegalAccessException, SQLException {
        rs.next();
        for (String methodName : new String[]{"getUnicodeStream", "absolute", "relative", "updateNull"}) {
            final Method m = ResultSet.class.getMethod(methodName, int.class);
            try {
                m.invoke(rs, 1);
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
                rs.updateBytes(1, new byte[] {});
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
                rs.updateAsciiStream(1, new ByteArrayInputStream(new byte[]{}));
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
                rs.updateBinaryStream(1, new ByteArrayInputStream(new byte[]{}));
            }
        });
        Assert.assertThrows(SQLFeatureNotSupportedException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                rs.updateBinaryStream(1, new ByteArrayInputStream(new byte[]{}), 10);
            }
        });
        Assert.assertThrows(SQLFeatureNotSupportedException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                rs.updateCharacterStream(1, new StringReader(""));
            }
        });
        Assert.assertThrows(SQLFeatureNotSupportedException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                rs.updateCharacterStream(1, new StringReader(""), 10);
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
                rs.updateBytes("boolean", new byte[] {});
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