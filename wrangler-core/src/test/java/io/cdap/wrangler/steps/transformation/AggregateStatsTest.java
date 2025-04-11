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

package io.cdap.wrangler.steps.transformation;

import io.cdap.wrangler.api.Row;
import io.cdap.wrangler.api.parser.ByteSize;
import io.cdap.wrangler.api.parser.TimeDuration;
import io.cdap.wrangler.test.TestingRig;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Tests for {@link AggregateStats}
 */
public class AggregateStatsTest {

  @Test
  public void testBasicAggregation() throws Exception {
    String[] directives = new String[] {
      "aggregate-stats :size :time total_size total_time"
    };

    List<Row> rows = Arrays.asList(
      createRow("size", new ByteSize("1KB"), "time", new TimeDuration("1s")),
      createRow("size", new ByteSize("2MB"), "time", new TimeDuration("500ms")),
      createRow("size", new ByteSize("3GB"), "time", new TimeDuration("2m"))
    );

    List<Row> results = TestingRig.execute(directives, rows);

    Assert.assertEquals(1, results.size());
    Row result = results.get(0);
    
    // Expected: 1KB + 2MB + 3GB = ~3.002GB (in MB)
    // 1KB = 1024 bytes
    // 2MB = 2097152 bytes
    // 3GB = 3221225472 bytes
    // Total = 3223323648 bytes = 3074.0234375 MB
    Assert.assertEquals(3074.0234375, result.getValue("total_size"), 0.0001);
    
    // Expected: 1s + 500ms + 2m = 121.5s
    Assert.assertEquals(121.5, result.getValue("total_time"), 0.0001);
  }

  @Test
  public void testCustomOutputUnits() throws Exception {
    String[] directives = new String[] {
      "aggregate-stats :size :time total_size total_time GB h"
    };

    List<Row> rows = Arrays.asList(
      createRow("size", new ByteSize("1024MB"), "time", new TimeDuration("3600s")),
      createRow("size", new ByteSize("2048MB"), "time", new TimeDuration("7200s"))
    );

    List<Row> results = TestingRig.execute(directives, rows);

    Assert.assertEquals(1, results.size());
    Row result = results.get(0);
    
    // Expected: 1024MB + 2048MB = 3GB
    Assert.assertEquals(3.0, result.getValue("total_size"), 0.0001);
    
    // Expected: 3600s + 7200s = 10800s = 3h
    Assert.assertEquals(3.0, result.getValue("total_time"), 0.0001);
  }

  @Test(expected = Exception.class)
  public void testInvalidInput() throws Exception {
    String[] directives = new String[] {
      "aggregate-stats :size :time total_size total_time"
    };

    List<Row> rows = Arrays.asList(
      createRow("size", "not a byte size", "time", new TimeDuration("1s"))
    );

    TestingRig.execute(directives, rows);
  }

  private Row createRow(Object... values) {
    Row row = new Row();
    for (int i = 0; i < values.length; i += 2) {
      row.add(values[i].toString(), values[i + 1]);
    }
    return row;
  }
} 