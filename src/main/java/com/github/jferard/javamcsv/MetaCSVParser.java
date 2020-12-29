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

package com.github.jferard.javamcsv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class MetaCSVParser {
    public static MetaCSVParser create(InputStream is) throws IOException {
        Reader reader = new InputStreamReader(is);
        return create(reader);
    }

    public static MetaCSVParser create(Reader reader) throws IOException {
        CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT);
        return new MetaCSVParser(parser);
    }

    private final MetaCSVDataBuilder metaCSVDataBuilder;
    private final CSVParser parser;

    public MetaCSVParser(CSVParser parser) {
        this.parser = parser;
        this.metaCSVDataBuilder = new MetaCSVDataBuilder();
    }

    public MetaCSVData parse() throws MetaCSVParseException, MetaCSVDataException {
        Iterator<CSVRecord> it = parser.iterator();
        this.checkHeader(it);
        while (it.hasNext()) {
            CSVRecord record = it.next();
            String domain = record.get(0);
            String key = record.get(1);
            String value = record.get(2);
            this.parseRow(domain, key, value);
        }
        return this.metaCSVDataBuilder.build();
    }

    private void checkHeader(Iterator<CSVRecord> it) throws MetaCSVParseException {
        if (!it.hasNext()) {
            throw new MetaCSVParseException("Empty file");
        }
        CSVRecord header = it.next();
        if (!(header.get(0).equals("domain") && header.get(1).equals("key") &&
                header.get(2).equals("value"))) {
            throw new MetaCSVParseException("Bad header: " + header.toString());
        }
    }

    private void parseRow(String domain, String key, String value) throws MetaCSVParseException {
        if (domain.equals("file")) {
            this.parseFileRow(key, value);
        } else if (domain.equals("csv")) {
            this.parseCSVRow(key, value);
        } else if (domain.equals("data")) {
            this.parseDataRow(key, value);
        } else {
            throw new MetaCSVParseException("Unknown domain: " + domain);
        }
    }

    private void parseFileRow(String key, String value) throws MetaCSVParseException {
        if (key.equals("encoding")) {
            this.metaCSVDataBuilder.encoding(value);
        } else if (key.equals("bom")) {
            this.metaCSVDataBuilder.bom(this.parseBoolean(value));
        } else if (key.equals("line_terminator")) {
            this.metaCSVDataBuilder.lineTerminator(value);
        } else {
            throw new MetaCSVParseException("Unknown key for domain `file`: " + key);
        }
    }

    private void parseCSVRow(String key, String value) throws MetaCSVParseException {
        if (key.equals("delimiter")) {
            this.metaCSVDataBuilder.delimiter(this.parseOneChar(value));
        } else if (key.equals("double_quote")) {
            this.metaCSVDataBuilder.doubleQuote(this.parseBoolean(value));
        } else if (key.equals("escape_char")) {
            this.metaCSVDataBuilder.escapeChar(this.parseOneChar(value));
        } else if (key.equals("quote_char")) {
            this.metaCSVDataBuilder.quoteChar(this.parseOneChar(value));
        } else if (key.equals("skip_initial_space")) {
            this.metaCSVDataBuilder.skipInitialSpace(this.parseBoolean(value));
        } else {
            throw new MetaCSVParseException("Expected csv key, got: " + value);
        }
    }

    private boolean parseBoolean(String value) throws MetaCSVParseException {
        if (value.equals("true")) {
            return true;
        } else if (value.equals("false")) {
            return false;
        } else {
            throw new MetaCSVParseException("Expected boolean, got: " + value);
        }
    }

    private char parseOneChar(String value) throws MetaCSVParseException {
        if (value.length() != 1) {
            throw new MetaCSVParseException("Expected one char, got: " + value);
        }
        return value.charAt(0);
    }

    private void parseDataRow(String key, String value) throws MetaCSVParseException {
        List<String> keys = Util.parse(key);
        if (keys.get(0).equals("col")) {
            if (keys.size() != 3) {
                throw new MetaCSVParseException("Unknown data key: " + keys);
            }
            this.parseDataColRow(keys.get(1), keys.get(2), value);
        } else if (keys.get(0).equals("null_value")) {
            if (keys.size() != 1) {
                throw new MetaCSVParseException("Unknown data key: " + keys);
            }
            this.metaCSVDataBuilder.nullValue(value);
        } else {
            throw new MetaCSVParseException("Unknown data key: " + key);
        }
    }

    private void parseDataColRow(String colNum, String colKey, String value)
            throws MetaCSVParseException {
        int c;
        try {
            c = Integer.parseInt(colNum);
        } catch (NumberFormatException e) {
            throw new MetaCSVParseException("Unknown col num: " + colNum);
        }
        this.parseDataColRow(c, colKey, value);
    }

    private void parseDataColRow(int c, String colKey, String value) throws MetaCSVParseException {
        if (colKey.equals("type")) {
            this.parseColType(c, value);
        } else {
            throw new MetaCSVParseException("Unknown col n key: " + colKey);
        }
    }

    private void parseColType(int c, String value) throws MetaCSVParseException {
        List<String> values = Util.parse(value);
        String valueType = values.get(0);
        List<String> parameters = values.subList(1, values.size());
        FieldDescription<?> fieldDescription;
        if (valueType.equals("boolean")) {
            fieldDescription = parseBoolean(parameters);
        } else if (valueType.equals("currency")) {
            fieldDescription = parseCurrency(parameters);
        } else if (valueType.equals("date")) {
            fieldDescription = parseDate(parameters);
        } else if (valueType.equals("datetime")) {
            fieldDescription = parseDatetime(parameters);
        } else if (valueType.equals("decimal")) {
            fieldDescription = parseDecimal(parameters);
        } else if (valueType.equals("float")) {
            fieldDescription = parseFloat(parameters);
        } else if (valueType.equals("integer")) {
            fieldDescription = parseInteger(parameters);
        } else if (valueType.equals("percentage")) {
            fieldDescription = parsePercentage(parameters);
        } else if (valueType.equals("text")) {
            fieldDescription = TextFieldDescription.INSTANCE;
        } else if (valueType.equals("any")) {
            fieldDescription = new AnyFieldDescription(parameters);
        } else {
            throw new MetaCSVParseException("Unknown col n type: " + valueType);
        }
        this.metaCSVDataBuilder.colType(c, fieldDescription);
    }

    private FieldDescription<Boolean> parseBoolean(List<String> parameters)
            throws MetaCSVParseException {
        String trueWord;
        String falseWord;
        if (parameters.size() == 1) {
            trueWord = parameters.get(0);
            falseWord = "";
        } else if (parameters.size() == 2) {
            trueWord = parameters.get(0);
            falseWord = parameters.get(1);
        } else {
            throw new MetaCSVParseException("Bad boolean format! " + parameters);
        }
        return new BooleanFieldDescription(trueWord, falseWord);
    }

    private FieldDescription<? extends Number> parseCurrency(List<String> parameters)
            throws MetaCSVParseException {
        boolean pre = parsePre(parameters.get(0));
        String symbol = parameters.get(1);
        String numberType = parameters.get(2);
        if (numberType.equals("integer")) {
            FieldDescription<Long> numberDescription =
                    parseInteger(parameters.subList(3, parameters.size()));
            return new IntegerCurrencyFieldDescription(pre, symbol, numberDescription);
        } else if (numberType.equals("decimal")) {
            FieldDescription<BigDecimal> numberDescription =
                    parseDecimal(parameters.subList(3, parameters.size()));
            return new DecimalCurrencyFieldDescription(pre, symbol, numberDescription);
        } else {
            throw new MetaCSVParseException("Unknown currency number type: " + parameters);
        }
    }

    private FieldDescription<Date> parseDate(List<String> parameters) throws MetaCSVParseException {
        if (parameters.size() == 1) {
            String format = parameters.get(0);
            return DateFieldDescription.create(format);
        } else if (parameters.size() == 2) {
            String format = parameters.get(0);
            String locale = parameters.get(1);
            return DateFieldDescription.create(format, locale);
        } else {
            throw new MetaCSVParseException("Unknown date field: " + parameters);
        }
    }

    private FieldDescription<Date> parseDatetime(List<String> parameters)
            throws MetaCSVParseException {
        if (parameters.size() == 1) {
            String format = parameters.get(0);
            return DatetimeFieldDescription.create(format);
        } else if (parameters.size() == 2) {
            String format = parameters.get(0);
            String locale = parameters.get(1);
            return DatetimeFieldDescription.create(format, locale);
        } else {
            throw new MetaCSVParseException("Unknown datetime field: " + parameters);
        }
    }

    private FieldDescription<Double> parseFloat(List<String> parameters)
            throws MetaCSVParseException {
        if (parameters.size() == 2) {
            return new FloatFieldDescription(parameters.get(0), parameters.get(1));
        } else {
            throw new MetaCSVParseException("Unknown integer field: " + parameters);
        }
    }

    private FieldDescription<BigDecimal> parseDecimal(List<String> parameters)
            throws MetaCSVParseException {
        if (parameters.size() == 2) {
            return new DecimalFieldDescription(parameters.get(0), parameters.get(1));
        } else {
            throw new MetaCSVParseException("Unknown integer field: " + parameters);
        }
    }

    private FieldDescription<Long> parseInteger(List<String> parameters)
            throws MetaCSVParseException {
        if (parameters.size() == 0) {
            return IntegerFieldDescription.INSTANCE;
        } else if (parameters.size() == 1) {
            return new IntegerFieldDescription(parameters.get(0));
        } else {
            throw new MetaCSVParseException("Unknown integer field: " + parameters);
        }
    }

    private boolean parsePre(String prePost) throws MetaCSVParseException {
        if (prePost.equals("pre")) {
            return true;
        } else if (prePost.equals("post")) {
            return false;
        } else {
            throw new MetaCSVParseException("Unknown pre/post: " + prePost);
        }
    }

    private FieldDescription<? extends Number> parsePercentage(List<String> parameters)
            throws MetaCSVParseException {
        boolean pre = parsePre(parameters.get(0));
        String symbol = parameters.get(1);
        String numberType = parameters.get(2);
        if (numberType.equals("float")) {
            FieldDescription<Double> numberDescription =
                    parseFloat(parameters.subList(3, parameters.size()));
            return new FloatPercentageFieldDescription(pre, symbol, numberDescription);
        } else if (numberType.equals("decimal")) {
            FieldDescription<BigDecimal> numberDescription =
                    parseDecimal(parameters.subList(3, parameters.size()));
            return new DecimalPercentageFieldDescription(pre, symbol, numberDescription);
        } else {
            throw new MetaCSVParseException("Unknown currency number type: " + parameters);
        }
    }
}
