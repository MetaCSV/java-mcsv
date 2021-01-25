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

public enum DataType {
    OBJECT(ObjectFieldDescription.INSTANCE), BOOLEAN(BooleanFieldDescription.INSTANCE),
    CURRENCY_DECIMAL(CurrencyDecimalFieldDescription.INSTANCE),
    CURRENCY_INTEGER(CurrencyIntegerFieldDescription.INSTANCE), DATE(DateFieldDescription.INSTANCE),
    DATETIME(DatetimeFieldDescription.INSTANCE), DECIMAL(DecimalFieldDescription.INSTANCE), FLOAT(
            FloatFieldDescription.INSTANCE), INTEGER(IntegerFieldDescription.INSTANCE),
    PERCENTAGE_DECIMAL(PercentageDecimalFieldDescription.INSTANCE),
    PERCENTAGE_FLOAT(PercentageFloatFieldDescription.INSTANCE), TEXT(TextFieldDescription.INSTANCE);

    private final FieldDescription<?> description;

    DataType(FieldDescription<?> description) {
        this.description = description;
    }

    public FieldDescription<?> getDefaultDescription() {
        return description;
    }
}
