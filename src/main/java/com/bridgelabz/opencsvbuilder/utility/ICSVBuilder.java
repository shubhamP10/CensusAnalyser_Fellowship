package com.bridgelabz.opencsvbuilder.utility;

import com.bridgelabz.opencsvbuilder.exception.CSVBuilderException;

import java.io.Reader;
import java.util.Iterator;

public interface ICSVBuilder<E> {
   public Iterator<E> getCSVFileIterator(Reader reader, Class csvClass) throws CSVBuilderException;
}
