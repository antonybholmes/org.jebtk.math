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

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.jebtk.core.ColorUtils;
import org.jebtk.core.NameGetter;
import org.jebtk.core.collections.ArrayListCreator;
import org.jebtk.core.collections.CollectionUtils;
import org.jebtk.core.collections.DefaultTreeMap;
import org.jebtk.core.collections.IterMap;
import org.jebtk.core.collections.UniqueArrayList;
import org.jebtk.core.event.ChangeListeners;
import org.jebtk.core.io.FileUtils;
import org.jebtk.core.io.Io;
import org.jebtk.core.io.PathUtils;
import org.jebtk.core.json.Json;
import org.jebtk.core.json.JsonArray;
import org.jebtk.core.json.JsonObject;
import org.jebtk.core.json.JsonRepresentation;
import org.jebtk.core.text.TextUtils;
import org.jebtk.core.xml.XmlRepresentation;
import org.jebtk.core.xml.XmlUtils;
import org.jebtk.math.cluster.Cluster;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The class MatrixGroup.
 */
public class MatrixGroup extends ChangeListeners
implements NameGetter, JsonRepresentation, XmlRepresentation,
Comparable<MatrixGroup>, Iterable<Pattern> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /**
   * The member regexes.
   */
  private List<Pattern> mRegexes = new UniqueArrayList<Pattern>();

  /**
   * The member color.
   */
  private Color mColor = Color.BLACK;

  /**
   * The member name.
   */
  private String mName = null;

  /** The m case sensitive. */
  private boolean mCaseSensitive = false;

  /**
   * Instantiates a new matrix group.
   *
   * @param name the name
   * @param regexes the regexes
   * @param color the color
   */
  public MatrixGroup(String name, Collection<Pattern> regexes, Color color) {
    this(name, regexes, false, color);
  }

  /**
   * Instantiates a new matrix group.
   *
   * @param name the name
   * @param regexes the regexes
   * @param caseSensitive the case sensitive
   * @param color the color
   */
  public MatrixGroup(String name, Collection<Pattern> regexes,
      boolean caseSensitive, Color color) {
    mName = name;
    mColor = color;

    mRegexes.addAll(regexes);

    setCaseSensitive(caseSensitive);
  }

  /**
   * Instantiates a new matrix group.
   *
   * @param name the name
   * @param color the color
   */
  public MatrixGroup(String name, Color color) {
    mName = name;
    mColor = color;
  }

  /**
   * Instantiates a new matrix group.
   *
   * @param group the group
   */
  public MatrixGroup(MatrixGroup group) {
    this(group.mName, group.mRegexes, group.mColor);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.xml.XmlRepresentation#toXml(org.w3c.dom.Document)
   */
  @Override
  public Element toXml(Document doc) {
    Element ge = doc.createElement("group");
    ge.setAttribute("name", getName());
    ge.setAttribute("color", ColorUtils.toHtml(getColor()));

    for (Pattern p : mRegexes) {
      Element se = doc.createElement("search");
      se.appendChild(doc.createCDATASection(p.toString()));

      ge.appendChild(se);
    }

    return ge;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.common.json.JsonRepresentation#toJson()
   */
  @Override
  public Json toJson() {
    Json json = new JsonObject();

    json.add("name", mName);
    json.add("color", ColorUtils.toHtml(mColor));
    json.add("case-sensitive", mCaseSensitive);

    Json searches = json.createArray("searches");

    for (Pattern pattern : mRegexes) {
      searches.add(pattern.toString());
    }

    return json;
  }

  /**
   * Sets the name.
   *
   * @param name the new name
   */
  public void setName(String name) {
    mName = name;

    fireChanged();
  }

  /**
   * Sets the color.
   *
   * @param color the new color
   */
  public void setColor(Color color) {
    mColor = color;

    fireChanged();
  }

  /**
   * Sets the case sensitive.
   *
   * @param caseSensitive the new case sensitive
   */
  public void setCaseSensitive(boolean caseSensitive) {
    mCaseSensitive = caseSensitive;

    fireChanged();
  }

  /**
   * Gets the case sensitive.
   *
   * @return the case sensitive
   */
  public boolean getCaseSensitive() {
    return mCaseSensitive;
  }

  /**
   * Sets the regexes.
   *
   * @param regexes the new regexes
   * @return
   */
  public MatrixGroup setRegex(String regex, String... regexes) {
    mRegexes.clear();

    addRegex(regex, regexes);

    return this;
  }

  /**
   * Sets the regexes.
   *
   * @param regexes the new regexes
   * @return
   */
  public MatrixGroup setRegex(Pattern regex, Pattern... regexes) {
    mRegexes.clear();

    addRegex(regex, regexes);

    return this;
  }

  /**
   * Sets the regexes.
   *
   * @param regexes the new regexes
   * @return
   */
  public MatrixGroup setRegexes(Collection<Pattern> regexes) {

    mRegexes.clear();

    addRegexes(regexes);

    return this;
  }

  /**
   * Adds the regex.
   *
   * @param regex the regex
   * @return
   */
  public MatrixGroup addRegex(String regex) {
    // Remove characters that look like regex reserved characters
    // before constructing pattern
    return addRegex(Pattern
        .compile(".*" + regex.replaceFirst("^\\.\\*", TextUtils.EMPTY_STRING)
        .replaceFirst("\\.\\*$", TextUtils.EMPTY_STRING) + ".*"));
  }

  /**
   * Adds the regexes.
   *
   * @param regexes the regexes
   * @return
   */
  public MatrixGroup addRegex(String regex, String... regexes) {
    addRegex(regex);

    for (String r : regexes) {
      addRegex(r);
    }

    return this;
  }

  /**
   * Adds the regexes.
   *
   * @param regexes the regexes
   * @return
   */
  public MatrixGroup addRegex(Pattern regex, Pattern... regexes) {
    mRegexes.add(regex);

    for (Pattern r : regexes) {
      mRegexes.add(r);
    }

    return this;
  }

  /**
   * Adds the regexes.
   *
   * @param regexes the regexes
   * @return
   */
  public MatrixGroup addRegexes(Collection<Pattern> regexes) {
    mRegexes.addAll(regexes);

    return this;
  }

  /**
   * Add the patterns from another group to this group.
   * 
   * @param g
   * @return
   */
  public MatrixGroup union(MatrixGroup g) {
    for (Pattern p : g) {
      addRegex(p);
    }

    return this;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.abh.lib.NameProperty#getName()
   */
  public String getName() {
    return mName;
  }

  /**
   * Returns the name of the group.
   *
   * @return the color
   */
  public Color getColor() {
    return mColor;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  @Override
  public int compareTo(MatrixGroup g) {
    return mName.compareTo(g.mName);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object g) {
    if (!(g instanceof MatrixGroup)) {
      return false;
    }

    return compareTo((MatrixGroup) g) == 0;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return mName.hashCode();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Iterable#iterator()
   */
  @Override
  public Iterator<Pattern> iterator() {
    return mRegexes.iterator();
  }

  public List<Pattern> getRegexes() {
    return Collections.unmodifiableList(mRegexes);
  }

  /**
   * Returns the number of columns in the group.
   *
   * @return the count
   */
  public int getCount() {
    return mRegexes.size();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return mName + ":" + ColorUtils.toHtml(mColor) + ":"
        + TextUtils.join(mRegexes, TextUtils.COMMA_DELIMITER);
  }

  /**
   * Creates the.
   *
   * @param name the name
   * @param regex the regex
   * @param color the color
   * @return the matrix group
   */
  public static MatrixGroup create(String name, String regex, Color color) {
    return create(name,
        TextUtils.fastSplit(regex, TextUtils.COMMA_DELIMITER),
        color);
  }

  /**
   * Creates the.
   *
   * @param name the name
   * @param regexes the regexes
   * @param color the color
   * @return the matrix group
   */
  public static MatrixGroup create(String name,
      List<String> regexes,
      Color color) {
    return new MatrixGroup(name, TextUtils.compile(regexes), color);
  }

  /**
   * Load groups.
   *
   * @param file the file
   * @param matrix the matrix
   * @return the list
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static List<MatrixGroup> loadGroups(Path file, DataFrame matrix)
      throws IOException {
    if (file == null) {
      return null;
    }

    String ext = PathUtils.getFileExt(file);

    if (ext.equals("mgrp2")) {
      return loadMgrp2(file, matrix);
    } else {
      return loadMgrp1(file, matrix);
    }

  }

  /**
   * Reads a group file into memory.
   *
   * @param file the file
   * @param matrix the matrix
   * @return the list
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static List<MatrixGroup> loadMgrp1(Path file, DataFrame matrix)
      throws IOException {
    List<MatrixGroup> groups = new ArrayList<MatrixGroup>();

    String line;
    List<String> tokens;

    BufferedReader reader = FileUtils.newBufferedReader(file);

    // skip header
    reader.readLine();

    try {
      while ((line = reader.readLine()) != null) {
        if (Io.isEmptyLine(line)) {
          continue;
        }

        tokens = TextUtils.fastSplit(line, TextUtils.TAB_DELIMITER);

        // Labels

        String name = tokens.get(0);
        Color color = ColorUtils.decodeHtmlColor(tokens.get(1));
        List<String> searchTerms = TextUtils
            .removeEmptyElements(CollectionUtils.subList(tokens, 2));

        MatrixGroup group = create(name, searchTerms, color);

        /*
         * for (String term : searchTerms) { System.err.println(name + " terms "
         * + term);
         * 
         * List<Integer> indices = TextUtils.matches(matrix.getColumnNames(),
         * term);
         * 
         * for (int row : indices) { if (inUse.contains(row)) { continue; }
         * 
         * //inUse.add(row);
         * 
         * group.add(row); } }
         */

        groups.add(group);
      }
    } finally {
      reader.close();
    }

    return groups;
  }

  /**
   * Load mgrp2.
   *
   * @param file the file
   * @param matrix the matrix
   * @return the list
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static List<MatrixGroup> loadMgrp2(Path file, DataFrame matrix)
      throws IOException {
    if (file == null) {
      return null;
    }

    if (!PathUtils.getFileExt(file).equals("mgrp2")) {
      return null;
    }

    List<MatrixGroup> groups = new ArrayList<MatrixGroup>();

    String line;
    List<String> tokens;

    Map<String, MatrixGroup> groupMap = new HashMap<String, MatrixGroup>();

    BufferedReader reader = FileUtils.newBufferedReader(file);

    // skip header
    reader.readLine();

    try {
      while ((line = reader.readLine()) != null) {
        if (Io.isEmptyLine(line)) {
          continue;
        }

        tokens = TextUtils.fastSplit(line, TextUtils.TAB_DELIMITER);

        // Labels

        String name = tokens.get(0);
        Color color = ColorUtils.decodeHtmlColor(tokens.get(1));
        String sample = tokens.get(2);

        MatrixGroup group;

        if (groupMap.containsKey(name)) {
          group = groupMap.get(name);
        } else {
          group = new MatrixGroup(name, color);

          groupMap.put(name, group);
          groups.add(group);
        }

        group.addRegex(sample);
      }
    } finally {
      reader.close();
    }

    return groups;
  }

  /**
   * Reads a group file into memory.
   *
   * @param file the file
   * @param matrix the matrix
   * @return the list
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static List<MatrixGroup> createRowGroupsByName(Path file,
      DataFrame matrix) throws IOException {
    List<MatrixGroup> groups = new ArrayList<MatrixGroup>();

    String line;
    List<String> tokens;

    BufferedReader reader = FileUtils.newBufferedReader(file);

    // skip header
    reader.readLine();

    try {
      while ((line = reader.readLine()) != null) {
        if (Io.isEmptyLine(line)) {
          continue;
        }

        tokens = TextUtils.fastSplit(line, TextUtils.TAB_DELIMITER);

        // Labels

        String name = tokens.get(0);
        Color color = ColorUtils.decodeHtmlColor(tokens.get(1));
        List<String> searchTerms = CollectionUtils.subList(tokens, 2);

        MatrixGroup group = create(name, searchTerms, color);

        groups.add(group);

        /*
         * for (String term : searchTerms) { List<Integer> indices =
         * TextUtils.matches(matrix.getRowNames(), term);
         * 
         * for (int row : indices) { if (inUse.contains(row)) { continue; }
         * 
         * inUse.add(row);
         * 
         * group.add(row); } }
         */
      }
    } finally {
      reader.close();
    }

    return groups;
  }

  /**
   * Write groups to file.
   *
   * @param <X> the generic type
   * @param file the file
   * @param groups the groups
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static <X extends MatrixGroup> void saveGroups(Path file,
      List<X> groups) throws IOException {
    BufferedWriter writer = FileUtils.newBufferedWriter(file);

    try {
      writer.write("name");
      writer.write(TextUtils.TAB_DELIMITER);
      writer.write("color");
      writer.write(TextUtils.TAB_DELIMITER);
      writer.write("seach_terms");
      writer.newLine();

      for (MatrixGroup group : groups) {
        writer.write(group.getName());
        writer.write(TextUtils.TAB_DELIMITER);
        writer.write(ColorUtils.toHtml(group.getColor()));
        writer.write(TextUtils.TAB_DELIMITER);
        writer.write(TextUtils.join(CollectionUtils.toString(group),
            TextUtils.TAB_DELIMITER));
        writer.newLine();
      }

    } finally {
      writer.close();
    }
  }

  /**
   * Returns a list of the row groups in use in a matrix with each group
   * containing the valid indices.
   *
   * @param matrix the matrix
   * @param groups the groups
   * @return the list
   */
  public static List<List<Integer>> findRowIndices(DataFrame matrix,
      List<MatrixGroup> groups) {
    return findIndices(matrix.getRowNames(), groups);
  }

  public static <X extends MatrixGroup> List<Integer> findRowIndices(
      DataFrame m,
      X group) {
    int n = m.getRows();

    List<Integer> ret = new ArrayList<Integer>(n);

    // Use the first set of indices
    String[] v = m.getIndex().getText(0);
    
    for (int i = 0; i < n; ++i) {
      
      for (Pattern regex : group) {
        //System.err.println("search for " + regex + " " + i + " " + v[i]);
        
        if (TextUtils.find(v[i], regex)) {
          ret.add(i);
          break;
        }
      }
    }

    if (ret.size() > 0) {
      Collections.sort(ret);
    }

    return ret;
  }

  /**
   * Returns an ordered list of lists where each list corresponds to a group and
   * each list contains the indices of the columns that match the group.
   *
   * @param m the matrix
   * @param groups the groups
   * @return the list
   */
  public static List<List<Integer>> findColumnIndices(DataFrame m,
      List<? extends MatrixGroup> groups) {
    // return findIndices(matrix.getColumnNames(), groups);

    List<List<Integer>> ret = new ArrayList<List<Integer>>();

    for (MatrixGroup group : groups) {
      ret.add(findColumnIndices(m, group));
    }

    return ret;
  }

  /**
   * Find column indices.
   *
   * @param <X> the generic type
   * @param m the m
   * @param group the group
   * @return the list
   */
  public static <X extends MatrixGroup> List<Integer> findColumnIndices(
      DataFrame m,
      X group) {
    int n = m.getCols();

    List<Integer> ret = new ArrayList<Integer>(n);

    for (int i = 0; i < n; ++i) {
      
      for (Pattern regex : group) {
        //System.err.println("search for " + regex + " " + m.getColumnHeader().getHeader(i) + " " + TextUtils.find(m.getColumnHeader().getHeader(i), regex));
        
        if (TextUtils.find(m.getColumnHeader().getHeader(i), regex)) {
          ret.add(i);
          break;
        }
      }
    }

    if (ret.size() > 0) {
      Collections.sort(ret);
    }

    return ret;
  }

  /**
   * Return a map of the column indices and the groups they belong to.
   * 
   * @param m
   * @param groups
   * @return
   */
  public static <X extends MatrixGroup> IterMap<Integer, List<X>> indexGroupMap(
      DataFrame m,
      List<X> groups) {
    int n = m.getCols();

    IterMap<Integer, List<X>> ret = DefaultTreeMap.create(new ArrayListCreator<X>());

    String[] names = m.getColumnNames();
    
    for (int i = 0; i < n; ++i) {
      //boolean found = false;
      
      for (X group : groups) {
        for (Pattern regex : group) {
          
          if (TextUtils.find(names[i], regex)) {
            ret.get(i).add(group);
            //found = true;
            break;
          }
        }
        
        //if (found) {
        //  break;
        //}
      }
    }

    return ret;
  }

  /**
   * Find all column indices.
   *
   * @param m the m
   * @param groups the groups
   * @return the list
   */
  public static List<Integer> findAllColumnIndices(DataFrame m,
      List<? extends MatrixGroup> groups) {
    List<Integer> ret = new UniqueArrayList<Integer>(m.getCols());

    for (MatrixGroup group : groups) {
      ret.addAll(findColumnIndices(m, group));
    }

    Collections.sort(ret);

    return ret;
  }

  /**
   * Returns a list of the row groups in use in a matrix with each group
   * containing the valid indices.
   *
   * @param ids the ids
   * @param groups the groups
   * @return the list
   */
  public static List<List<Integer>> findIndices(String[] ids,
      List<? extends MatrixGroup> groups) {
    List<List<Integer>> ret = new ArrayList<List<Integer>>();

    for (MatrixGroup group : groups) {
      ret.add(findIndices(ids, group));
    }

    return ret;
  }

  /**
   * Finds all the indices covered by all of the groups returning them in order.
   *
   * @param ids the ids
   * @param groups the groups
   * @return the list
   */
  public static List<Integer> findAllIndices(String[] ids,
      List<? extends MatrixGroup> groups) {
    List<Integer> ret = new UniqueArrayList<Integer>();

    for (MatrixGroup group : groups) {
      ret.addAll(findIndices(ids, group));
    }

    Collections.sort(ret);

    return ret;
  }

  /**
   * Find indices.
   *
   * @param names the names
   * @param group the group
   * @return the list
   */
  public static List<Integer> findIndices(String[] names,
      MatrixGroup group) {
    Set<Integer> ret = new HashSet<Integer>();

    for (Pattern regex : group) {
      //System.err.println("m group " + Arrays.toString(names) + " " + regex);
      
      ret.addAll(TextUtils.find(names, regex));
    }

    return CollectionUtils.sort(ret);
  }

  /**
   * Returns a list of the row groups in use in a matrix with each group
   * containing the valid indices.
   *
   * @param groups the groups
   * @param ids the ids
   * @return the list
   */
  public static List<List<Integer>> findGroupIndices(
      List<? extends MatrixGroup> groups,
      List<List<String>> ids) {
    List<List<Integer>> ret = new ArrayList<List<Integer>>();

    for (MatrixGroup group : groups) {
      List<Integer> newGroup = new ArrayList<Integer>();

      for (Pattern regex : group) {
        for (int i = 0; i < ids.size(); ++i) {
          List<Integer> indices = TextUtils.find(ids.get(i), regex);

          if (indices.size() == 0) {
            continue;
          }

          newGroup.add(i);
        }
      }

      ret.add(newGroup);
    }

    return ret;
  }

  /**
   * Orders groups by the lowest indices they contain. The groups will be
   * arranged in the order they appear first.
   *
   * @param m the m
   * @param groups the groups
   * @return the list
   */
  public static List<MatrixGroup> orderGroups(DataFrame m,
      List<MatrixGroup> groups) {
    Map<Integer, List<MatrixGroup>> orderedGroups = arrangeGroupsByIndex(m,
        groups);

    List<MatrixGroup> ret = orderGroups(orderedGroups);

    return ret;
  }

  /**
   * Returns a list of groups ordered by the lowest indices they contain. If
   * multiple groups contain the same indices they will be ordered by name. Thus
   * this list may not be alphabetical but is designed for display purposes
   * (e.g. on legends) so that groups can be listed in the order they appear on
   * plots etc.
   *
   * @param groups the groups
   * @return the list
   */
  public static List<MatrixGroup> orderGroups(
      Map<Integer, List<MatrixGroup>> groups) {
    if (groups == null || groups.size() == 0) {
      return null;
    }

    Set<MatrixGroup> used = new HashSet<MatrixGroup>();

    List<MatrixGroup> ret = new ArrayList<MatrixGroup>(groups.size());

    List<Integer> orderedKeys = CollectionUtils.sort(groups.keySet());

    for (int key : orderedKeys) {
      List<MatrixGroup> temp = new ArrayList<MatrixGroup>();

      for (MatrixGroup group : groups.get(key)) {
        if (used.contains(group)) {
          continue;
        }

        temp.add(group);
        used.add(group);
      }

      // sort the groups by name
      Collections.sort(temp);

      ret.addAll(temp);
    }

    return ret;
  }

  /**
   * Returns a map of indices and their corresponding groups. Groups will always
   * appear in the same order in each index.
   *
   * @param m the m
   * @param groups the groups
   * @return the map
   */
  public static Map<Integer, List<MatrixGroup>> arrangeGroupsByIndex(
      DataFrame m,
      List<? extends MatrixGroup> groups) {

    Map<Integer, List<MatrixGroup>> orderedGroups = DefaultTreeMap
        .create(new ArrayListCreator<MatrixGroup>());

    for (MatrixGroup group : groups) {
      List<Integer> indices = findColumnIndices(m, group);

      for (int i : indices) {
        orderedGroups.get(i).add(group);
      }
    }

    return orderedGroups;
  }

  /**
   * Orders groups so they appear in the order of the cluster and not the
   * matrix.
   *
   * @param m the m
   * @param groups the groups
   * @param rootCluster the root cluster
   * @return the map
   */
  public static Map<Integer, List<MatrixGroup>> arrangeGroupsByCluster(
      DataFrame m,
      List<MatrixGroup> groups,
      Cluster rootCluster) {
    if (groups == null || groups.size() == 0 || rootCluster == null) {
      return null;
    }

    // First get the normal order
    Map<Integer, List<MatrixGroup>> orderedGroups = MatrixGroup
        .arrangeGroupsByIndex(m, groups);

    List<Integer> newOrder = new ArrayList<Integer>();

    Deque<Cluster> stack = new ArrayDeque<Cluster>();

    stack.push(rootCluster);

    while (stack.size() > 0) {
      Cluster cluster = stack.pop();

      if (cluster.isParent()) {
        Cluster c1 = cluster.getChild1();
        Cluster c2 = cluster.getChild2();

        // Determine which is closest
        stack.push(c2);
        stack.push(c1);
      } else {
        // add the indices in the order they appear in the
        // cluster tree
        newOrder.add(cluster.getId());
      }
    }

    // reorder the groups

    Map<Integer, List<MatrixGroup>> ret = new HashMap<Integer, List<MatrixGroup>>();

    for (int i = 0; i < newOrder.size(); ++i) {
      ;
    }

    return ret;
  }

  /**
   * Parse a colon delimited string representation of a group.
   *
   * @param groupDefinition the group definition
   * @return the matrix group
   */
  public static MatrixGroup parse(String groupDefinition) {
    List<String> tokens = TextUtils.fastSplit(groupDefinition,
        TextUtils.COLON_DELIMITER);

    String name = tokens.get(0);

    Color color = ColorUtils.decodeHtmlColor(tokens.get(1));

    tokens = TextUtils.fastSplit(tokens.get(2), TextUtils.COMMA_DELIMITER);

    return create(name, tokens, color);
  }

  /**
   * Write xml.
   *
   * @param file the file
   * @param groups the groups
   * @throws IOException Signals that an I/O exception has occurred.
   * @throws TransformerException the transformer exception
   * @throws ParserConfigurationException the parser configuration exception
   */
  public static void writeXml(Path file,
      Collection<? extends MatrixGroup> groups)
          throws IOException, TransformerException, ParserConfigurationException {
    Document doc = XmlUtils.createDoc();

    Element ge = doc.createElement("groups");

    for (MatrixGroup group : groups) {
      ge.appendChild(group.toXml(doc));
    }

    doc.appendChild(ge);

    XmlUtils.writeXml(doc, file);

    // LOG.info("Wrote settings to {}", Path.getAbsoluteFile());
  }

  /**
   * Write json.
   *
   * @param file the file
   * @param groups the groups
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static void writeJson(Path file,
      Collection<? extends MatrixGroup> groups) throws IOException {
    Json json = new JsonArray();

    for (MatrixGroup g : groups) {
      json.add(g.toJson());
    }

    Json.prettyWrite(json, file);

    // LOG.info("Wrote settings to {}", Path.getAbsoluteFile());
  }

}
