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

import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class CSVRecordProcessor {
    private final ProcessorProvider provider;
    private final TimeZone timeZone;
    private int maxSize;
    private OnError onError;
    private final Map<Integer, FieldProcessor<?>> processorByIndex;

    CSVRecordProcessor(ProcessorProvider provider, OnError onError, TimeZone timeZone) {
        this.provider = provider;
        this.onError = onError;
        this.maxSize = 0;
        processorByIndex = new HashMap<Integer, FieldProcessor<?>>();
        this.timeZone = timeZone;
    }

    public MetaCSVRecord process(CSVRecord record) throws MetaCSVReadException, IOException {
        if (this.maxSize < record.size()) {
            updateProcessorByIndex(record);
            this.maxSize = record.size();
        }
        return new MetaCSVRecord(record, this.provider, processorByIndex,
                timeZone);
    }

    private void updateProcessorByIndex(CSVRecord record) {
        for (int i = this.maxSize; i < record.size(); i++) {
            FieldProcessor<?> processor = processorByIndex.get(i);
            if (processor == null) {
                processor = this.provider.getProcessor(i, this.onError);
                processorByIndex.put(i, processor);
            }
        }
    }

}
