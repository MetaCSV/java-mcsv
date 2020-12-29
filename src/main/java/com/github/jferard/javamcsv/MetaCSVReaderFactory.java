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

package com.github.jferard.javamcsv;import org.apache.commons.csv.CSVFormat;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;

public class MetaCSVReaderFactory {

    private final MetaCSVData data;

    private final Reader reader;

    public MetaCSVReaderFactory(MetaCSVData data, Reader reader) {
        this.data = data;
        this.reader = reader;
    }

    public MetaCSVReader build() throws IOException {
        CSVFormat format = CSVFormatHelper.getCSVFormat(data);
        CSVRecordProcessor processor = getProcessor();
        return new MetaCSVReader(format.parse(reader), processor, this.data.getMetaData());
    }

    private CSVRecordProcessor getProcessor() {
        Map<Integer, FieldProcessor<?>> processorByIndex = this.data.getProcessorByIndex();
        return new CSVRecordProcessor(processorByIndex);
    }
}
