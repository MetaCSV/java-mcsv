java-mcsv - A MetaCSV library for Java

Copyright (C) 2020 J. Férard <https://github.com/jferard>

License: GPLv3

# Overview
py-mcsv is a [MetaCSV](https://github.com/jferard/MetaCSV) library for Java.
I quote the README:

> MetaCSV is an open specification for a CSV file description. This description
> is written in a small auxiliary CSV file that may be stored along wih the 
>CSV file itself. This auxilary file should provide the informations necessary 
>to read and type the content of the CSV file. The standard extension is 
>".mcsv".

Java-mcsv is able to read **and type** the rows of a CSV file, provided that you 
have an appropriate MetaCSV file.

(The [ColumnDet](https://github.com/jferard/ColumnDet) package is able to 
generate automatically a sensible MetaCSV file for a given CSV file.)  

# Example
Here's a basic example. The `example.csv` file reads (encoding: utf-8, 
newline: CRLF): 

    name,date,count
    foo,2020-11-21,15
    foo,2020-11-22,-8

The `example.mcsv` file reads (encoding: utf-8, 
newline: CRLF, see the [MetaCSV format specification](https://github.com/jferard/MetaCSV#full-specification-draft-0)):

    domain,key,value
    data,col/1/type,date/YYYY-MM-dd
    data,col/2/type,integer

The code is:

    File f = getResourceAsFile("example.csv");
    MetaCSVReader reader = MetaCSVReader.create(f);
    System.out.println(reader.getTypes());
    for (MetaCSVRecord record : reader) {
        System.out.println(record);
    }
        
Output:

    {1=date/YYYY-MM-dd, 2=integer}
    MetaCSVRecord(CSVRecord [comment=null, mapping=null, recordNumber=1, values=[name, date, count]] ,[name, date, count])
    MetaCSVRecord(CSVRecord [comment=null, mapping=null, recordNumber=2, values=[foo, 2020-11-21, 15]] ,[foo, Mon Dec 30 00:00:00 CET 2019, 15])
    MetaCSVRecord(CSVRecord [comment=null, mapping=null, recordNumber=3, values=[foo, 2020-11-22, -8]] ,[foo, Mon Dec 30 00:00:00 CET 2019, -8])
