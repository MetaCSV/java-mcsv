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

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;
import java.util.List;

public class MetaCSVParser implements Closeable {
    public static MetaCSVParser create(InputStream metaIn) throws IOException {
        return MetaCSVParser.create(metaIn, true);
    }

    public static MetaCSVParser create(InputStream metaIn, boolean header) throws IOException {
        Reader reader = new InputStreamReader(metaIn);
        return MetaCSVParser.create(reader, header);
    }

    public static MetaCSVParser create(Reader reader) throws IOException {
        return MetaCSVParser.create(reader, true);
    }

    public static MetaCSVParser create(Reader reader, boolean header) throws IOException {
        CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT);
        return new MetaCSVParser(parser, header);
    }

    public static MetaCSVParser create(Iterable<Iterable<String>> metaTriplets, boolean header) {
        return new MetaCSVParser(metaTriplets, header);
    }

    private final MetaCSVDataBuilder metaCSVDataBuilder;
    private final Iterable<? extends Iterable<String>> rows;
    private boolean header;
    private ColTypeParser colTypeParser;

    public MetaCSVParser(Iterable<? extends Iterable<String>> rows, boolean header) {
        this.rows = rows;
        this.header = header;
        this.metaCSVDataBuilder = new MetaCSVDataBuilder();
        this.colTypeParser = new ColTypeParser();
    }

    public MetaCSVData parse() throws MetaCSVParseException, MetaCSVDataException {
        Iterator<? extends Iterable<String>> it = rows.iterator();
        if (this.header) {
            this.checkHeader(it);
        }
        while (it.hasNext()) {
            MetaCSVRow row = MetaCSVRow.fromIterable(it.next());
            this.parseRow(row.getDomain(), row.getKey(), row.getValue());
        }
        return this.metaCSVDataBuilder.build();
    }

    private void checkHeader(Iterator<? extends Iterable<String>> it) throws MetaCSVParseException {
        if (!it.hasNext()) {
            throw new MetaCSVParseException("Empty file");
        }
        MetaCSVRow header = MetaCSVRow.fromIterable(it.next());
        if (!(header.getDomain().equals("domain") && header.getKey().equals("key") &&
                header.getValue().equals("value"))) {
            throw new MetaCSVParseException("Bad header: " + header.toString());
        }
    }

    private void parseRow(String domain, String key, String value) throws MetaCSVParseException {
        if (domain.equals("meta")) {
            this.parseMetaRow(key, value);
        } else if (domain.equals("file")) {
            this.parseFileRow(key, value);
        } else if (domain.equals("csv")) {
            this.parseCSVRow(key, value);
        } else if (domain.equals("data")) {
            this.parseDataRow(key, value);
        } else {
            throw new MetaCSVParseException("Unknown domain: " + domain);
        }
    }

    private void parseMetaRow(String key, String value) {
        if (key.equals("version")) {
            this.metaCSVDataBuilder.metaVersion(value);
        } else {
            this.metaCSVDataBuilder.meta(key, value);
        }
    }

    private void parseFileRow(String key, String value) throws MetaCSVParseException {
        if (key.equals("encoding")) {
            this.metaCSVDataBuilder.encoding(value);
        } else if (key.equals("bom")) {
            this.metaCSVDataBuilder.bom(this.parseBoolean(value));
        } else if (key.equals("line_terminator")) {
            this.metaCSVDataBuilder.lineTerminator(value);
        } else {
            throw new MetaCSVParseException("Unknown key for domain `file`: " + key);
        }
    }

    private void parseCSVRow(String key, String value) throws MetaCSVParseException {
        if (key.equals("delimiter")) {
            this.metaCSVDataBuilder.delimiter(this.parseOneChar(value));
        } else if (key.equals("double_quote")) {
            this.metaCSVDataBuilder.doubleQuote(this.parseBoolean(value));
        } else if (key.equals("escape_char")) {
            this.metaCSVDataBuilder.escapeChar(this.parseOneChar(value));
        } else if (key.equals("quote_char")) {
            this.metaCSVDataBuilder.quoteChar(this.parseOneChar(value));
        } else if (key.equals("skip_initial_space")) {
            this.metaCSVDataBuilder.skipInitialSpace(this.parseBoolean(value));
        } else {
            throw new MetaCSVParseException("Expected csv key, got: " + value);
        }
    }

    private boolean parseBoolean(String value) throws MetaCSVParseException {
        if (value.equals("true")) {
            return true;
        } else if (value.equals("false")) {
            return false;
        } else {
            throw new MetaCSVParseException("Expected boolean, got: " + value);
        }
    }

    private char parseOneChar(String value) throws MetaCSVParseException {
        if (value.length() != 1) {
            throw new MetaCSVParseException("Expected one char, got: " + value);
        }
        return value.charAt(0);
    }

    private void parseDataRow(String key, String value) throws MetaCSVParseException {
        List<String> keys = Util.parse(key);
        if (keys.get(0).equals("col")) {
            if (keys.size() != 3) {
                throw new MetaCSVParseException("Unknown data key: " + keys);
            }
            this.parseDataColRow(keys.get(1), keys.get(2), value);
        } else if (keys.get(0).equals("null_value")) {
            if (keys.size() != 1) {
                throw new MetaCSVParseException("Unknown data key: " + keys);
            }
            this.metaCSVDataBuilder.nullValue(value);
        } else {
            throw new MetaCSVParseException("Unknown data key: " + key);
        }
    }

    private void parseDataColRow(String colNum, String colKey, String value)
            throws MetaCSVParseException {
        int c;
        try {
            c = Integer.parseInt(colNum);
        } catch (NumberFormatException e) {
            throw new MetaCSVParseException("Unknown col num: " + colNum);
        }
        this.parseDataColRow(c, colKey, value);
    }

    private void parseDataColRow(int c, String colKey, String value) throws MetaCSVParseException {
        if (colKey.equals("type")) {
            this.parseColType(c, value);
        } else {
            throw new MetaCSVParseException("Unknown col n key: " + colKey);
        }
    }

    private void parseColType(int c, String value) throws MetaCSVParseException {
        FieldDescription<?> fieldDescription = this.colTypeParser.parseColType(value);
        this.metaCSVDataBuilder.colType(c, fieldDescription);
    }

    @Override
    public void close() throws IOException {
        if (this.rows instanceof Closeable) {
            ((Closeable) this.rows).close();
        }
    }
}

