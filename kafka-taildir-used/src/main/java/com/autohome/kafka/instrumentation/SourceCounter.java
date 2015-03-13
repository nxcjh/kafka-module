/*
 * Licensed to the Apache Software Foundation (ASF) under one
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

/**
 * SourceCounter继承了MonitoredCounterGroup, 主要统计项如下
 */
public class SourceCounter extends MonitoredCounterGroup implements SourceCounterMBean {

    //表示source接收的event个数
  private static final String COUNTER_EVENTS_RECEIVED ="src.events.received";
    //表示source处理成功的event个数, 和上面的区别就是上面虽然接收了可能没有处理成功
  private static final String COUNTER_EVENTS_ACCEPTED ="src.events.accepted";
    //表示append次数, 在averosource和thriftsource中调用
  private static final String COUNTER_APPEND_RECEIVED = "src.append.received";
    //表示append处理成功次数
  private static final String COUNTER_APPEND_ACCEPTED = "src.append.accepted";
    //表示appendBatch被调用的次数, 在avrosource和thriftsource中调用
  private static final String COUNTER_APPEND_BATCH_RECEIVED ="src.append-batch.received";
    //表示appendBatch处理成功次数
  private static final String COUNTER_APPEND_BATCH_ACCEPTED ="src.append-batch.accepted";
    //用在avrosource中表示打开连接的数量
  private static final String COUNTER_OPEN_CONNECTION_COUNT = "src.open-connection.count";


  private static final String[] ATTRIBUTES =
    {
      COUNTER_EVENTS_RECEIVED, COUNTER_EVENTS_ACCEPTED,
      COUNTER_APPEND_RECEIVED, COUNTER_APPEND_ACCEPTED,
      COUNTER_APPEND_BATCH_RECEIVED, COUNTER_APPEND_BATCH_ACCEPTED,
      COUNTER_OPEN_CONNECTION_COUNT
    };


  public SourceCounter(String name) {
    super(MonitoredCounterGroup.Type.SOURCE, name, ATTRIBUTES);
  }

 
  public long getEventReceivedCount() {
    return get(COUNTER_EVENTS_RECEIVED);
  }

  public long incrementEventReceivedCount() {
    return increment(COUNTER_EVENTS_RECEIVED);
  }

  public long addToEventReceivedCount(long delta) {
    return addAndGet(COUNTER_EVENTS_RECEIVED, delta);
  }

 
  public long getEventAcceptedCount() {
    return get(COUNTER_EVENTS_ACCEPTED);
  }

  public long incrementEventAcceptedCount() {
    return increment(COUNTER_EVENTS_ACCEPTED);
  }

  public long addToEventAcceptedCount(long delta) {
    return addAndGet(COUNTER_EVENTS_ACCEPTED, delta);
  }

 
  public long getAppendReceivedCount() {
    return get(COUNTER_APPEND_RECEIVED);
  }

  public long incrementAppendReceivedCount() {
    return increment(COUNTER_APPEND_RECEIVED);
  }

 
  public long getAppendAcceptedCount() {
    return get(COUNTER_APPEND_ACCEPTED);
  }

  public long incrementAppendAcceptedCount() {
    return increment(COUNTER_APPEND_ACCEPTED);
  }

 
  public long getAppendBatchReceivedCount() {
    return get(COUNTER_APPEND_BATCH_RECEIVED);
  }

  public long incrementAppendBatchReceivedCount() {
    return increment(COUNTER_APPEND_BATCH_RECEIVED);
  }

 
  public long getAppendBatchAcceptedCount() {
    return get(COUNTER_APPEND_BATCH_ACCEPTED);
  }

  public long incrementAppendBatchAcceptedCount() {
    return increment(COUNTER_APPEND_BATCH_ACCEPTED);
  }

  public long getOpenConnectionCount() {
    return get(COUNTER_OPEN_CONNECTION_COUNT);
  }

  public void setOpenConnectionCount(long openConnectionCount){
    set(COUNTER_OPEN_CONNECTION_COUNT, openConnectionCount);
  }
}
