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

import org.apache.commons.csv.CSVParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MetaCSVReader implements Iterable<MetaCSVRecord> {
    public static MetaCSVReader create(File csvFile)
            throws IOException, MetaCSVParseException {
        File metaCSVFile = Util.withExtension(csvFile, ".mcsv");
        return create(csvFile, metaCSVFile);
    }

    public static MetaCSVReader create(File csvFile, File metaCSVFile)
            throws IOException, MetaCSVParseException {
        InputStream metaIn = new FileInputStream(metaCSVFile);
        InputStream in = new FileInputStream(csvFile);
        return create(in, metaIn);
    }

    public static MetaCSVReader create(InputStream is, InputStream metaIs)
            throws IOException, MetaCSVParseException {
        MetaCSVData data = MetaCSVParser.create(metaIs).parse();
        Reader reader = new InputStreamReader(is, data.getEncoding());
        return new MetaCSVReaderFactory(data, reader).build();
    }

    private final CSVParser parser;
    private final CSVRecordProcessor processor;
    private Map<Integer, String> types;

    public MetaCSVReader(CSVParser parser, CSVRecordProcessor processor, Map<Integer, String> types) {
        this.parser = parser;
        this.processor = processor;
        this.types = types;
    }

    public Map<Integer, String> getTypes() throws IOException {
        return this.types;
    }

    @Override
    public Iterator<MetaCSVRecord> iterator() {
        return new CSVRecordIterator(this.parser.iterator(), this.processor);
    }
}
