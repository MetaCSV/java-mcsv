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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

public class MetaCSVReaderBuilder {
    private File csvFile;
    private File metaCSVFile;
    private String[] metaCSVdirectives;
    private InputStream csvIn;
    private InputStream metaIn;
    private MetaCSVData data;
    private MetaCSVParser metaParser;

    public MetaCSVReaderBuilder csvFile(File csvFile) {
        this.csvFile = csvFile;
        this.metaCSVFile = Util.withExtension(csvFile, ".mcsv");
        return this;
    }

    public MetaCSVReaderBuilder csvIn(InputStream csvIn) {
        this.csvIn = csvIn;
        return this;
    }

    public MetaCSVReaderBuilder metaCSVFile(File metaCSVFile) {
        this.metaCSVFile = metaCSVFile;
        return this;
    }

    public MetaCSVReaderBuilder metaCSVdirectives(String... metaCSVdirectives) {
        this.metaCSVdirectives = metaCSVdirectives;
        return this;
    }

    public MetaCSVReaderBuilder metaIn(InputStream metaIn) {
        this.metaIn = metaIn;
        return this;
    }

    public MetaCSVReaderBuilder metaParser(MetaCSVParser metaParser) {
        this.metaParser = metaParser;
        return this;
    }


    public MetaCSVReaderBuilder metaData(MetaCSVData data) {
        this.data = data;
        return this;
    }

    public MetaCSVReader build()
            throws IOException, MetaCSVParseException, MetaCSVDataException, MetaCSVReadException {
        MetaCSVData data = getData();
        if (this.csvIn == null) {
            this.csvIn = new FileInputStream(csvFile);
        }
        return create(this.csvIn, data);
    }

    private MetaCSVData getData() throws MetaCSVParseException, IOException, MetaCSVDataException {
        if (this.data != null) {
            return this.data;
        }
        if (this.metaParser == null) {
            if (this.metaIn == null) {
                if (this.metaCSVdirectives == null) {
                    if (this.metaCSVFile == null) {
                        throw new MetaCSVParseException("");
                    } else {
                        this.metaIn = new FileInputStream(this.metaCSVFile);
                        this.metaParser = MetaCSVParser.create(metaIn);
                    }
                } else {
                    String metaString = Util.join(metaCSVdirectives, "\r\n");
                    Reader metaReader = new StringReader("domain,key,value\r\n" + metaString);
                    this.metaParser = MetaCSVParser.create(metaReader);
                }
            } else {
                this.metaParser = MetaCSVParser.create(metaIn);
            }
        }
        return this.metaParser.parse();
    }

    public MetaCSVReader create(InputStream csvIn, MetaCSVData data)
            throws IOException, MetaCSVReadException {
        if (data.isUtf8BOM()) {
            byte[] buffer = new byte[3];
            int count = 0;
            while (count < 3) {
                count = csvIn.read(buffer, count, 3-count);
            }
            if ((buffer[0] & 0xFF) != 0xEF || (buffer[1] & 0xFF) != 0xBB ||
                    (buffer[2] & 0xFF) != 0xBF) {
                throw new MetaCSVReadException("BOM expected");
            }
        }
        InputStreamReader reader = new InputStreamReader(csvIn, data.getEncoding());
        CSVFormat format = CSVFormatHelper.getCSVFormat(data);
        CSVRecordProcessor processor = new CSVRecordProcessor(data);
        return new MetaCSVReader(format.parse(reader), processor, data.getMetaData());
    }
}
