package com.bridgelabz.censusanalyser.utility;

import com.bridgelabz.censusanalyser.exception.CensusAnalyserException;

import java.io.Reader;
import java.util.Iterator;

public interface ICSVBuilder<E> {
   Iterator<E> getCSVFileIterator(Reader reader, Class csvClass) throws CensusAnalyserException;
}
