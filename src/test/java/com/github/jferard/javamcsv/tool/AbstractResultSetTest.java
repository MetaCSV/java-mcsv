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

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

public class AbstractResultSetTest {
    @Test
    public void test() throws SQLException, IOException {
        final ResultSet rs = new AbstractResultSet() {
            @Override
            public boolean next() throws SQLException {
                return false;
            }

            @Override
            public void close() throws SQLException {

            }

            @Override
            public boolean wasNull() throws SQLException {
                return false;
            }

            @Override
            public String getString(int columnIndex) throws SQLException {
                return null;
            }

            @Override
            public boolean getBoolean(int columnIndex) throws SQLException {
                return false;
            }

            @Override
            public byte getByte(int columnIndex) throws SQLException {
                return 0;
            }

            @Override
            public short getShort(int columnIndex) throws SQLException {
                return 0;
            }

            @Override
            public int getInt(int columnIndex) throws SQLException {
                return 0;
            }

            @Override
            public long getLong(int columnIndex) throws SQLException {
                return 0;
            }

            @Override
            public float getFloat(int columnIndex) throws SQLException {
                return 1.0f;
            }

            @Override
            public double getDouble(int columnIndex) throws SQLException {
                return 0;
            }

            @Override
            public byte[] getBytes(int columnIndex) throws SQLException {
                return new byte[]{3};
            }

            @Override
            public Date getDate(int columnIndex) throws SQLException {
                return null;
            }

            @Override
            public Time getTime(int columnIndex) throws SQLException {
                return new Time(5);
            }

            @Override
            public Timestamp getTimestamp(int columnIndex) throws SQLException {
                return new Timestamp(6);
            }

            @Override
            public InputStream getAsciiStream(int columnIndex) throws SQLException {
                return new ByteArrayInputStream(new byte[] {'7'});
            }

            @Override
            public InputStream getBinaryStream(int columnIndex) throws SQLException {
                return new ByteArrayInputStream(new byte[] {'8'});
            }

            @Override
            public SQLWarning getWarnings() throws SQLException {
                return null;
            }

            @Override
            public void clearWarnings() throws SQLException {

            }

            @Override
            public ResultSetMetaData getMetaData() throws SQLException {
                return null;
            }

            @Override
            public Object getObject(int columnIndex) throws SQLException {
                return null;
            }

            @Override
            public int findColumn(String columnLabel) throws SQLException {
                return 0;
            }

            @Override
            public Reader getCharacterStream(int columnIndex) throws SQLException {
                return new StringReader("9");
            }

            @Override
            public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
                return new BigDecimal("2.0");
            }

            @Override
            public int getFetchDirection() throws SQLException {
                return 0;
            }

            @Override
            public void setFetchDirection(int direction) throws SQLException {

            }

            @Override
            public int getFetchSize() throws SQLException {
                return 0;
            }

            @Override
            public void setFetchSize(int rows) throws SQLException {

            }

            @Override
            public int getType() throws SQLException {
                return 0;
            }

            @Override
            public int getConcurrency() throws SQLException {
                return 0;
            }

            @Override
            public Statement getStatement() throws SQLException {
                return null;
            }

            @Override
            public Date getDate(int columnIndex, Calendar cal) throws SQLException {
                return null;
            }

            @Override
            public Time getTime(int columnIndex, Calendar cal) throws SQLException {
                return null;
            }

            @Override
            public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
                return null;
            }

            @Override
            public int getHoldability() throws SQLException {
                return 0;
            }

            @Override
            public boolean isClosed() throws SQLException {
                return false;
            }

            @Override
            public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
                return null;
            }

            @Override
            public InputStream getUnicodeStream(int columnIndex) {
                return new ByteArrayInputStream(new byte[] {'A'});
            }

            @Override
            public BigDecimal getBigDecimal(int columnIndex, int scale) {
                return new BigDecimal("2");
            }

            @Override
            public int getRow() {
                return 1000;
            }

            @Override
            public void updateNull(int columnIndex) throws SQLException {
            }

            @Override
            public void updateBoolean(int columnIndex, boolean b) throws SQLException {
            }

            @Override
            public void updateByte(int columnIndex, byte b) throws SQLException {
            }

            @Override
            public void updateShort(int columnIndex, short s) throws SQLException {
            }

            @Override
            public void updateInt(int columnIndex, int i) throws SQLException {
            }

            @Override
            public void updateLong(int columnIndex, long i) throws SQLException {
            }

            @Override
            public void updateFloat(int columnIndex, float f) throws SQLException {
            }

            @Override
            public void updateDouble(int columnIndex, double d) throws SQLException {
            }
        };
        Assert.assertEquals(0, rs.getByte("foo"));
        Assert.assertEquals(1.0f, rs.getFloat("foo"), 0.01);
        Assert.assertArrayEquals(new byte[]{3}, rs.getBytes("foo"));
        Assert.assertEquals(new Time(5), rs.getTime("foo"));
        Assert.assertEquals(new Timestamp(6), rs.getTimestamp("foo"));
        Assert.assertEquals('7', rs.getAsciiStream("foo").read());
        Assert.assertEquals('8', rs.getBinaryStream("foo").read());
        Assert.assertEquals('9', rs.getCharacterStream("foo").read());
        Assert.assertEquals('A', rs.getUnicodeStream("foo").read());
        Assert.assertEquals(new BigDecimal("2"), rs.getBigDecimal("foo", 2));
        Assert.assertEquals(1000, rs.getRow());
        rs.updateNull("foo");
        rs.updateBoolean("foo", true);
        rs.updateByte("foo", (byte) '1');
        rs.updateShort("foo", (short) 1);
        rs.updateInt("foo", 1);
        rs.updateLong("foo", 1L);
        rs.updateFloat("foo", 1.0f);
        rs.updateDouble("foo", 1.0);
    }
}