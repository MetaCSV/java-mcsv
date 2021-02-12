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

import org.apache.commons.csv.CSVParser;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public class MetaCSVReader implements Iterable<MetaCSVRecord>, Closeable {
    public static MetaCSVReader create(File csvFile)
            throws IOException, MetaCSVParseException, MetaCSVReadException, MetaCSVDataException {
        return MetaCSVReaderFactory.create(csvFile);
    }

    public static MetaCSVReader create(File csvFile, File metaCSVFile)
            throws IOException, MetaCSVParseException, MetaCSVReadException, MetaCSVDataException {
        return MetaCSVReaderFactory.create(csvFile, metaCSVFile);
    }

    public static MetaCSVReader create(File csvFile, String... metaCSVdirectives)
            throws IOException, MetaCSVParseException, MetaCSVReadException, MetaCSVDataException {
        return MetaCSVReaderFactory.create(csvFile, metaCSVdirectives);
    }

    public static MetaCSVReader create(InputStream in, String... metaCSVdirectives)
            throws IOException, MetaCSVParseException, MetaCSVDataException, MetaCSVReadException {
        return MetaCSVReaderFactory.create(in, metaCSVdirectives);
    }

    public static MetaCSVReader create(InputStream in, InputStream metaIn)
            throws IOException, MetaCSVParseException, MetaCSVReadException, MetaCSVDataException {
        return MetaCSVReaderFactory.create(in, metaIn);
    }

    public static MetaCSVReader create(InputStream in, MetaCSVData data)
            throws IOException, MetaCSVReadException {
        return MetaCSVReaderFactory.create(in, data);
    }

    private final CSVParser parser;
    private final CSVRecordProcessor processor;
    private MetaCSVMetaData metaData;

    public MetaCSVReader(CSVParser parser, CSVRecordProcessor processor,
                         MetaCSVMetaData metaData) {
        this.parser = parser;
        this.processor = processor;
        this.metaData = metaData;
    }

    @Override
    public Iterator<MetaCSVRecord> iterator() {
        return new CSVRecordIterator(this.parser.iterator(), this.processor);
    }

    public long getRow() {
        return this.parser.getRecordNumber() - 1;
    }

    public MetaCSVMetaData getMetaData() {
        return this.metaData;
    }

    @Override
    public void close() throws IOException {
        this.parser.close();
    }
}
