/**
 * Copyright © 2016-2026 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.common.data.edqs;

import org.thingsboard.server.common.data.kv.DataType;

/**
 * Typed attribute or latest-TS value stored in the EDQS index (data point).
 */

public interface DataPoint extends Comparable<DataPoint> {

    String NOT_SUPPORTED = "Not supported!";
   /**
    * Returns ts.
    *
    * @return the long result
    * @throws Exception if an unexpected error occurs during processing
    */


    long getTs();
  /**
   * Returns type.
   *
   * @return {@link DataType}
   * @throws Exception if an unexpected error occurs during processing
   */


    DataType getType();
 /**
  * Returns str.
  *
  * @return {@link String}
  * @throws Exception if an unexpected error occurs during processing
  */


    String getStr();
  /**
   * Returns long.
   *
   * @return the long result
   * @throws Exception if an unexpected error occurs during processing
   */


    long getLong();
 /**
  * Returns double.
  *
  * @return the double result
  * @throws Exception if an unexpected error occurs during processing
  */


    double getDouble();
   /**
    * Returns bool.
    *
    * @return the boolean result
    * @throws Exception if an unexpected error occurs during processing
    */


    boolean getBool();
     /**
      * Returns json.
      *
      * @return {@link String}
      * @throws Exception if an unexpected error occurs during processing
      */


    String getJson();
    /**
     * Value to string.
     *
     * @return {@link String}
     * @throws Exception if an unexpected error occurs during processing
     */


    String valueToString();

}
