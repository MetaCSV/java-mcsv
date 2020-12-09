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

public class BooleanFieldProcessor implements FieldProcessor<Boolean> {
    private final String trueWord;
    private final String falseWord;
    private final String nullValue;

    public BooleanFieldProcessor(String trueWord, String falseWord, String nullValue) {
        this.trueWord = trueWord;
        this.falseWord = falseWord;
        this.nullValue = nullValue;
    }

    @Override
    public Boolean toObject(String text) throws MetaCSVReadException {
        if (text == null || text.trim().equals(this.nullValue)) {
            return null;
        }
        text = text.trim();
        if (text.equals(this.trueWord)) {
            return true;
        } else if (text.equals(this.falseWord)) {
            return false;
        } else {
            throw new MetaCSVReadException("Unknown boolean: "+text+" ("+this.trueWord+"/"+this.falseWord+")");
        }
    }

    @Override
    public String toString(Boolean value) throws MetaCSVWriteException {
        if (value == null) {
            return this.nullValue;
        } else if (value) {
            return this.trueWord;
        } else {
            return this.falseWord;
        }
    }
}
