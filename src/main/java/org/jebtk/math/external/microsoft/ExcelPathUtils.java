/**
 * Copyright 2017 Antony Holmes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jebtk.math.external.microsoft;

import org.jebtk.core.io.ExtTest;
import org.jebtk.core.io.PathUtils.Ext;

/**
 * The Class ExcelPathUtils.
 */
public class ExcelPathUtils {

  /**
   * The Class ExcelExt.
   */
  public static class ExcelExt extends Ext {

    /**
     * Xlsx.
     *
     * @return the ext test
     */
    public ExtTest xlsx() {
      return type("xlsx");
    }

    /**
     * Xls.
     *
     * @return the ext test
     */
    public ExtTest xls() {
      return type("xls");
    }
  }

  /**
   * Ext.
   *
   * @return the excel ext
   */
  public static ExcelExt ext() {
    return new ExcelExt();
  }
}
