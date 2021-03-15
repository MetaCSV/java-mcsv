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

import com.github.jferard.javamcsv.description.BooleanFieldDescription;
import com.github.jferard.javamcsv.description.CurrencyDecimalFieldDescription;
import com.github.jferard.javamcsv.description.CurrencyIntegerFieldDescription;
import com.github.jferard.javamcsv.description.DateFieldDescription;
import com.github.jferard.javamcsv.description.DatetimeFieldDescription;
import com.github.jferard.javamcsv.description.DecimalFieldDescription;
import com.github.jferard.javamcsv.description.FieldDescription;
import com.github.jferard.javamcsv.description.FloatFieldDescription;
import com.github.jferard.javamcsv.description.IntegerFieldDescription;
import com.github.jferard.javamcsv.description.PercentageDecimalFieldDescription;
import com.github.jferard.javamcsv.description.PercentageFloatFieldDescription;
import com.github.jferard.javamcsv.description.TextFieldDescription;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class ColTypeParser {
    private final ObjectTypeParser objectParser;

    public ColTypeParser(ObjectTypeParser objectParser) {
        this.objectParser = objectParser;
    }

    public FieldDescription<?> parseColType(String value) throws MetaCSVParseException {
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
        } else if (valueType.equals("object")) {
            fieldDescription = this.objectParser.parse(parameters);
        } else {
            throw new MetaCSVParseException("Unknown col n type: " + valueType);
        }
        return fieldDescription;
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
            return new CurrencyIntegerFieldDescription(pre, symbol, numberDescription);
        } else if (numberType.equals("decimal")) {
            FieldDescription<BigDecimal> numberDescription =
                    parseDecimal(parameters.subList(3, parameters.size()));
            return new CurrencyDecimalFieldDescription(pre, symbol, numberDescription);
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
            return new PercentageFloatFieldDescription(pre, symbol, numberDescription);
        } else if (numberType.equals("decimal")) {
            FieldDescription<BigDecimal> numberDescription =
                    parseDecimal(parameters.subList(3, parameters.size()));
            return new PercentageDecimalFieldDescription(pre, symbol, numberDescription);
        } else {
            throw new MetaCSVParseException("Unknown currency number type: " + parameters);
        }
    }
}
