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

import com.github.jferard.javamcsv.description.FieldDescription;
import com.github.jferard.javamcsv.description.ObjectFieldDescription;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

public class MetaCSVParserBuilder {
    public static final ObjectTypeParser DEFAULT_OBJECT_PARSER = new ObjectTypeParser() {
        @Override
        public FieldDescription<?> parse(List<String> parameters) {
            return new ObjectFieldDescription(parameters);
        }
    };

    private File metaCSVFile;
    private String[] metaCSVDirectives;
    private Iterable<? extends Iterable<String>> metaTriplets;
    private InputStream metaIn;
    private MetaCSVParser metaParser;
    private boolean header;
    private ObjectTypeParser objectParser;

    public MetaCSVParserBuilder() {
        this.objectParser = DEFAULT_OBJECT_PARSER;
    }

    public MetaCSVParserBuilder metaCSVFile(File metaCSVFile) {
        this.metaCSVFile = metaCSVFile;
        this.header = true;
        return this;
    }

    public MetaCSVParserBuilder metaCSVDirectives(String... metaCSVDirectives) {
        this.metaCSVDirectives = metaCSVDirectives;
        this.header = false;
        return this;
    }

    public MetaCSVParserBuilder metaTriplets(Iterable<? extends Iterable<String>> metaTriplets) {
        this.metaTriplets = metaTriplets;
        this.header = false;
        return this;
    }

    /**
     * Only for triplets or directives. Otherwise, the header is mandatory.
     *
     * @param header
     * @return
     */
    public MetaCSVParserBuilder header(boolean header) {
        this.header = header;
        return this;
    }

    public MetaCSVParserBuilder metaIn(InputStream metaIn) {
        this.metaIn = metaIn;
        this.header = true;
        return this;
    }

    public MetaCSVParserBuilder metaParser(MetaCSVParser metaParser) {
        this.metaParser = metaParser;
        this.header = true;
        return this;
    }

    public MetaCSVParserBuilder objectParser(ObjectTypeParser objectParser) {
        this.objectParser = objectParser;
        return this;
    }

    public MetaCSVData build() throws MetaCSVDataException, MetaCSVParseException, IOException {
        if (this.metaParser == null) {
            Iterable<? extends Iterable<String>> rows = buildRows();
            this.metaParser = new MetaCSVParser(rows, this.header, this.objectParser);
        }
        return this.metaParser.parse();
    }

    private Iterable<? extends Iterable<String>> buildRows()
            throws MetaCSVParseException, IOException {
        Iterable<? extends Iterable<String>> rows;
        if (this.metaTriplets == null) {
            if (this.metaCSVDirectives == null) {
                rows = buildRowsFromStreamOrFile();
            } else {
                rows = buildRowsFromDirectives();
            }
        } else {
            rows = this.metaTriplets;
        }
        return rows;
    }

    private Iterable<? extends Iterable<String>> buildRowsFromDirectives() throws IOException {
        Reader reader = new StringReader(Util.join(metaCSVDirectives, "\r\n"));
        return CSVFormat.RFC4180.parse(reader);
    }

    private Iterable<? extends Iterable<String>> buildRowsFromStreamOrFile()
            throws MetaCSVParseException, IOException {
        if (this.metaIn == null) {
            if (this.metaCSVFile == null) {
                throw new MetaCSVParseException("");
            } else {
                this.metaIn = new FileInputStream(this.metaCSVFile);
            }
        }
        final Reader reader = new InputStreamReader(metaIn);
        return new CSVParser(reader, CSVFormat.DEFAULT);
    }

}
