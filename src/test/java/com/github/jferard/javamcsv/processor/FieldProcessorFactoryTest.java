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

package com.github.jferard.javamcsv.processor;

import com.github.jferard.javamcsv.OnError;
import com.github.jferard.javamcsv.ReadError;
import com.github.jferard.javamcsv.description.IntegerFieldDescription;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;

public class FieldProcessorFactoryTest {
    private FieldProcessorFactory factory;

    @Before
    public void setUp() {
        this.factory = new FieldProcessorFactory();
    }

    @Test
    public void testReadWrap() {
        ReadFieldProcessor<Long> processor =
                this.factory.toReadFieldProcessor(IntegerFieldDescription.INSTANCE, "<NULL>",
                        OnError.WRAP);
        Assert.assertEquals(10L, processor.toObject("10"));
        Assert.assertEquals(new ReadError("foo", "integer"), processor.toObject("foo"));
    }

    @Test
    public void testReadNull() {
        ReadFieldProcessor<Long> processor =
                this.factory.toReadFieldProcessor(IntegerFieldDescription.INSTANCE, "<NULL>",
                        OnError.NULL);
        Assert.assertEquals(10L, processor.toObject("10"));
        Assert.assertNull(processor.toObject("foo"));
    }

    @Test
    public void testReadText() {
        ReadFieldProcessor<Long> processor =
                this.factory.toReadFieldProcessor(IntegerFieldDescription.INSTANCE, "<NULL>",
                        OnError.TEXT);
        Assert.assertEquals(10L, processor.toObject("10"));
        Assert.assertEquals("foo", processor.toObject("foo"));
    }

    @Test
    public void testReadException() {
        final ReadFieldProcessor<Long> processor =
                this.factory.toReadFieldProcessor(IntegerFieldDescription.INSTANCE, "<NULL>",
                        OnError.EXCEPTION);
        Assert.assertEquals(10L, processor.toObject("10"));
        Assert.assertThrows(RuntimeException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                processor.toObject("foo");
            }
        });
    }

    @Test
    public void testWriteWrap() {
        Assert.assertThrows(RuntimeException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                FieldProcessorFactoryTest.this.factory
                        .toWriteFieldProcessor(IntegerFieldDescription.INSTANCE, "<NULL>",
                        OnError.WRAP, true);
            }
        });
    }

    @Test
    public void testWriteNull() {
        WriteFieldProcessor processor =
                this.factory.toWriteFieldProcessor(IntegerFieldDescription.INSTANCE, "<NULL>",
                        OnError.NULL, true);
        Assert.assertEquals("10", processor.toString(10L));
        Assert.assertEquals("<NULL>", processor.toString(null));
    }

    @Test
    public void testWriteText() {
        WriteFieldProcessor processor =
                this.factory.toWriteFieldProcessor(IntegerFieldDescription.INSTANCE, "<NULL>",
                        OnError.TEXT, true);
        Assert.assertEquals("10", processor.toString(10L));
        Assert.assertEquals("foo", processor.toString("foo"));
    }

    @Test
    public void testWriteException() {
        final WriteFieldProcessor processor =
                this.factory.toWriteFieldProcessor(IntegerFieldDescription.INSTANCE, "<NULL>",
                        OnError.EXCEPTION, true);
        Assert.assertEquals("10", processor.toString(10L));
        Assert.assertThrows(RuntimeException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                processor.toString("foo");
            }
        });
    }}