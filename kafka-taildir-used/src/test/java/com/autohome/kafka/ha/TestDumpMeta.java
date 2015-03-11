package com.autohome.kafka.ha;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;

import com.autohome.kafka.core.upstream.TailFile;

import junit.framework.TestCase;

public class TestDumpMeta extends TestCase {
//	public DumpMeta dm = new DumpMeta();
	public Map a = new HashMap();
	public void testDumpMeta() throws ClassNotFoundException, IOException{
		IMocksControl control = EasyMock.createControl();
		TailFile tf = control.createMock(TailFile.class);
		a.put("abc.txt", new FileOffsetObj(123l));
		a.put("bcd.txt", new FileOffsetObj(1234l));
		tf.getFileMeta();
		 EasyMock.expectLastCall().andReturn(a);
		 control.replay();
		  DumpMeta dm = new DumpMeta(tf);
//		  dm.writeObject();
//		  dm.renamefile();
//		 Map map = dm.readObject();
//		 assertEquals(2, map.size());
//		 assertEquals(1234l, ((FileOffsetObj)map.get("bcd.txt")).getOffset());
	}
	
	protected void setUp() throws Exception {
		super.setUp();
	}
}
