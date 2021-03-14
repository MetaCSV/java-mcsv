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

package com.github.jferard.javamcsv;import org.apache.commons.csv.CSVFormat;
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
        File metaCSVFile = Util.withExtension(csvFile, ".mcsv");
        return create(csvFile, metaCSVFile, data);
    }

    public static MetaCSVWriter create(File csvFile, File metaCSVFile, MetaCSVData data)
            throws IOException {
        OutputStream metaOut = new FileOutputStream(metaCSVFile);
        OutputStream out = new FileOutputStream(csvFile);
        return create(out, metaOut, data);
    }

    public static MetaCSVWriter create(OutputStream out, OutputStream metaOut, MetaCSVData data)
            throws IOException {
        try {
            MetaCSVRenderer renderer = MetaCSVRenderer.create(metaOut);
            renderer.render(data);
        } finally {
            metaOut.close();
        }
        return create(out, data);
    }

    public static MetaCSVWriter create(OutputStream out, MetaCSVData data) throws IOException {
        CSVFormat format = CSVFormatHelper.getCSVFormat(data);
        Appendable writer = new OutputStreamWriter(out, data.getEncoding());
        CSVPrinter printer = new CSVPrinter(writer, format);
        return new MetaCSVWriter(printer, data);
    }

    private final ProcessorProvider provider;
    private final CSVPrinter printer;

    private MetaCSVWriter(CSVPrinter printer, ProcessorProvider provider) {
        this.printer = printer;
        this.provider = provider;
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
            FieldProcessor<?> rawProcessor = provider.getProcessor(i, OnError.EXCEPTION);
            FieldProcessor<Object> processor = (FieldProcessor<Object>) rawProcessor;
            String formattedValue = processor.toString(value);
            formattedValues.add(formattedValue);
        }
        printer.printRecord(formattedValues);
    }
}
