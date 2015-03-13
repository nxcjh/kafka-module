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

import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Used for keeping track of internal metrics using atomic integers</p>
 *
 * This is used by a variety of component types such as Sources, Channels,
 * Sinks, SinkProcessors, ChannelProcessors, Interceptors and Serializers.
 *
 * 这个类是用来跟踪内部的指标的, 注册组件的MBean并跟踪和更新统计值. 需要监控
 * 的组件都要继承这个类,这个类可以跟踪flume内部所有组件, 但是目前只实现了3个.
 */
public abstract class MonitoredCounterGroup {

  private static final Logger logger = LoggerFactory.getLogger(MonitoredCounterGroup.class);

  // Key for component's start time in MonitoredCounterGroup.counterMap
  private static final String COUNTER_GROUP_START_TIME = "start.time";

  // key for component's stop time in MonitoredCounterGroup.counterMap
  private static final String COUNTER_GROUP_STOP_TIME = "stop.time";

  private final Type type;
  private final String name;
  private final Map<String, AtomicLong> counterMap;

  private AtomicLong startTime;
  private AtomicLong stopTime;
  private volatile boolean registered = false;


    /**
     * 构造方法, 主要是设置组件的类型, 名称,
     * 然后将所有的atters(这个是设定的各个统计项)加入Map<String,AtomicLog> counterMap.
     * 值设定为0; 然后初始化计数器的开始时间和结束时间,都设为0;
     *
     * @param type
     * @param name
     * @param attrs
     */
  protected MonitoredCounterGroup(Type type, String name, String... attrs) {
    this.type = type;
    this.name = name;

    Map<String, AtomicLong> counterInitMap = new HashMap<String, AtomicLong>();

    // Initialize the counters
    for (String attribute : attrs) {
      counterInitMap.put(attribute, new AtomicLong(0L));
    }

    counterMap = Collections.unmodifiableMap(counterInitMap);

    startTime = new AtomicLong(0L);
    stopTime = new AtomicLong(0L);
  }

  /**
   * Starts the component
   *
   * Initializes the values for the stop time as well as all the keys in the
   * internal map to zero and sets the start time to the current time in
   * milliseconds since midnight January 1, 1970 UTC
   *
   * 会先注册jishux计数器, 然后对所有统计项的统计值设为0; 将开始时间设为当前时间
   * */
  public void start() {

    register();
    stopTime.set(0L);
    for (String counter : counterMap.keySet()) {
      counterMap.get(counter).set(0L);
    }
    startTime.set(System.currentTimeMillis());
    logger.info("Component type: " + type + ", name: " + name + " started");
  }

  /**
   * Registers the counter.
   * This method is exposed only for testing, and there should be no need for
   * any implementations to call this method directly.
   *
   * 如果这个计数器还未注册, 将这个计数器的MBean进行注册, 就可以进行跟踪了
   */
  @VisibleForTesting
  void register() {
    if (!registered) {
      try {
        ObjectName objName = new ObjectName("org.apache.flume." + type.name().toLowerCase(Locale.ENGLISH) + ":type=" + this.name);

        if (ManagementFactory.getPlatformMBeanServer().isRegistered(objName)) {
          logger.debug("Monitored counter group for type: " + type + ", name: "
              + name + ": Another MBean is already registered with this name. "
              + "Unregistering that pre-existing MBean now...");
          ManagementFactory.getPlatformMBeanServer().unregisterMBean(objName);
          logger.debug("Monitored counter group for type: " + type + ", name: "
              + name + ": Successfully unregistered pre-existing MBean.");
        }
        ManagementFactory.getPlatformMBeanServer().registerMBean(this, objName);
        logger.info("Monitored counter group for type: " + type + ", name: "
            + name + ": Successfully registered new MBean.");
        registered = true;
      } catch (Exception ex) {
        logger.error("Failed to register monitored counter group for type: "
                + type + ", name: " + name, ex);
      }
    }
  }

