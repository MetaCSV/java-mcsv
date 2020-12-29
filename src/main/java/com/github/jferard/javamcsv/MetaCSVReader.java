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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;

public class MetaCSVReader implements Iterable<MetaCSVRecord> {
    public static MetaCSVReader create(File csvFile)
            throws IOException, MetaCSVParseException, MetaCSVReadException, MetaCSVDataException {
        File metaCSVFile = Util.withExtension(csvFile, ".mcsv");
        return create(csvFile, metaCSVFile);
    }

    public static MetaCSVReader create(File csvFile, File metaCSVFile)
            throws IOException, MetaCSVParseException, MetaCSVReadException, MetaCSVDataException {
        InputStream metaIn = new FileInputStream(metaCSVFile);
        InputStream in = new FileInputStream(csvFile);
        return create(in, metaIn);
    }

    public static MetaCSVReader create(File csvFile, String... metaCSVdirectives)
            throws IOException, MetaCSVParseException, MetaCSVReadException, MetaCSVDataException {
        InputStream in = new FileInputStream(csvFile);
        return create(in, metaCSVdirectives);
    }

    public static MetaCSVReader create(InputStream in, String... metaCSVdirectives)
            throws IOException, MetaCSVParseException, MetaCSVDataException, MetaCSVReadException {
        String metaString = Util.join(metaCSVdirectives, "\r\n");
        Reader metaReader = new StringReader("domain,key,value\r\n"+metaString);
        MetaCSVParser parser = MetaCSVParser.create(metaReader);
        return create(in, parser);
    }

    public static MetaCSVReader create(InputStream in, InputStream metaIn)
            throws IOException, MetaCSVParseException, MetaCSVReadException, MetaCSVDataException {
        MetaCSVParser parser = MetaCSVParser.create(metaIn);
        return create(in, parser);
    }

    private static MetaCSVReader create(InputStream in, MetaCSVParser parser)
            throws MetaCSVParseException, MetaCSVDataException, IOException, MetaCSVReadException {
        MetaCSVData data = parser.parse();
        if (data.isUtf8BOM()) {
            byte[] buffer = new byte[3];
            int count = 0;
            while (count < 3) {
                count = in.read(buffer, count, 3-count);
            }
            if ((buffer[0] & 0xFF) != 0xEF || (buffer[1] & 0xFF) != 0xBB ||
                    (buffer[2] & 0xFF) != 0xBF) {
                throw new MetaCSVReadException("BOM expected");
            }
        }
        Reader reader = new InputStreamReader(in, data.getEncoding());
        return new MetaCSVReaderFactory(data, reader).build();
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

    public MetaCSVMetaData getMetaData() {
        return this.metaData;
    }
}
