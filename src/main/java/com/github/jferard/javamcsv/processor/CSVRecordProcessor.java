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

package com.github.jferard.javamcsv.processor;

import com.github.jferard.javamcsv.MetaCSVReadException;
import com.github.jferard.javamcsv.MetaCSVRecord;
import com.github.jferard.javamcsv.OnError;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.util.HashMap;
import java.util.TimeZone;

public class CSVRecordProcessor {
    private final ProcessorProvider provider;
    private final TimeZone timeZone;
    private int maxSize;
    private ReadProcessorProvider readProcessorProvider;
    private OnError onError;
    private final HashMap<Integer, ReadFieldProcessor<?>> processorByIndex;

    public CSVRecordProcessor(ProcessorProvider provider,
                              ReadProcessorProvider readProcessorProvider, OnError onError,
                              TimeZone timeZone) {
        this.provider = provider;
        this.readProcessorProvider = readProcessorProvider;
        this.onError = onError;
        this.maxSize = 0;
        processorByIndex = new HashMap<Integer, ReadFieldProcessor<?>>();
        this.timeZone = timeZone;
    }

    public MetaCSVRecord createRecord(CSVRecord record) {
        if (this.maxSize < record.size()) {
            updateProcessorByIndex(record);
            this.maxSize = record.size();
        }
        return new MetaCSVRecord(record, this.provider, this.readProcessorProvider, processorByIndex,
                timeZone);
    }

    private void updateProcessorByIndex(CSVRecord record) {
        for (int i = this.maxSize; i < record.size(); i++) {
            ReadFieldProcessor<?> processor = processorByIndex.get(i);
            if (processor == null) {
                processor = this.readProcessorProvider.getProcessor(i);
                processorByIndex.put(i, processor);
            }
        }
    }

}
