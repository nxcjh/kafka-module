/*
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.autohome.kafka.instrumentation;

public class SinkCounter extends MonitoredCounterGroup implements SinkCounterMBean {

  //这个调用的地方颇多, 都表示"连接"创建的数量, 比如与HBase建立连接, 与avrosource建立链接以及文件的打开等
  private static final String COUNTER_CONNECTION_CREATED ="sink.connection.creation.count";
  //对象上面的stop操作, destroyConnection, close文件操作等.
  private static final String COUNTER_CONNECTION_CLOSED ="sink.connection.closed.count";
    //表示上面所表示"连接"时异常, 失败的次数
  private static final String COUNTER_CONNECTION_FAILED ="sink.connection.failed.count";
    //表示这个批次处理的event数量为0的情况
  private static final String COUNTER_BATCH_EMPTY = "sink.batch.empty";
    //表示这个批次处理的event的数量介于0和配置的batchSize之间
  private static final String COUNTER_BATCH_UNDERFLOW ="sink.batch.underflow";
    //表示这个批次处理的event数量等于设定的batchSize
  private static final String COUNTER_BATCH_COMPLETE ="sink.batch.complete";
    //准备处理的event的个数
  private static final String COUNTER_EVENT_DRAIN_ATTEMPT = "sink.event.drain.attempt";
    //这个表示处理成功的event数量, 与上面不同的是上面的是还未处理的.
  private static final String COUNTER_EVENT_DRAIN_SUCCESS = "sink.event.drain.sucess";

  private static final String[] ATTRIBUTES = {
    COUNTER_CONNECTION_CREATED, COUNTER_CONNECTION_CLOSED,
    COUNTER_CONNECTION_FAILED, COUNTER_BATCH_EMPTY,
    COUNTER_BATCH_UNDERFLOW, COUNTER_BATCH_COMPLETE,
    COUNTER_EVENT_DRAIN_ATTEMPT, COUNTER_EVENT_DRAIN_SUCCESS
  };


  public SinkCounter(String name) {
    super(MonitoredCounterGroup.Type.SINK, name, ATTRIBUTES);
  }

  
  public long getConnectionCreatedCount() {
    return get(COUNTER_CONNECTION_CREATED);
  }

  public long incrementConnectionCreatedCount() {
    return increment(COUNTER_CONNECTION_CREATED);
  }

  
  public long getConnectionClosedCount() {
    return get(COUNTER_CONNECTION_CLOSED);
  }

  public long incrementConnectionClosedCount() {
    return increment(COUNTER_CONNECTION_CLOSED);
  }

  
  public long getConnectionFailedCount() {
    return get(COUNTER_CONNECTION_FAILED);
  }

  public long incrementConnectionFailedCount() {
    return increment(COUNTER_CONNECTION_FAILED);
  }

  
  public long getBatchEmptyCount() {
    return get(COUNTER_BATCH_EMPTY);
  }

  public long incrementBatchEmptyCount() {
    return increment(COUNTER_BATCH_EMPTY);
  }

  
  public long getBatchUnderflowCount() {
    return get(COUNTER_BATCH_UNDERFLOW);
  }

  public long incrementBatchUnderflowCount() {
    return increment(COUNTER_BATCH_UNDERFLOW);
  }

  
  public long getBatchCompleteCount() {
    return get(COUNTER_BATCH_COMPLETE);
  }

  public long incrementBatchCompleteCount() {
    return increment(COUNTER_BATCH_COMPLETE);
  }

  
  public long getEventDrainAttemptCount() {
    return get(COUNTER_EVENT_DRAIN_ATTEMPT);
  }

  public long incrementEventDrainAttemptCount() {
    return increment(COUNTER_EVENT_DRAIN_ATTEMPT);
  }

  public long addToEventDrainAttemptCount(long delta) {
    return addAndGet(COUNTER_EVENT_DRAIN_ATTEMPT, delta);
  }

  
  public long getEventDrainSuccessCount() {
    return get(COUNTER_EVENT_DRAIN_SUCCESS);
  }

  public long incrementEventDrainSuccessCount() {
    return increment(COUNTER_EVENT_DRAIN_SUCCESS);
  }

  public long addToEventDrainSuccessCount(long delta) {
    return addAndGet(COUNTER_EVENT_DRAIN_SUCCESS, delta);
  }
}
