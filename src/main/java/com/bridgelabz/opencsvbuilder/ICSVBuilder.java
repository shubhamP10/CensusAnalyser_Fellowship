package com.bridgelabz.opencsvbuilder;

import java.io.Reader;
import java.util.Iterator;

public interface ICSVBuilder<E> {
   Iterator<E> getCSVFileIterator(Reader reader, Class csvClass) throws CSVBuilderException;
}
