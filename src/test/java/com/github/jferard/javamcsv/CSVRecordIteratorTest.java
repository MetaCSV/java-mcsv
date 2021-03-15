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

import org.apache.commons.csv.CSVRecord;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.TimeZone;

public class CSVRecordIteratorTest {
    @Test
    public void testReadException() throws IOException, MetaCSVDataException, MetaCSVReadException {
        Iterator<CSVRecord> wrappedIterator =
                Arrays.asList(TestHelper.createRecord("foo", "bar", "baz"),
                        TestHelper.createRecord("foo value", "bar value", "baz value")).iterator();
        MetaCSVData metaData = new MetaCSVDataBuilder()
                .colType(1, IntegerFieldDescription.INSTANCE).nullValue(null).build();
        Iterator<MetaCSVRecord> it =
                new CSVRecordsIterator(wrappedIterator, new CSVRecordProcessor(metaData,
                        OnError.WRAP, TimeZone.getTimeZone("UTC")));
        Assert.assertTrue(it.hasNext());
        TestHelper.assertMetaEquals(TestHelper.createMetaRecord("foo", "bar", "baz"), it.next());
        Assert.assertTrue(it.hasNext());
        it.next();
    }

    @Test
    public void testRead() throws IOException, MetaCSVDataException, MetaCSVReadException {
        Iterator<CSVRecord> wrappedIterator =
                Arrays.asList(TestHelper.createRecord("foo", "bar", "baz"),
                        TestHelper.createRecord("foo value", "1", "baz value")).iterator();
        MetaCSVData metaData = new MetaCSVDataBuilder()
                .colType(1, IntegerFieldDescription.INSTANCE).nullValue(null).build();
        Iterator<MetaCSVRecord> it =
                new CSVRecordsIterator(wrappedIterator, new CSVRecordProcessor(metaData,
                        OnError.WRAP, TimeZone.getTimeZone("UTC")));
        Assert.assertTrue(it.hasNext());
        TestHelper.assertMetaEquals(TestHelper.createMetaRecord("foo", "bar", "baz"),
                it.next());
        Assert.assertTrue(it.hasNext());
        TestHelper.assertMetaEquals(TestHelper.createMetaRecord("foo value", 1L, "baz value"),
                it.next());
        Assert.assertFalse(it.hasNext());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRemove() throws IOException {
        Iterator<CSVRecord> wrappedIterator =
                Collections.singleton(TestHelper.createRecord("foo", "bar, baz")).iterator();
        Iterator<MetaCSVRecord> it =
                new CSVRecordsIterator(wrappedIterator, CSVRecordsIterator.HEADER_PROCESSOR);
        it.remove();
    }
}