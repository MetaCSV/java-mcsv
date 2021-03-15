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

import java.util.Iterator;
import java.util.Map;

public class CSVRecordIterator implements Iterator<Object> {
    private int c;
    private CSVRecord record;
    private Map<Integer, FieldProcessor<?>> processorByIndex;

    public CSVRecordIterator(CSVRecord record, Map<Integer, FieldProcessor<?>> processorByIndex) {
        this.record = record;
        this.processorByIndex = processorByIndex;
        this.c = 0;
    }

    @Override
    public boolean hasNext() {
        return this.c < record.size();
    }

    @Override
    public Object next() {
        String text = record.get(c);
        FieldProcessor<?> processor = this.processorByIndex.get(c);
        this.c++;
        try {
            return processor.toObject(text);
        } catch (MetaCSVReadException e) {
            // Should not happen.
            throw new RuntimeException(e);
        }
    }
}
