package com.autohome.kafka.instrumentation.utils;

import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.Set;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanServer;
import javax.management.ObjectInstance;

import org.apache.log4j.Logger;

import com.google.common.base.Throwables;
import com.google.common.collect.Maps;

public class JMXPollUtil {

	private static final Logger LOG = Logger.getLogger(JMXPollUtil.class);
	  private static MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();

	  /**
	   * 获取所有的mbean
	   * @return
	   */
	  public static Map<String, Map<String, String>> getAllMBeans() {
		  
	    Map<String, Map<String, String>> mbeanMap = Maps.newHashMap();	    
	    Set<ObjectInstance> queryMBeans = null;
	    
	    try {
	      queryMBeans = mbeanServer.queryMBeans(null, null);
	    } catch (Exception ex) {
	      LOG.error("Could not get Mbeans for monitoring", ex);
	      Throwables.propagate(ex);
	    }
	  
	    for (ObjectInstance obj : queryMBeans) {
	      try {
//	    	  LOG.info((obj.getObjectName().getDomain()+"====="+obj.getObjectName().toString()));
	        if (obj.getObjectName().getDomain().toString().startsWith("\"kafka.producer\"")) { 	
	         
	        MBeanAttributeInfo[] attrs = mbeanServer.getMBeanInfo(obj.getObjectName()).getAttributes();
	        String strAtts[] = new String[attrs.length];
	        for (int i = 0; i < strAtts.length; i++) {
	          strAtts[i] = attrs[i].getName();
	        }
	        AttributeList attrList = mbeanServer.getAttributes(obj.getObjectName(), strAtts);
	        String component = obj.getObjectName().toString().substring(obj.getObjectName().toString().indexOf('=') + 1);
	        Map<String, String> attrMap = Maps.newHashMap();


	        for (Object attr : attrList) {
	          Attribute localAttr = (Attribute) attr;
	          if(localAttr.getName().equalsIgnoreCase("type")){
	            component = localAttr.getValue()+ "." + component;
	          }
	          attrMap.put(localAttr.getName(), localAttr.getValue().toString());
	        }
	        mbeanMap.put(component, attrMap);
	        }
	      } catch (Exception e) {
	        LOG.error("Unable to poll JMX for metrics.", e);
	      }
	    }
	    return mbeanMap;
	  }
	}
