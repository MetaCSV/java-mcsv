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

import com.github.jferard.javamcsv.processor.WriteFieldProcessor;
import com.github.jferard.javamcsv.processor.WriteProcessorProvider;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class MetaCSVWriter implements Closeable {
    public static MetaCSVWriter create(File csvFile, MetaCSVData data) throws IOException {
        return new MetaCSVWriterBuilder().csvFile(csvFile).metaData(data).build();
    }

    public static MetaCSVWriter create(File csvFile, File metaCSVFile, MetaCSVData data)
            throws IOException {
        return new MetaCSVWriterBuilder().csvFile(csvFile).metaCSVFile(metaCSVFile).metaData(data).build();
    }

    public static MetaCSVWriter create(OutputStream out, OutputStream metaOut, MetaCSVData data)
            throws IOException {
        return new MetaCSVWriterBuilder().out(out).metaOut(metaOut).metaData(data).build();
    }

    public static MetaCSVWriter create(OutputStream out, MetaCSVData data) throws IOException {
        return new MetaCSVWriterBuilder().out(out).metaData(data).build();
    }

    private final WriteProcessorProvider writeProvider;
    private final CSVPrinter printer;

    protected MetaCSVWriter(CSVPrinter printer, WriteProcessorProvider writeProvider) {
        this.printer = printer;
        this.writeProvider = writeProvider;
    }

    public void close() throws IOException {
        this.printer.close();
    }

    public void writeHeader(List<String> header) throws IOException {
        printer.printRecord(header);
    }

    public void writeRow(List<Object> values) throws IOException {
        List<String> formattedValues = new ArrayList<String>(values.size());
        for (int i = 0; i < values.size(); i++) {
            Object value = values.get(i);
            WriteFieldProcessor processor = writeProvider.getProcessor(i);
            String formattedValue = processor.toString(value);
            formattedValues.add(formattedValue);
        }
        printer.printRecord(formattedValues);
    }
}
