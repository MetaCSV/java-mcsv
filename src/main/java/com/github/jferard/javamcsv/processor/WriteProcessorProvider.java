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

import com.github.jferard.javamcsv.OnError;
import com.github.jferard.javamcsv.description.TextFieldDescription;
import com.github.jferard.javamcsv.description.FieldDescription;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WriteProcessorProvider {
    private final Map<Integer, FieldDescription<?>> descriptionByColIndex;
    private final String nullValue;
    private final OnError onError;
    private final FieldProcessorFactory fieldProcessorFactory;
    private final List<WriteFieldProcessor> processors;

    public WriteProcessorProvider(Map<Integer, FieldDescription<?>> descriptionByColIndex,
                                  String nullValue, OnError onError) {
        this.descriptionByColIndex = descriptionByColIndex;
        this.nullValue = nullValue;
        this.onError = onError;
        this.fieldProcessorFactory = new FieldProcessorFactory();
        this.processors = new ArrayList<WriteFieldProcessor>();
    }

    public WriteFieldProcessor getProcessor(int c) {
        while (c >= this.processors.size()) {
            this.processors.add(null);
        }
        WriteFieldProcessor processor = this.processors.get(c);
        if (processor == null) {
            processor = createProcessor(c);
            this.processors.set(c, processor);
        }
        return processor;
    }

    private WriteFieldProcessor createProcessor(int c) {
        FieldDescription<?> fieldDescription = this.descriptionByColIndex.get(c);
        if (fieldDescription == null) {
            fieldDescription = TextFieldDescription.INSTANCE;
        }
        return this.fieldProcessorFactory
                .toWriteFieldProcessor(fieldDescription, nullValue, onError);
    }
}
