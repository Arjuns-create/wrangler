/*
 * Copyright © 2017-2019 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package io.cdap.wrangler.api.parser;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.cdap.wrangler.api.annotations.PublicEvolving;

/**
 * A {@link Token} implementation for representing byte sizes with units.
 */
@PublicEvolving
public class ByteSize implements Token {
  private final String originalValue;
  private final long bytes;

  public ByteSize(String value) {
    this.originalValue = value;
    this.bytes = parseBytes(value);
  }

  private long parseBytes(String value) {
    String numStr = value.replaceAll("[^0-9.]", "");
    String unit = value.replaceAll("[0-9.]", "").toUpperCase();
    double number = Double.parseDouble(numStr);

    switch (unit) {
      case "B":
        return (long) number;
      case "KB":
      case "K":
        return (long) (number * 1024);
      case "MB":
      case "M":
        return (long) (number * 1024 * 1024);
      case "GB":
      case "G":
        return (long) (number * 1024 * 1024 * 1024);
      case "TB":
      case "T":
        return (long) (number * 1024L * 1024L * 1024L * 1024L);
      case "PB":
      case "P":
        return (long) (number * 1024L * 1024L * 1024L * 1024L * 1024L);
      default:
        throw new IllegalArgumentException("Invalid byte size unit: " + unit);
    }
  }

  @Override
  public Object value() {
    return bytes;
  }

  @Override
  public TokenType type() {
    return TokenType.BYTE_SIZE;
  }

  @Override
  public JsonElement toJson() {
    JsonObject object = new JsonObject();
    object.addProperty("type", type().name());
    object.addProperty("value", originalValue);
    object.addProperty("bytes", bytes);
    return object;
  }

  /**
   * Gets the size in bytes.
   *
   * @return The size in bytes.
   */
  public long getBytes() {
    return bytes;
  }

  /**
   * Gets the original string value including the unit.
   *
   * @return The original string value.
   */
  public String getOriginalValue() {
    return originalValue;
  }
} 