  /**
   * Shuts Down the Component
   *
   * Used to indicate that the component is shutting down.
   *
   * Sets the stop time and then prints out the metrics from
   * the internal map of keys to values for the following components:
   *
   * - ChannelCounter
   * - ChannelProcessorCounter
   * - SinkCounter
   * - SinkProcessorCounter
   * - SourceCounter
   *
   * 会设置结束时间为当前时间, 输出各个统计项的信息,
   * 我们用Ctrl+c 结束进程时, 最后显示的统计信息就是来自这里
   * 其他方法都是获取counterMap中的信息或者更新等, 比较简单.
   */
  public void stop() {

    // Sets the stopTime for the component as the current time in milliseconds
    stopTime.set(System.currentTimeMillis());

    // Prints out a message indicating that this component has been stopped
    logger.info("Component type: " + type + ", name: " + name + " stopped");

    // Retrieve the type for this counter group
    final String typePrefix = type.name().toLowerCase(Locale.ENGLISH);

    // Print out the startTime for this component
    logger.info("Shutdown Metric for type: " + type + ", "
      + "name: " + name + ". "
      + typePrefix + "." + COUNTER_GROUP_START_TIME
      + " == " + startTime);

    // Print out the stopTime for this component
    logger.info("Shutdown Metric for type: " + type + ", "
      + "name: " + name + ". "
      + typePrefix + "." + COUNTER_GROUP_STOP_TIME
      + " == " + stopTime);

    // Retrieve and sort counter group map keys
    final List<String> mapKeys = new ArrayList<String>(counterMap.keySet());

    Collections.sort(mapKeys);

    // Cycle through and print out all the key value pairs in counterMap
    for (final String counterMapKey : mapKeys) {

      // Retrieves the value from the original counterMap.
      final long counterMapValue = get(counterMapKey);

      logger.info("Shutdown Metric for type: " + type + ", "
        + "name: " + name + ". "
        + counterMapKey + " == " + counterMapValue);
    }
  }

  /**
   * Returns when this component was first started
   *
   * @return
   */
  public long getStartTime() {
    return startTime.get();
  }

  /**
   * Returns when this component was stopped
   *
   * @return
   */
  public long getStopTime() {
    return stopTime.get();
  }

  @Override
  public final String toString() {
    StringBuilder sb = new StringBuilder(type.name()).append(":");
    sb.append(name).append("{");
    boolean first = true;
    Iterator<String> counterIterator = counterMap.keySet().iterator();
    while (counterIterator.hasNext()) {
      if (first) {
        first = false;
      } else {
        sb.append(", ");
      }
      String counterName = counterIterator.next();
      sb.append(counterName).append("=").append(get(counterName));
    }
    sb.append("}");

    return sb.toString();
  }


  /**
   * Retrieves the current value for this key
   *
   * @param counter The key for this metric
   * @return The current value for this key
   */
  protected long get(String counter) {
    return counterMap.get(counter).get();
  }

  /**
   * Sets the value for this key to the given value
   *
   * @param counter The key for this metric
   * @param value The new value for this key
   */
  protected void set(String counter, long value) {
    counterMap.get(counter).set(value);
  }

  /**
   * Atomically adds the delta to the current value for this key
   *
   * @param counter The key for this metric
   * @param delta
   * @return The updated value for this key
   */
  protected long addAndGet(String counter, long delta) {
    return counterMap.get(counter).addAndGet(delta);
  }

  /**
   * Atomically increments the current value for this key by one
   *
   * @param counter The key for this metric
   * @return The updated value for this key
   */
  protected long increment(String counter) {
    return counterMap.get(counter).incrementAndGet();
  }

  /**
   * Component Enum Constants
   *
   * Used by each component's constructor to distinguish which type the
   * component is.
   */
  public static enum Type {
    SOURCE,
    CHANNEL_PROCESSOR,
    CHANNEL,
    SINK_PROCESSOR,
    SINK,
    INTERCEPTOR,
    SERIALIZER,
    OTHER
  };

  public String getType(){
    return type.name();
  }
}
