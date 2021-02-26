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

public class CSVFormatHelper {
    public static CSVFormat getCSVFormat(CSVParameters data) {
        CSVFormat format = CSVFormat.DEFAULT;
        char delimiter = data.getDelimiter();
        if (delimiter != format.getDelimiter()) {
            format = format.withDelimiter(delimiter);
        }

        char quoteChar = data.getQuoteChar();
        Character formatQuoteCharacter = format.getQuoteCharacter();
        assert formatQuoteCharacter != null;
        if (quoteChar == '\0') {
            format = format.withQuote(null);
        } else if (quoteChar != formatQuoteCharacter) {
            format = format.withQuote(quoteChar);
        }

        String lineTerminator = data.getLineTerminator();
        if (!lineTerminator.equals(format.getRecordSeparator())) {
            format = format.withRecordSeparator(lineTerminator);
        }

        boolean doubleQuote = data.isDoubleQuote();
        if (doubleQuote) {
            format = format.withEscape(null);
        } else {
            char escapeChar = data.getEscapeChar();
            Character formatEscapeCharacter = format.getEscapeCharacter();
            assert formatEscapeCharacter == null;
            if (escapeChar != '\0' && escapeChar != '"') {
                format = format.withEscape(escapeChar);
            }
        }

        boolean skipInitialSpace = data.isSkipInitialSpace();
        if (skipInitialSpace != format.getIgnoreSurroundingSpaces()) {
            format = format.withIgnoreSurroundingSpaces(skipInitialSpace);
        }

        return format;
    }
}
