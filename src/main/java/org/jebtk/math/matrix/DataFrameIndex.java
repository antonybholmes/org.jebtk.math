/**
 * Copyright (C) 2016, Antony Holmes
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. Neither the name of copyright holder nor the names of its contributors 
 *     may be used to endorse or promote products derived from this software 
 *     without specific prior written permission. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.jebtk.math.matrix;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jebtk.core.collections.CyclicList;
import org.jebtk.core.collections.UniqueArrayList;
import org.jebtk.core.event.ChangeListeners;

/**
 * Provides annotation for a matrix.
 * 
 * @author Antony Holmes
 *
 */
public class DataFrameIndex extends ChangeListeners implements Iterable<String> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  public static final String HEADER_NAMES = "Headings";

  /**
   * The member names.
   */
  private List<String> mNames = new CyclicList<String>(
      new UniqueArrayList<String>());

  /**
   * The member name index map.
   */
  // private Map<Integer, String> mNameIndexMap =
  // new HashMap<Integer, String>();

  /**
   * The member annotation.
   */
  private Map<String, Matrix> mAnnotationMap;

  private int mSize;


  /**
   * Instantiates a new annotation.
   *
   * @param size the size
   */
  public DataFrameIndex(int size) {
    mAnnotationMap = new HashMap<String, Matrix>(10);
    mSize = size;
  }

  /**
   * Sets the num annotation.
   *
   * @param name the name
   * @param values the values
   */
  public void setAnnotation(String name, double[] values) {
    autoCreate(name, MatrixType.NUMBER, NumberType.DOUBLE).setRow(0, values);
    
    //MatrixOperations.numToRow(values, 0, mAnnotationMap.get(name));
  }

  /**
   * Sets the num annotation.
   *
   * @param name the name
   * @param values the values
   */
  public void setAnnotation(String name, int[] values) {
    autoCreate(name, MatrixType.NUMBER, NumberType.INT).setRow(0, values);
    
    //MatrixOperations.numToRow(values, 0, mAnnotationMap.get(name));
  }
  
  public void setAnnotation(String name, long[] values) {
    autoCreate(name, MatrixType.NUMBER, NumberType.LONG).setRow(0, values);
    
    //MatrixOperations.numToRow(values, 0, mAnnotationMap.get(name));
  }
  
  public void setAnnotation(String name, boolean[] values) {
    autoCreate(name, MatrixType.BOOL).setRow(0, values);
    
    //MatrixOperations.numToRow(values, 0, mAnnotationMap.get(name));
  }
  
  public void setAnnotation(String name, String[] values) {
    autoCreate(name, MatrixType.MIXED).setRow(0, values);
    
    //MatrixOperations.textToRow(values, 0, mAnnotationMap.get(name));
  }
  
  public void setAnnotation(String name, Object[] values) {
    autoCreate(name, MatrixType.MIXED).setRow(0, values);
    
    //MatrixOperations.textToRow(values, 0, mAnnotationMap.get(name));
  }
  


  /**
   * Sets the annotation.
   *
   * @param name the name
   * @param m the m
   */
  public void setAnnotation(String name, Matrix m) {
    switch (m.getType()) {
    case NUMBER:
      autoCreate(name, MatrixType.NUMBER);
      break;
    case TEXT:
      autoCreate(name, MatrixType.TEXT);
      break;
    default:
      autoCreate(name, MatrixType.MIXED);
      break;
    }

    mAnnotationMap.get(name).copyRow(m, 0, 0);
  }

  /**
   * Auto create.
   *
   * @param name the name
   * @param type the type
   * @return the matrix
   */
  private Matrix autoCreate(String name, MatrixType type) {
    return autoCreate(name, type, NumberType.DOUBLE);
  }

  /**
   * Ensure a key exists and keep track of the order it is added.
   *
   * @param name the name
   * @param type the type
   * @param intMode the int mode
   * @return the matrix
   */
  private Matrix autoCreate(String name,
      MatrixType type,
      NumberType numberType) {
    if (!mAnnotationMap.containsKey(name)) {
      switch (type) {
      case NUMBER:
        switch (numberType) {
        case INT:
          mAnnotationMap.put(name, new IntMatrix(1, mSize)); //new IntWorksheet(1, mSize)); //1, mSize
          break;
        default:
          mAnnotationMap.put(name, new DoubleMatrix(1, mSize)); //new DoubleWorksheet(1, mSize));
          break;
        }

        break;
      case TEXT:
        mAnnotationMap.put(name, new TextMatrix(1, mSize)); //new TextWorksheet(1, mSize));
        break;
      default:
        mAnnotationMap.put(name, new MixedMatrix(1, mSize)); //new MixedWorksheet(1, mSize));
        break;
      }

      // mNameIndexMap.put(mNames.size(), name);
      mNames.add(name);

      fireChanged();
    }

    return mAnnotationMap.get(name);
  }

  /**
   * Returns an annotation by its index (i.e. the order in which it was
   * created).
   *
   * @param index the index
   * @return the annotation
   */
  public Matrix getAnnotation(int index) {
    return getAnnotation(getName(index));
  }

  /**
   * Returns a list of the annotations by name.
   *
   * @param name the name
   * @return the annotation
   */
  public Matrix getAnnotation(String name) {
    return autoCreate(name, MatrixType.MIXED);
  }

  /**
   * Gets the values.
   *
   * @param index the index
   * @return the values
   */
  public double[] getValues(int index) {
    return getValues(getName(index));
  }

  /**
   * Gets the values.
   *
   * @param name the name
   * @return the values
   */
  public double[] getValues(String name) {
    return mAnnotationMap.get(name).rowToDouble(0);
  }

  /**
   * Gets the text.
   *
   * @param index the index
   * @return the text
   */
  public String[] getText(int index) {
    return getText(getName(index));
  }

  /**
   * Gets the text.
   *
   * @param name the name
   * @return the text
   */
  public String[] getText(String name) {
    return mAnnotationMap.get(name).rowToText(0);
  }

  /**
   * Gets the annotation.
   *
   * @param name the name
   * @param index the index
   * @return the annotation
   */
  public Object getAnnotation(String name, int index) {
    return mAnnotationMap.get(name).get(0, index);
  }

  /**
   * Gets the value.
   *
   * @param name the name
   * @param index the index
   * @return the value
   */
  public double getValue(String name, int index) {
    return mAnnotationMap.get(name).getValue(0, index);
  }

  /**
   * Gets the text.
   *
   * @param name the name
   * @param index the index
   * @return the text
   */
  public String getText(String name, int index) {
    return mAnnotationMap.get(name).getText(0, index);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Iterable#iterator()
   */
  @Override
  public Iterator<String> iterator() {
    return mNames.iterator();
  }

  /**
   * Clear.
   */
  public void clear() {
    mNames.clear();
    mAnnotationMap.clear();
  }

  /**
   * Returns a list of the annotation names in the order in which they were
   * added. This method returns a copy to prevent the internal list from
   * becoming corrupted.
   *
   * @return the names
   */
  public List<String> getNames() {
    return Collections.unmodifiableList(mNames);
  }

  /**
   * Gets the name of the index
   *
   * @param index the index. Annotations are accessed using negative indices
   * @return the name
   */
  public String getName(int index) {
    String name = mNames.get(index);

    // System.err.println("annotation name " + index + " " + name);

    return name;
  }

  /**
   * Sets the annotation.
   *
   * @param name the name
   * @param index the index
   * @param value the value
   */
  public void setAnnotation(String name, int index, double value) {
    autoCreate(name, MatrixType.MIXED);

    // modify the internal representation rather than a copy
    mAnnotationMap.get(name).set(0, index, value);
  }

  /**
   * Sets the annotation.
   *
   * @param name the name
   * @param index the index
   * @param value the value
   */
  public void setAnnotation(String name, int index, int value) {
    autoCreate(name, MatrixType.MIXED);

    // modify the internal representation rather than a copy
    mAnnotationMap.get(name).set(0, index, value);
  }

  /**
   * Sets the annotation.
   *
   * @param name the name
   * @param index the index
   * @param value the value
   */
  public void setAnnotation(String name, int index, String value) {
    autoCreate(name, MatrixType.MIXED);

    // modify the internal representation rather than a copy
    mAnnotationMap.get(name).set(0, index, value);
  }

  /**
   * Sets the annotation.
   *
   * @param name the name
   * @param index the index
   * @param value the value
   */
  public void setAnnotation(String name, int index, Object value) {
    autoCreate(name, MatrixType.MIXED);

    // modify the internal representation rather than a copy
    mAnnotationMap.get(name).set(0, index, value);
  }

  /**
   * Returns the number of annotations.
   * 
   * @return
   */
  public int size() {
    return mAnnotationMap.size();
  }

  public String[] getHeadings() {
    return getText(HEADER_NAMES);
  }
  
  public String getHeader(int index) {
    return mAnnotationMap.get(HEADER_NAMES).getText(0, index);
  }
  
  public void setHeading(int column, String name) {
    setAnnotation(DataFrameIndex.HEADER_NAMES, column, name);
  }
  
  public void setHeadings(String[] values) {
    setAnnotation(HEADER_NAMES, values);
    
    //MatrixOperations.textToRow(values, 0, mAnnotationMap.get(name));
  }

  public void setHeadings(Collection<String> values) {
    String[] names = new String[values.size()];
    values.toArray(names);
    setHeadings(names);
  }
}
