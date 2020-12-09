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

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class MetaCSVData {
    private final boolean doubleQuote;
    private final char escapeChar;
    private final char quoteChar;
    private final boolean skipInitialSpace;
    private final Map<Integer, FieldDescription<?>> descriptionByColIndex;
    private String nullValue;
    private Charset encoding;
    private char delimiter;
    private boolean utf8BOM;
    private String lineTerminator;

    public MetaCSVData(Charset encoding, boolean utf8BOM, String lineTerminator, char delimiter,
                       boolean doubleQuote, char escapeChar, char quoteChar,
                       boolean skipInitialSpace,
                       String nullValue,
                       Map<Integer, FieldDescription<?>> descriptionByColIndex) {
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

    public Map<Integer, FieldDescription<?>> getDescriptionByIndex() {
        return this.descriptionByColIndex;
    }

    public Map<Integer, FieldProcessor<?>> getProcessorByIndex() {
        Map<Integer, FieldProcessor<?>> processorByIndex =
                new HashMap<Integer, FieldProcessor<?>>();
        for (Map.Entry<Integer, FieldDescription<?>> entry : this.descriptionByColIndex.entrySet()) {
            processorByIndex.put(entry.getKey(), entry.getValue().toFieldProcessor(this.nullValue));
        }
        return processorByIndex;
    }

    @Override
    public String toString() {
        return "MetaCSVData(encoding=" + this.encoding + ", lineTerminator=" + this.lineTerminator +
                ", delimiter=" + this.delimiter + ", doubleQuote=" + this.doubleQuote +
                ", escapeChar=" + this.escapeChar + ", quoteChar=" + this.quoteChar +
                ", skipInitialSpace=" + this.skipInitialSpace +
                ", descriptionByColIndex=" + this.descriptionByColIndex + ")";
    }

    public boolean isUtf8BOM() {
        return utf8BOM;
    }

    public Map<Integer, String> getTypes() throws IOException {
        Map<Integer, String> types = new HashMap<Integer, String>();
        for (Map.Entry<Integer, FieldDescription<?>> entry: this.descriptionByColIndex.entrySet()) {
            StringBuilder sb = new StringBuilder();
            entry.getValue().render(sb);
            types.put(entry.getKey(), sb.toString());
        }
        return types;
    }

    public FieldProcessor<?> getDefaultProcessor() {
        return new TextFieldProcessor(this.nullValue);
    }

    public String getNullValue() {
        return this.nullValue;
    }
}
