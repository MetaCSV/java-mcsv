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

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class CSVFormatHelperTest {
    @Test
    public void testApacheCommons() throws IOException {
        CSVFormat format = CSVFormat.DEFAULT.withEscape('"');
        StringBuilder out = new StringBuilder();
        CSVPrinter printer = new CSVPrinter(out, format);
        printer.printRecord("\"foo\", \"bar\"", "baz");
        Assert.assertEquals("\"\"\"foo\"\", \"\"bar\"\"\",baz\r\n", out.toString());
    }

    @Test
    public void test() throws MetaCSVDataException {
        MetaCSVData data = new MetaCSVDataBuilder().build();
        Assert.assertEquals(CSVFormat.DEFAULT, CSVFormatHelper.getCSVFormat(data));
    }

    @Test
    public void test2() throws MetaCSVDataException {
        MetaCSVData data = new MetaCSVDataBuilder().delimiter(';').lineTerminator("\n").build();
        Assert.assertEquals(CSVFormat.DEFAULT.withDelimiter(';').withRecordSeparator('\n'),
                CSVFormatHelper.getCSVFormat(data));
    }
}