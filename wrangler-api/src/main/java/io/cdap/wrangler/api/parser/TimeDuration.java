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
 * A {@link Token} implementation for representing time durations with units.
 */
@PublicEvolving
public class TimeDuration implements Token {
  private final String originalValue;
  private final long milliseconds;

  public TimeDuration(String value) {
    this.originalValue = value;
    this.milliseconds = parseMilliseconds(value);
  }

  private long parseMilliseconds(String value) {
    String numStr = value.replaceAll("[^0-9.]", "");
    String unit = value.replaceAll("[0-9.]", "").toLowerCase();
    double number = Double.parseDouble(numStr);

    switch (unit) {
      case "ms":
        return (long) number;
      case "s":
        return (long) (number * 1000);
      case "m":
        return (long) (number * 60 * 1000);
      case "h":
        return (long) (number * 60 * 60 * 1000);
      case "d":
        return (long) (number * 24 * 60 * 60 * 1000);
      default:
        throw new IllegalArgumentException("Invalid time duration unit: " + unit);
    }
  }

  @Override
  public Object value() {
    return milliseconds;
  }

  @Override
  public TokenType type() {
    return TokenType.TIME_DURATION;
  }

  @Override
  public JsonElement toJson() {
    JsonObject object = new JsonObject();
    object.addProperty("type", type().name());
    object.addProperty("value", originalValue);
    object.addProperty("milliseconds", milliseconds);
    return object;
  }

  /**
   * Gets the duration in milliseconds.
   *
   * @return The duration in milliseconds.
   */
  public long getMilliseconds() {
    return milliseconds;
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