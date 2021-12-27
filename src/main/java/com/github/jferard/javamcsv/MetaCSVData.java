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

import com.github.jferard.javamcsv.processor.ProcessorProvider;
import com.github.jferard.javamcsv.processor.ReadProcessorProvider;
import com.github.jferard.javamcsv.processor.TextFieldProcessor;
import com.github.jferard.javamcsv.description.FieldDescription;
import com.github.jferard.javamcsv.processor.FieldProcessor;
import com.github.jferard.javamcsv.processor.WriteProcessorProvider;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetaCSVData implements CSVParameters {
    public static MetaCSVData DEFAULT;

    static {
        try {
            DEFAULT = new MetaCSVDataBuilder().build();
        } catch (MetaCSVDataException e) {
            throw new AssertionError(e);
        }
    }

    private final boolean doubleQuote;
    private final char escapeChar;
    private final char quoteChar;
    private final boolean skipInitialSpace;
    private final Map<Integer, FieldDescription<?>> descriptionByColIndex;
    private String nullValue;
    private String metaVersion;
    private Map<String, String> meta;
    private Charset encoding;
    private char delimiter;
    private boolean utf8BOM;
    private String lineTerminator;
    private Map<Integer, FieldProcessor<?>> processorByIndex;
    private TextFieldProcessor textFieldProcessor;

    public MetaCSVData(String metaVersion, Map<String, String> meta,
                       Charset encoding, boolean utf8BOM,
                       String lineTerminator, char delimiter,
                       char quoteChar, boolean doubleQuote, char escapeChar,
                       boolean skipInitialSpace,
                       String nullValue,
                       Map<Integer, FieldDescription<?>> descriptionByColIndex) {
        this.metaVersion = metaVersion;
        this.meta = meta;
        this.encoding = encoding;
        this.utf8BOM = utf8BOM;
        this.lineTerminator = lineTerminator;
        this.delimiter = delimiter;
        this.doubleQuote = doubleQuote;
        this.escapeChar = escapeChar;
        this.quoteChar = quoteChar;
        this.skipInitialSpace = skipInitialSpace;
        this.nullValue = nullValue;
        this.descriptionByColIndex = descriptionByColIndex;
        processorByIndex = new HashMap<Integer, FieldProcessor<?>>();
        for (Map.Entry<Integer, FieldDescription<?>> entry : this.descriptionByColIndex
                .entrySet()) {
            processorByIndex.put(entry.getKey(), entry.getValue().toFieldProcessor(this.nullValue));
        }
        textFieldProcessor = new TextFieldProcessor(this.nullValue);
    }

    public Charset getEncoding() {
        return this.encoding;
    }

    public String getLineTerminator() {
        return this.lineTerminator;
    }

    public char getDelimiter() {
        return this.delimiter;
    }

    public FieldDescription<?> getDescription(int c) {
        return this.descriptionByColIndex.get(c);
    }

    @Override
    public String toString() {
        return "MetaCSVData(encoding=" + this.encoding + ", lineTerminator=" +
                Util.escapeLineTerminator(this.lineTerminator) +
                ", delimiter=" + this.delimiter + ", doubleQuote=" + this.doubleQuote +
                ", escapeChar=" + this.escapeChar + ", quoteChar=" + this.quoteChar +
                ", skipInitialSpace=" + this.skipInitialSpace +
                ", descriptionByColIndex=" + this.descriptionByColIndex + ")";
    }

    public boolean isUtf8BOM() {
        return utf8BOM;
    }

    public String getNullValue() {
        return this.nullValue;
    }

    public boolean isDoubleQuote() {
        return this.doubleQuote;
    }

    public char getEscapeChar() {
        return this.escapeChar;
    }

    public char getQuoteChar() {
        return this.quoteChar;
    }

    public boolean isSkipInitialSpace() {
        return this.skipInitialSpace;
    }

    /**
     * @return the utility meta data object
     */
    public MetaCSVMetaData getMetaData() {
        return MetaCSVMetaData.create(this.descriptionByColIndex);
    }

    public List<Integer> getSortedColIndices() {
        List<Integer> indices = new ArrayList<Integer>(this.descriptionByColIndex.keySet());
        Collections.sort(indices);
        return indices;
    }

    public String getMetaVersion() {
        return metaVersion;
    }

    public String getMeta(final String key) {
        return meta.get(key);
    }

    public ReadProcessorProvider toReadProcessorProvider(OnError onError) {
        return new ReadProcessorProvider(this.descriptionByColIndex, this.nullValue, onError);
    }

    public WriteProcessorProvider toWriteProcessorProvider(
            OnError onError) {
        return new WriteProcessorProvider(this.descriptionByColIndex, this.nullValue, onError);
    }

    public ProcessorProvider toProcessorProvider(String nullValue) {
        return new ProcessorProvider(this.descriptionByColIndex, nullValue);
    }
}
