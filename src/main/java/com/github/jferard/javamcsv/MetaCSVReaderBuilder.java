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

import com.github.jferard.javamcsv.processor.CSVRecordProcessor;
import com.github.jferard.javamcsv.processor.ProcessorProvider;
import com.github.jferard.javamcsv.processor.ReadProcessorProvider;
import org.apache.commons.csv.CSVFormat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.TimeZone;

public class MetaCSVReaderBuilder {
    private final MetaCSVParserBuilder parserBuilder;
    private TimeZone timeZone;
    private File csvFile;
    private InputStream csvIn;
    private MetaCSVData data;
    private OnError onError;

    public MetaCSVReaderBuilder() {
        this.parserBuilder = new MetaCSVParserBuilder();
        this.onError = OnError.WRAP;
        timeZone = Util.UTC_TIME_ZONE;
    }

    public MetaCSVReaderBuilder csvFile(File csvFile) {
        this.csvFile = csvFile;
        File metaCSVFile = Util.withExtension(csvFile, ".mcsv");
        this.parserBuilder.metaCSVFile(metaCSVFile);
        return this;
    }

    public MetaCSVReaderBuilder csvIn(InputStream csvIn) {
        this.csvIn = csvIn;
        return this;
    }

    public MetaCSVReaderBuilder metaCSVFile(File metaCSVFile) {
        this.parserBuilder.metaCSVFile(metaCSVFile);
        return this;
    }

    public MetaCSVReaderBuilder metaCSVDirectives(String... metaCSVDirectives) {
        this.parserBuilder.metaCSVDirectives(metaCSVDirectives);
        return this;
    }

    public MetaCSVReaderBuilder metaCSVTriplets(Iterable<? extends Iterable<String>> metaTriplets) {
        this.parserBuilder.metaTriplets(metaTriplets);
        return this;
    }

    public MetaCSVReaderBuilder metaIn(InputStream metaIn) {
        this.parserBuilder.metaIn(metaIn);
        return this;
    }

    public MetaCSVReaderBuilder metaParser(MetaCSVParser metaParser) {
        this.parserBuilder.metaParser(metaParser);
        return this;
    }

    public MetaCSVReaderBuilder timeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
        return this;
    }


    public MetaCSVReaderBuilder metaData(MetaCSVData data) {
        this.data = data;
        return this;
    }

    public MetaCSVReaderBuilder onError(OnError onError) {
        this.onError = onError;
        return this;
    }

    public MetaCSVReader build()
            throws IOException, MetaCSVParseException, MetaCSVDataException, MetaCSVReadException {
        MetaCSVData data = getData();
        if (this.csvIn == null) {
            this.csvIn = new FileInputStream(csvFile);
        }
        return this.create(this.csvIn, data);
    }

    private MetaCSVData getData() throws MetaCSVParseException, IOException, MetaCSVDataException {
        if (this.data == null) {
            return this.parserBuilder.buildData();
        } else {
            return this.data;
        }
    }

    private MetaCSVReader create(InputStream csvIn, MetaCSVData data)
            throws IOException, MetaCSVReadException {
        if (data.isUtf8BOM()) {
            gobbleBOM(csvIn);
        }
        InputStreamReader reader = new InputStreamReader(csvIn, data.getEncoding());
        CSVFormat format = CSVFormatHelper.getCSVFormat(data);
        ProcessorProvider processorProvider = data.toProcessorProvider(data.getNullValue());
        ReadProcessorProvider readProcessorProvider =
                data.toReadProcessorProvider(this.onError);
        CSVRecordProcessor processor = new CSVRecordProcessor(
                processorProvider, readProcessorProvider, this.onError, timeZone);
        return new MetaCSVReader(format.parse(reader), processor, data.getMetaData());
    }

    private void gobbleBOM(InputStream csvIn) throws IOException, MetaCSVReadException {
        byte[] buffer = new byte[3];
        int count = 0;
        while (count < 3) {
            count = csvIn.read(buffer, count, 3 - count);
        }
        if ((buffer[0] & 0xFF) != 0xEF || (buffer[1] & 0xFF) != 0xBB ||
                (buffer[2] & 0xFF) != 0xBF) {
            throw new MetaCSVReadException("BOM expected");
        }
    }

    public MetaCSVReaderBuilder objectParser(ObjectTypeParser objectParser) {
        this.parserBuilder.objectParser(objectParser);
        return this;
    }
}
