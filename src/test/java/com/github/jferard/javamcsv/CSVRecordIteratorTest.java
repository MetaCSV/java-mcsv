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

package com.github.jferard.javamcsv;

import org.apache.commons.csv.CSVRecord;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CSVRecordIteratorTest {
    @Test(expected = RuntimeException.class)
    public void testReadException() throws IOException {
        Iterator<CSVRecord> wrappedIterator =
                Arrays.asList(TestHelper.createRecord("foo", "bar", "baz"),
                        TestHelper.createRecord("foo value", "bar value", "baz value")).iterator();
        Map<Integer, FieldProcessor<?>> processorByIndex =
                new HashMap<Integer, FieldProcessor<?>>();
        processorByIndex.put(1, IntegerFieldDescription.INSTANCE.toFieldProcessor(null));
        Iterator<MetaCSVRecord> it =
                new CSVRecordIterator(wrappedIterator, new CSVRecordProcessor(processorByIndex));
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(TestHelper.toList(TestHelper.createMetaRecord("foo", "bar", "baz")),
                TestHelper.toList(it.next()));
        Assert.assertTrue(it.hasNext());
        it.next();
    }

    @Test
    public void testRead() throws IOException {
        Iterator<CSVRecord> wrappedIterator =
                Arrays.asList(TestHelper.createRecord("foo", "bar", "baz"),
                        TestHelper.createRecord("foo value", "1", "baz value")).iterator();
        Map<Integer, FieldProcessor<?>> processorByIndex =
                new HashMap<Integer, FieldProcessor<?>>();
        processorByIndex.put(1, IntegerFieldDescription.INSTANCE.toFieldProcessor(null));
        Iterator<MetaCSVRecord> it =
                new CSVRecordIterator(wrappedIterator, new CSVRecordProcessor(processorByIndex));
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(TestHelper.toList(TestHelper.createMetaRecord("foo", "bar", "baz")),
                TestHelper.toList(it.next()));
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals(
                TestHelper.toList(TestHelper.createMetaRecord("foo value", 1, "baz value")),
                TestHelper.toList(it.next()));
        Assert.assertFalse(it.hasNext());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRemove() throws IOException {
        Iterator<CSVRecord> wrappedIterator =
                Collections.singleton(TestHelper.createRecord("foo", "bar, baz")).iterator();
        Iterator<MetaCSVRecord> it =
                new CSVRecordIterator(wrappedIterator, CSVRecordProcessor.TEXT_PROCESSOR);
        it.remove();
    }
}