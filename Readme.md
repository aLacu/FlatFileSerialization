# Flat File Serialization (ffs)

This is a simple library to automanage I/O with FixedWidthFormat type file (https://www.ibm.com/support/knowledgecenter/SSFPJS_8.6.0/com.ibm.wbpm.wid.integ.doc/topics/rfixwidth.html).
Or really anything that is a flat file with bounded field lengths.
You can find a sample to "learn" how to use it.

It's quite simple, there are 5 main objects:
- the @FixedWidthField with all the subfield on how you want it to be managed
- the ObjectMapper is the actual translator String to Object and viceversa
- FixedWidthFormatStreamBuilder to build your input or output stream
- the @FixedWidthCustomParser if you want to manage values directly
- the AnnotationProcessor and Autogenerate(JSON or FF)Definition, to generate compile time how the file will be composed.

It still misses a compile-time check on the field positioning, but it will work.

**Version 1.0.1**
Now the keychange cycling is being made through a stateful stream instead of grouping by operation (memory intensive).
It results 2 times slower than simple reading, which is good.

Any question or suggestion is more than welcome.