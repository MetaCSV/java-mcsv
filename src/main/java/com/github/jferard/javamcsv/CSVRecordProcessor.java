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

import java.util.ArrayList;
import java.util.List;

public class CSVRecordProcessor {
    private final ProcessorProvider provider;

    CSVRecordProcessor(ProcessorProvider provider) {
        this.provider = provider;
    }

    public MetaCSVRecord process(CSVRecord record) throws MetaCSVReadException {
        List<Object> values = new ArrayList<Object>(record.size());
        for (int i = 0; i < record.size(); i++) {
            FieldProcessor<?> processor = provider.getProcessor(i);
            String s = record.get(i);
            values.add(processor.toObject(s));
        }
        return new MetaCSVRecord(record, values);
    }
}
