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

import com.github.jferard.javamcsv.TestHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AbstractResultSetTest {

    private ResultSet rs;
    private Object o;
    private Ref ref;
    private Blob blob;
    private Clob clob;
    private Array array;
    private NClob nclob;
    private RowId rowId;
    private SQLXML sqlxml;

    @Before
    public void setUp() throws SQLException, IOException {
        o = new Object();
        rs = new AbstractResultSet() {
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
                return new Date(1);
            }

            @Override
            public Date getDate(int columnIndex, Calendar cal) throws SQLException {
                return new Date(2);
            }

            @Override
            public Time getTime(int columnIndex) throws SQLException {
                return new Time(5);
            }

            @Override
            public Time getTime(int columnIndex, Calendar cal) throws SQLException {
                return new Time(6);
            }

            @Override
            public Timestamp getTimestamp(int columnIndex) throws SQLException {
                return new Timestamp(10);
            }

            @Override
            public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
                return new Timestamp(11);
            }

            @Override
            public InputStream getAsciiStream(int columnIndex) throws SQLException {
                return new ByteArrayInputStream(new byte[]{'7'});
            }

            @Override
            public InputStream getBinaryStream(int columnIndex) throws SQLException {
                return new ByteArrayInputStream(new byte[]{'8'});
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
            public int getHoldability() throws SQLException {
                return 0;
            }

            @Override
            public boolean isClosed() throws SQLException {
                return false;
            }

            @Override
            public InputStream getUnicodeStream(int columnIndex) {
                return new ByteArrayInputStream(new byte[]{'A'});
            }


            @Override
            public Reader getNCharacterStream(int columnIndex) throws SQLException {
                return new StringReader("baz");
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
                o = null;
            }

            @Override
            public void updateBoolean(int columnIndex, boolean b) throws SQLException {
                o = b;
            }

            @Override
            public void updateByte(int columnIndex, byte b) throws SQLException {
                o = b;
            }

            @Override
            public void updateShort(int columnIndex, short s) throws SQLException {
                o = s;
            }

            @Override
            public void updateInt(int columnIndex, int i) throws SQLException {
                o = i;
            }

            @Override
            public void updateLong(int columnIndex, long i) throws SQLException {
                o = i;
            }

            @Override
            public void updateFloat(int columnIndex, float f) throws SQLException {
                o = f;
            }

            @Override
            public void updateDouble(int columnIndex, double d) throws SQLException {
                o = d;
            }

            @Override
            public void updateBigDecimal(int columnIndex, BigDecimal d) throws SQLException {
                o = d;
            }

            @Override
            public void updateString(int columnIndex, String s) throws SQLException {
                o = s;
            }

            @Override
            public void updateBytes(int columnIndex, byte[] bs) throws SQLException {
                o = bs;
            }

            @Override
            public void updateDate(int columnIndex, Date d) throws SQLException {
                o = d;
            }

            @Override
            public void updateTime(int columnIndex, Time t) throws SQLException {
                o = t;
            }

            @Override
            public void updateTimestamp(int columnIndex, Timestamp ts) throws SQLException {
                o = ts;
            }

            @Override
            public void updateAsciiStream(int columnIndex, InputStream in, int len)
                    throws SQLException {
                o = TestHelper.boundedStream(in, len);
            }

            @Override
            public void updateAsciiStream(int columnIndex, InputStream in, long len)
                    throws SQLException {
                o = TestHelper.boundedStream(in, len);
            }

            @Override
            public void updateBinaryStream(int columnIndex, InputStream in, int len)
                    throws SQLException {
                o = TestHelper.boundedStream(in, len);
            }

            @Override
            public void updateBinaryStream(int columnIndex, InputStream in, long len)
                    throws SQLException {
                o = TestHelper.boundedStream(in, len);
            }

            @Override
            public void updateCharacterStream(int columnIndex, Reader reader, int len)
                    throws SQLException {
                o = TestHelper.boundedReader(reader, len);
            }

            @Override
            public void updateCharacterStream(int columnIndex, Reader reader, long len)
                    throws SQLException {
                o = TestHelper.boundedReader(reader, len);
            }

            @Override
            public void updateNCharacterStream(int columnIndex, Reader reader, long length)
                    throws SQLException {
                o = TestHelper.boundedReader(reader, length);
            }

            @Override
            public void updateObject(int columnIndex, Object x, int scaleOrLength)
                    throws SQLException {
                o = x;
            }

            @Override
            public void updateObject(int columnIndex, Object x) throws SQLException {
                o = x;
            }

            @Override
            public void updateRef(int columnIndex, Ref ref) throws SQLException {
                o = ref;
            }

            @Override
            public void updateBlob(int columnIndex, Blob blob) throws SQLException {
                o = blob;
            }

            @Override
            public void updateBlob(int columnIndex, InputStream inputStream, long length)
                    throws SQLException {
                try {
                    o = TestHelper.readStream(TestHelper.boundedStream(inputStream, length));
                } catch (IOException e) {
                    throw new SQLException(e);
                }
            }

            @Override
            public void updateBlob(int columnIndex, InputStream inputStream)
                    throws SQLException {
                try {
                    o = TestHelper.readStream(inputStream);
                } catch (IOException e) {
                    throw new SQLException(e);
                }
            }

            @Override
            public void updateClob(int columnIndex, Clob clob) throws SQLException {
                o = clob;
            }

            @Override
            public void updateClob(int columnIndex, Reader reader, long length)
                    throws SQLException {
                try {
                    o = TestHelper.readReader(TestHelper.boundedReader(reader, length));
                } catch (IOException e) {
                    throw new SQLException(e);
                }
            }

            @Override
            public void updateClob(int columnIndex, Reader reader)
                    throws SQLException {
                try {
                    o = TestHelper.readReader(reader);
                } catch (IOException e) {
                    throw new SQLException(e);
                }
            }

            @Override
            public void updateNClob(int columnIndex, NClob nclob) throws SQLException {
                o = nclob;
            }

            @Override
            public void updateNClob(int columnIndex, Reader reader, long length)
                    throws SQLException {
                try {
                    o = TestHelper.readReader(TestHelper.boundedReader(reader, length));
                } catch (IOException e) {
                    throw new SQLException(e);
                }
            }

            @Override
            public void updateNClob(int columnIndex, Reader reader)
                    throws SQLException {
                try {
                    o = TestHelper.readReader(reader);
                } catch (IOException e) {
                    throw new SQLException(e);
                }
            }


            @Override
            public void updateNCharacterStream(int columnIndex, Reader r) throws SQLException {
                o = r;
            }

            @Override
            public void updateAsciiStream(int columnIndex, InputStream in) throws SQLException {
                o = in;
            }

            @Override
            public void updateBinaryStream(int columnIndex, InputStream in) throws SQLException {
                o = in;
            }

            @Override
            public void updateCharacterStream(int columnIndex, Reader r) throws SQLException {
                o = r;
            }

            @Override
            public void updateArray(int columnIndex, Array arr) throws SQLException {
                o = arr;
            }

            @Override
            public void updateRowId(int columnIndex, RowId rowId) throws SQLException {
                o = rowId;
            }

            @Override
            public void updateSQLXML(int columnIndex, SQLXML sqlxml) throws SQLException {
                o = sqlxml;
            }

            @Override
            public void updateNString(int columnIndex, String s) throws SQLException {
                o = s;

            }

            @Override
            public Object getObject(int columnIndex, Map<String, Class<?>> map)
                    throws SQLException {
                return new Time(0);
            }

            @Override
            public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
                return (T) new Time(1);
            }

            @Override
            public Ref getRef(int columnIndex) throws SQLException {
                return ref;
            }

            @Override
            public Blob getBlob(int columnIndex) throws SQLException {
                return blob;
            }

            @Override
            public Clob getClob(int columnIndex) throws SQLException {
                return clob;
            }

            @Override
            public NClob getNClob(int columnIndex) throws SQLException {
                return nclob;
            }

            @Override
            public Array getArray(int columnIndex) throws SQLException {
                return array;
            }

            @Override
            public RowId getRowId(int columnIndex) throws SQLException {
                return rowId;
            }

            @Override
            public SQLXML getSQLXML(int columnIndex) throws SQLException {
                return sqlxml;
            }

            @Override
            public String getNString(int columnIndex) throws SQLException {
                return "baz";
            }

            @Override
            public URL getURL(int columnIndex) throws SQLException {
                try {
                    return new URL("file://bar");
                } catch (MalformedURLException e) {
                    throw new SQLException(e);
                }
            }
        };
        this.ref = PowerMock.createMock(Ref.class);
        this.blob = PowerMock.createMock(Blob.class);
        this.clob = PowerMock.createMock(Clob.class);
        this.nclob = PowerMock.createMock(NClob.class);
        this.array = PowerMock.createMock(Array.class);
        this.rowId = PowerMock.createMock(RowId.class);
        this.sqlxml = PowerMock.createMock(SQLXML.class);
    }

    @Test
    public void testGetObjects() throws SQLException, IOException {
        Assert.assertEquals(0, rs.getByte("foo"));
        Assert.assertEquals(1.0f, rs.getFloat("foo"), 0.01);
        Assert.assertArrayEquals(new byte[]{3}, rs.getBytes("foo"));
        Assert.assertEquals(new BigDecimal("2"), rs.getBigDecimal("foo", 2));

        Assert.assertEquals(new Date(1), rs.getDate("foo"));
        Assert.assertEquals(new Date(2), rs.getDate("foo", Calendar.getInstance()));

        Assert.assertEquals(new Time(5), rs.getTime("foo"));
        Assert.assertEquals(new Time(6), rs.getTime("foo", Calendar.getInstance()));

        Assert.assertEquals(new Timestamp(10), rs.getTimestamp("foo"));
        Assert.assertEquals(new Timestamp(11), rs.getTimestamp("foo", Calendar.getInstance()));


        Assert.assertEquals(new Time(0), rs.getObject("foo", new HashMap<String, Class<?>>()));
        Assert.assertEquals(new Time(1), rs.getObject("foo", Time.class));
        Assert.assertNull(rs.getObject("foo"));

        Assert.assertEquals(rowId, rs.getRowId("foo"));
        Assert.assertEquals(sqlxml, rs.getSQLXML("foo"));

        Assert.assertEquals(ref, rs.getRef("foo"));
        Assert.assertEquals(array, rs.getArray("foo"));
        Assert.assertEquals(new URL("file://bar"), rs.getURL("foo"));
        Assert.assertEquals("baz", rs.getNString("foo"));
    }

    @Test
    public void testGetBlobs() throws SQLException, IOException {
        Assert.assertEquals(clob, rs.getClob("foo"));
        Assert.assertEquals(nclob, rs.getNClob("foo"));
        Assert.assertEquals(blob, rs.getBlob("foo"));
    }

    @Test
    public void testGetStreams() throws SQLException, IOException {
        Assert.assertArrayEquals(new byte[]{'7'}, TestHelper.readStream(rs.getAsciiStream("foo")));
        Assert.assertArrayEquals(new byte[]{'8'}, TestHelper.readStream(rs.getBinaryStream("foo")));
        Assert.assertEquals("9", TestHelper.readReader(rs.getCharacterStream("foo")));
        Assert.assertEquals("baz", TestHelper.readReader(rs.getNCharacterStream("foo")));
        Assert.assertArrayEquals(new byte[]{'A'},
                TestHelper.readStream(rs.getUnicodeStream("foo")));
    }

    @Test
    public void testOthers() throws SQLException, IOException {
        Assert.assertEquals(1000, rs.getRow());
    }

    @Test
    public void testUpdateObjects() throws SQLException, IOException {
        rs.updateNull("foo");
        Assert.assertNull(o);
        rs.updateBoolean("foo", true);
        Assert.assertEquals(true, o);
        rs.updateByte("foo", (byte) '1');
        Assert.assertEquals((byte) '1', o);
        rs.updateShort("foo", (short) 1);
        Assert.assertEquals((short) 1, o);
        rs.updateInt("foo", 2);
        Assert.assertEquals(2, o);
        rs.updateLong("foo", 3L);
        Assert.assertEquals(3L, o);
        rs.updateFloat("foo", 4.0f);
        Assert.assertEquals(4.0f, (Float) o, 0.01f);
        rs.updateDouble("foo", 5.0);
        Assert.assertEquals(5.0, (Double) o, 0.01);
        rs.updateBigDecimal("foo", new BigDecimal("10"));
        Assert.assertEquals(new BigDecimal("10"), o);
        rs.updateString("foo", "bar");
        Assert.assertEquals("bar", o);
        rs.updateBytes("foo", new byte[]{1, 2, 3});
        Assert.assertArrayEquals(new byte[]{1, 2, 3}, (byte[]) o);

        rs.updateDate("foo", new Date(12345));
        Assert.assertEquals(new Date(12345), o);
        rs.updateTime("foo", new Time(1234));
        Assert.assertEquals(new Time(1234), o);
        rs.updateTimestamp("foo", new Timestamp(12346));
        Assert.assertEquals(new Timestamp(12346), o);

        rs.updateObject("foo", 10, 10);
        Assert.assertEquals(10, o);
        rs.updateObject("foo", 15);
        Assert.assertEquals(15, o);

        rs.updateRef("foo", ref);
        Assert.assertEquals(ref, o);
        rs.updateArray("foo", array);
        Assert.assertEquals(array, o);
        rs.updateRowId("foo", rowId);
        Assert.assertEquals(rowId, o);
        rs.updateSQLXML("foo", sqlxml);
        Assert.assertEquals(sqlxml, o);
        rs.updateNString("foo", "bar");
        Assert.assertEquals("bar", o);
    }

    @Test
    public void testUpdateBlobs() throws SQLException, IOException {
        rs.updateBlob("foo", blob);
        Assert.assertEquals(blob, o);
        rs.updateBlob("foo", new ByteArrayInputStream(new byte[]{1, 5, 7}));
        Assert.assertArrayEquals(new byte[]{1, 5, 7}, (byte[]) o);
        rs.updateBlob("foo", new ByteArrayInputStream(new byte[]{1, 5, 7}), 2L);
        Assert.assertArrayEquals(new byte[]{1, 5}, (byte[]) o);

        rs.updateClob("foo", clob);
        Assert.assertEquals(clob, o);
        rs.updateClob("foo", new StringReader("bar"));
        Assert.assertEquals("bar", o);
        rs.updateClob("foo", new StringReader("bar"), 2L);
        Assert.assertEquals("ba", o);

        rs.updateNClob("foo", nclob);
        Assert.assertEquals(nclob, o);
        rs.updateNClob("foo", new StringReader("bar"));
        Assert.assertEquals("bar", o);
        rs.updateNClob("foo", new StringReader("bar"), 1L);
        Assert.assertEquals("b", o);
    }

    @Test
    public void testUpdateStreams() throws SQLException, IOException {
        rs.updateAsciiStream("foo", new ByteArrayInputStream(new byte[]{1, 5, 7}));
        Assert.assertArrayEquals(new byte[]{1, 5, 7}, TestHelper.readStream((InputStream) o));
        rs.updateAsciiStream("foo", new ByteArrayInputStream(new byte[]{1, 5, 7}), 2);
        Assert.assertArrayEquals(new byte[]{1, 5}, TestHelper.readStream((InputStream) o));
        rs.updateAsciiStream("foo", new ByteArrayInputStream(new byte[]{1, 5, 7}), 2L);
        Assert.assertArrayEquals(new byte[]{1, 5}, TestHelper.readStream((InputStream) o));

        rs.updateBinaryStream("foo", new ByteArrayInputStream(new byte[]{1, 9, 7}));
        Assert.assertArrayEquals(new byte[]{1, 9, 7}, TestHelper.readStream((InputStream) o));
        rs.updateBinaryStream("foo", new ByteArrayInputStream(new byte[]{1, 9, 7}), 2);
        Assert.assertArrayEquals(new byte[]{1, 9}, TestHelper.readStream((InputStream) o));
        rs.updateBinaryStream("foo", new ByteArrayInputStream(new byte[]{1, 9, 7}), 2L);
        Assert.assertArrayEquals(new byte[]{1, 9}, TestHelper.readStream((InputStream) o));

        rs.updateCharacterStream("foo", new StringReader("bar"));
        Assert.assertEquals("bar", TestHelper.readReader((Reader) o));
        rs.updateCharacterStream("foo", new StringReader("bar"), 2);
        Assert.assertEquals("ba", TestHelper.readReader((Reader) o));
        rs.updateCharacterStream("foo", new StringReader("bar"), 2L);
        Assert.assertEquals("ba", TestHelper.readReader((Reader) o));

        rs.updateNCharacterStream("foo", new StringReader("bar"));
        Assert.assertEquals("bar", TestHelper.readReader((Reader) o));
        rs.updateNCharacterStream("foo", new StringReader("bar"), 1);
        Assert.assertEquals("b", TestHelper.readReader((Reader) o));
    }
}