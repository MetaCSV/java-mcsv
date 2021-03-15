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

package com.github.jferard.javamcsv;

import com.github.jferard.javamcsv.processor.FieldProcessor;
import com.github.jferard.javamcsv.processor.ProcessorProvider;
import com.github.jferard.javamcsv.processor.ReadFieldProcessor;
import com.github.jferard.javamcsv.processor.ReadProcessorProvider;
import org.apache.commons.csv.CSVRecord;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import static com.github.jferard.javamcsv.Util.UTC_TIME_ZONE;

public class MetaCSVRecord implements Iterable<Object> {
    private final int offset;
    private CSVRecord record;
    private ProcessorProvider provider;
    private ReadProcessorProvider readProvider;
    private HashMap<Integer, ReadFieldProcessor<?>> processorByIndex;

    public MetaCSVRecord(CSVRecord record, ProcessorProvider provider,
                         ReadProcessorProvider readProvider,
                         HashMap<Integer, ReadFieldProcessor<?>> processorByIndex,
                         TimeZone timeZone) {
        this.record = record;
        this.provider = provider;
        this.readProvider = readProvider;
        this.processorByIndex = processorByIndex;
        this.offset = UTC_TIME_ZONE.getRawOffset() - timeZone.getRawOffset();
    }

    public Boolean getBoolean(int i) throws MetaCSVCastException, MetaCSVReadException {
        Object value = getValue(i);
        if (value == null) {
            return null;
        } else if (value instanceof Boolean) {
            return (Boolean) value;
        } else {
            throw new MetaCSVCastException("Not a boolean: " + value);
        }
    }

    public Date getDate(int i) throws MetaCSVReadException {
        Object value = getValue(i);
        if (value == null) {
            return null;
        } else if (value instanceof Date) {
            Date date = (Date) value;
            Calendar cal = GregorianCalendar.getInstance(UTC_TIME_ZONE);
            cal.setTimeInMillis(date.getTime() + offset);
            cal.set(Calendar.HOUR, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            return cal.getTime();
        } else {
            throw new MetaCSVCastException("Not a date: " + value);
        }
    }

    public Date getDatetime(int i) throws MetaCSVReadException {
        Object value = getValue(i);
        if (value == null) {
            return null;
        } else if (value instanceof Date) {
            Date date = (Date) value;
            return new Date(date.getTime() + offset);
        } else {
            throw new MetaCSVCastException("Not a datetime: " + value);
        }
    }

    public BigDecimal getDecimal(int i) throws MetaCSVReadException {
        Object value = getValue(i);
        if (value == null) {
            return null;
        } else if (value instanceof Number) {
            return (BigDecimal) value;
        } else {
            throw new MetaCSVCastException("Not a number: " + value);
        }
    }

    public Double getFloat(int i) throws MetaCSVReadException {
        Object value = getValue(i);
        if (value == null) {
            return null;
        } else if (value instanceof Number) {
            return ((Number) value).doubleValue();
        } else {
            throw new MetaCSVCastException("Not a number: " + value);
        }
    }

    public Long getInteger(int i) throws MetaCSVReadException {
        Object value = getValue(i);
        if (value == null) {
            return null;
        } else if (value instanceof Number) {
            return ((Number) value).longValue();
        } else {
            throw new MetaCSVCastException("Not a number: " + value);
        }
    }

    public CharSequence getText(int i) throws MetaCSVReadException {
        Object value = getValue(i);
        if (value == null) {
            return null;
        } else if (value instanceof CharSequence) {
            return (CharSequence) value;
        } else {
            throw new MetaCSVCastException("Not a text: " + value);
        }
    }

    public Object getObject(int i) throws MetaCSVReadException {
        return getValue(i);
    }

    public int size() {
        return this.record.size();
    }

    private Object getValue(int i) throws MetaCSVReadException {
        FieldProcessor<?> processor = this.provider.getProcessor(i);
        String text = record.get(i);
        return processor.toObject(text);
    }

    public List<Object> toList() {
        int size = this.record.size();
        List<Object> ret = new ArrayList<Object>(size);
        for (int c = 0; c < size; c++) {
            String text = record.get(c);
            ReadFieldProcessor<?> processor = this.readProvider.getProcessor(c);
            ret.add(processor.toObject(text));
        }
        return ret;
    }

    @Override
    public String toString() {
        return "MetaCSVRecord{record=" + record + "}";
    }

    @Override
    public Iterator<Object> iterator() {
        return new CSVRecordIterator(this.record, this.readProvider);
    }
}
