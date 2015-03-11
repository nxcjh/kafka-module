package com.autohome.kafka.comm;

import kafka.producer.Partitioner;
import kafka.utils.VerifiableProperties;


public class SimplePartitioner implements Partitioner {
   public SimplePartitioner (VerifiableProperties props) {

   }

   public int partition(Object key, int a_numPartitions) {
        int  partition = key.hashCode() % a_numPartitions;
      return partition;
 }

}
