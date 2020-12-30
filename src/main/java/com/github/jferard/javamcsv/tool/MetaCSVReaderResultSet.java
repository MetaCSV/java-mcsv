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

import com.github.jferard.javamcsv.MetaCSVReader;
import com.github.jferard.javamcsv.MetaCSVRecord;
import com.github.jferard.javamcsv.Util;

import java.io.ByteArrayInputStream;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class MetaCSVReaderResultSet extends AbstractResultSet {
    private final Map<String, Integer> indexByLabel;
    private Iterator<MetaCSVRecord> iterator;
    private MetaCSVReader reader;
    private MetaCSVRecord cur;
    private boolean wasNull;
    private final List<String> header;

    public MetaCSVReaderResultSet(MetaCSVReader reader) {
        this.reader = reader;
        this.iterator = reader.iterator();
        MetaCSVRecord headerRecord = this.iterator.next();
        this.header = Util.header(headerRecord);
        this.indexByLabel = new HashMap<String, Integer>();
        for (int i = 0; i < header.size(); i++) {
            this.indexByLabel.put(header.get(i), i+1);
        }
        this.cur = null;
        this.wasNull = false;
    }

    @Override
    public boolean next() throws SQLException {
        boolean ret = this.iterator.hasNext();
        if (ret) {
            this.cur = this.iterator.next();
        }
        return ret;
    }

    @Override
    public void close() throws SQLException {
        this.iterator = null;
    }

    @Override
    public boolean wasNull() throws SQLException {
        return this.wasNull;
    }

    @Override
    public String getString(int columnIndex) throws SQLException {
        Object o = this.cur.getAny(columnIndex-1);
        if (o == null) {
            this.wasNull = true;
            return null;
        } else {
            this.wasNull = false;
            return o.toString();
        }
    }

    @Override
    public boolean getBoolean(int columnIndex) throws SQLException {
        Object o = this.cur.getAny(columnIndex-1);
        if (o == null) {
            this.wasNull = true;
            return false;
        }
        this.wasNull = false;
        if (o instanceof Boolean) {
            return (Boolean) o;
        } else if (o instanceof CharSequence) {
            if (o.equals("1")) {
                return true;
            } else if (o.equals("0")) {
                return false;
            }
        } else if (o instanceof Integer) {
            int i = (Integer) o;
            if (i == 1) {
                return true;
            } else if (i == 0) {
                return false;
            } else {
                throw new SQLException("Bad boolean: " + o);
            }
        } else if (o instanceof Double) {
            double d = (Double) o;
            if (d == 1.0) {
                return true;
            } else if (d == 0.0) {
                return false;
            }
        } else if (o instanceof BigDecimal) {
            BigDecimal bd = (BigDecimal) o;
            if (bd.compareTo(BigDecimal.ONE) == 0) {
                return true;
            } else if (bd.compareTo(BigDecimal.ZERO) == 0) {
                return false;
            }
        }
        throw new SQLException("Bad boolean: " + o);
    }

    @Override
    public byte getByte(int columnIndex) throws SQLException {
        return (byte) getLong("Byte", columnIndex, Byte.MIN_VALUE, Byte.MAX_VALUE);
    }

    @Override
    public short getShort(int columnIndex) throws SQLException {
        return (short) getLong("Short", columnIndex, Short.MIN_VALUE, Short.MAX_VALUE);
    }

    @Override
    public int getInt(int columnIndex) throws SQLException {
        return (int) getLong("Int", columnIndex, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    @Override
    public long getLong(int columnIndex) throws SQLException {
        return getLong("Long", columnIndex);
    }

    private long getLong(String typeName, int columnIndex, long minValue, long maxValue)
            throws SQLException {
        long l = this.getLong(typeName, columnIndex);
        if (minValue <= l && l <= maxValue) {
            return l;
        }
        throw new SQLException("Bad " + typeName + ": " + l);
    }

    private long getLong(String typeName, int columnIndex) throws SQLException {
        Object o = this.cur.getAny(columnIndex-1);
        if (o == null) {
            this.wasNull = true;
            return 0L;
        }
        this.wasNull = false;
        if (o instanceof Long) {
            return (Long) o;
        }
        throw new SQLException("Bad " + typeName + ": " + o);
    }

    @Override
    public float getFloat(int columnIndex) throws SQLException {
        Number n = getNumber("float", columnIndex);
        return n.floatValue();
    }

    private Number getNumber(String typeName, int columnIndex) throws SQLException {
        Object o = this.cur.getAny(columnIndex-1);
        if (o == null) {
            this.wasNull = true;
            return 0;
        }
        this.wasNull = false;
        if (o instanceof Integer) {
            return (Integer) o;
        } else if (o instanceof Double) {
            return (Double) o;
        } else if (o instanceof BigDecimal) {
            return (BigDecimal) o;
        }
        throw new SQLException("Bad " + typeName + ": " + o);
    }


    @Override
    public double getDouble(int columnIndex) throws SQLException {
        Number n = getNumber("double", columnIndex);
        return n.doubleValue();
    }

    @Override
    public byte[] getBytes(int columnIndex) throws SQLException {
        return this.cur.getAny(columnIndex-1).toString().getBytes(Util.UTF_8_CHARSET);
    }

    @Override
    public Date getDate(int columnIndex) throws SQLException {
        java.util.Date date = getDate("Date", columnIndex);
        if (date == null) {
            return null;
        }
        return new Date(date.getTime());
    }

    private java.util.Date getDate(String typeName, int columnIndex) throws SQLException {
        Object o = this.cur.getAny(columnIndex-1);
        if (o == null) {
            this.wasNull = true;
            return null;
        } else {
            this.wasNull = false;
            if (o instanceof java.util.Date) {
                return (java.util.Date) o;
            }
        }
        throw new SQLException("Bad " + typeName + ": " + o);
    }

    @Override
    public Time getTime(int columnIndex) throws SQLException {
        return new Time(getDate("Date", columnIndex).getTime());
    }

    @Override
    public Timestamp getTimestamp(int columnIndex) throws SQLException {
        return new Timestamp(getDate("Date", columnIndex).getTime());
    }

    @Override
    public InputStream getAsciiStream(int columnIndex) throws SQLException {
        try {
            return new ByteArrayInputStream(
                    this.getString(columnIndex).getBytes(Util.ASCII_CHARSET));
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public InputStream getBinaryStream(int columnIndex) throws SQLException {
        try {
            return new ByteArrayInputStream(
                    this.getString(columnIndex).getBytes(Util.UTF_8_CHARSET));
        } catch (Exception e) {
            throw new SQLException(e);
        }
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
        return new MetaCSVResultSetMetaData(this.reader.getMetaData(), this.header, this.indexByLabel);
    }

    @Override
    public Object getObject(int columnIndex) throws SQLException {
        return this.cur.getAny(columnIndex-1);
    }

    @Override
    public int findColumn(String columnLabel) throws SQLException {
        Integer columnIndex = this.indexByLabel.get(columnLabel);
        if (columnIndex == null) {
            throw new SQLException("Unknown label: " + columnLabel);
        }
        return columnIndex;
    }

    @Override
    public Reader getCharacterStream(int columnIndex) throws SQLException {
        return new StringReader(this.getString(columnIndex));
    }

    @Override
    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        Object o = this.cur.getAny(columnIndex-1);
        if (o == null) {
            this.wasNull = true;
            return BigDecimal.ZERO;
        } else {
            this.wasNull = false;
            if (o instanceof Integer || o instanceof Double) {
                new BigDecimal(o.toString());
            } else if (o instanceof BigDecimal) {
                return (BigDecimal) o;
            }
        }
        throw new SQLException("Bad BigDecimal: " + o);
    }

    @Override
    public int getFetchDirection() throws SQLException {
        return ResultSet.FETCH_FORWARD;
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        if (direction != ResultSet.FETCH_FORWARD) {
            throw new SQLException();
        }
    }

    @Override
    public int getFetchSize() throws SQLException {
        return 0;
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        // ignore
    }

    @Override
    public int getType() throws SQLException {
        return ResultSet.TYPE_FORWARD_ONLY;
    }

    @Override
    public int getConcurrency() throws SQLException {
        return ResultSet.CONCUR_READ_ONLY;
    }

    @Override
    public Statement getStatement() throws SQLException {
        return null; // If the result set was generated some other way [...], this method may return null.
    }

    @Override
    public Date getDate(int columnIndex, Calendar cal) throws SQLException {
        Date d = this.getDate(columnIndex);
        long millis = getMillis(d, cal);
        return new Date(millis);
    }

    @Override
    public Time getTime(int columnIndex, Calendar cal) throws SQLException {
        Time t = this.getTime(columnIndex);
        long millis = getMillis(t, cal);
        return new Time(millis);
    }

    @Override
    public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        Timestamp t = this.getTimestamp(columnIndex);
        long millis = getMillis(t, cal);
        return new Timestamp(millis);
    }

    private long getMillis(java.util.Date date, Calendar cal) {
        TimeZone tz = cal.getTimeZone();
        long time = date.getTime();
        return time + tz.getOffset(time);
    }

    @Override
    public int getHoldability() throws SQLException {
        return ResultSet.HOLD_CURSORS_OVER_COMMIT;
    }

    @Override
    public boolean isClosed() throws SQLException {
        return !iterator.hasNext();
    }

    @Override
    public int getRow() throws SQLException {
        return (int) this.reader.getRow();
    }

    @Override
    public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        return null;
    }
}