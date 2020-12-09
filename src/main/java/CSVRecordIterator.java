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

import org.apache.commons.csv.CSVRecord;

import java.util.Iterator;

public class CSVRecordIterator implements Iterator<MetaCSVRecord> {
    private final Iterator<CSVRecord> csvIterator;
    private final CSVRecordProcessor processor;
    private boolean first;

    public CSVRecordIterator(Iterator<CSVRecord> csvIterator, CSVRecordProcessor processor) {
        this.csvIterator = csvIterator;
        this.processor = processor;
        this.first = true;
    }

    @Override
    public boolean hasNext() {
        return this.csvIterator.hasNext();
    }

    @Override
    public MetaCSVRecord next() {
        CSVRecord record = this.csvIterator.next();
        try {
            if (this.first) {
                this.first = false;
                return CSVRecordProcessor.TEXT_PROCESSOR.process(record);
            }
            return processor.process(record);
        } catch (MetaCSVReadException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove");
    }
}
