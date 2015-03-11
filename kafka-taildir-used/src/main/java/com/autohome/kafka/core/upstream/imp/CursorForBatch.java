package com.autohome.kafka.core.upstream.imp;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

import kafka.producer.KeyedMessage;

import com.autohome.kafka.ha.FileOffsetObj;
/**
 * 读取文件类 通过nio进行读取
 * 
 * @author yaogang
 *
 */
public class CursorForBatch {
	  private static final Logger LOG = Logger.getLogger(CursorForBatch.class);
	  File file=null;
	  ByteBuffer buf = ByteBuffer.allocateDirect(Short.MAX_VALUE);
	  private FileOffsetObj foo = null;
	  RandomAccessFile raf = null;
	  final BlockingQueue<KeyedMessage<String,String>> sync;
	  private  volatile boolean madeProgress = false;
	  FileChannel in = null;
	  private String fullfilename;
	  private String topic;
	  long offset = 0l;
	  public CursorForBatch(String fullfilename,String topic,BlockingQueue<KeyedMessage<String,String>> sync,long offset){
		  this.sync = sync;
		  this.topic = topic;
		  this.offset = offset;
		  this.fullfilename = fullfilename;
		  this.file = new File(fullfilename);
		  this.foo = new FileOffsetObj();
		  foo.setFullfileName(this.fullfilename);
		  foo.setOffset(this.offset);
		  foo.setOn(true);
		  try {
			init(this.offset);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  }
	 
	  public void init() throws IOException{
		  init(0);
		  
	  }
	  public void init(long offset) throws IOException{
		  raf = new RandomAccessFile(file,"r");
		  LOG.info(this.fullfilename+" offset is "+offset);
		  this.raf.seek(offset);
		  in = raf.getChannel();
	  }
	  // 逐行解析读入的二进制文件
	private boolean extractLines(ByteBuffer buf) throws UnsupportedEncodingException, InterruptedException{
		   madeProgress = false;
		  int start = buf.position();
		  buf.mark();// 标记结果
		  while(buf.hasRemaining()){
		    	 byte b = buf.get();
		    	 if(b =='\n'||b=='~'){// ~符号是针对app数据的
//		    	 if(b =='\n'){// ~符号是针对app数据的
		    		 int end = buf.position();
		    		 int sz = end - start;
		    		 byte[] body = new byte[sz - 1];
		    	     buf.reset(); // go back to mark
		    	     buf.get(body, 0, sz - 1); // read data body is fill with data
		    	     buf.get(); // read '\n'
		    	     buf.mark(); // new mark.
		    	     start = buf.position(); //读取完这一行之后重新定位、n位置。
		    	     sync.put(new KeyedMessage<String, String>(topic,offset+"",new String(body,"utf-8")));
		    		 madeProgress = true;
		    	 }
		    }
//		  sync.put(e); batch function 
		    buf.reset();
		    buf.compact();
		    return madeProgress;
	  }
	  boolean readDataFromChannle() throws IOException, InterruptedException{
		int rd=0;
		boolean falge = false;
		if(buf.position()== buf.capacity()){// 当缓冲区数据没有 “\n” 或是 "~" 的情况下 造成无法读取数据，因此需要加入这样的考虑
			buf.clear();
		}
	    while ((rd = in.read(buf)) > 0) {
	    	offset+=rd;// 记录文件偏移量。
		 	buf.flip();
		 	falge = extractLines(buf);
		 	foo.setOffset(offset);
	    }
	    return falge;
	  }
	  /**
	   * @return offset file
	 * @throws InterruptedException 
	 * @throws IOException 
	   */
	  public boolean tailReadFile() throws IOException, InterruptedException{
		  
			  if(in==null){
				  LOG.error(" file read channel is not init");
			  }
			return readDataFromChannle();
		
		 
	  }
	  public void flush(){
		  if (raf != null) {
		      try {
		        raf.close(); // release handles
		      } catch (IOException e) {
		        LOG.error("problem closing file " + e.getMessage(), e);
		      }
		    } 
		  buf.flip(); // buffer consume mode
		  int remaining = buf.remaining();
		  if (remaining > 0) {
			   byte[] body = new byte[remaining];
			   buf.get(body, 0, remaining);
		  }
		  in = null;
		  buf.clear();
	  }
	  public long getOffset(){
		  return this.offset;
	  }
	
	  public String getFullfilename() {
		return fullfilename;
	}

	public void setFullfilename(String fullfilename) {
		this.fullfilename = fullfilename;
	}

	public void close() throws IOException{
		    if (raf != null) {
		        try {
		          raf.close(); // release handles
		        } catch (IOException e) {
		          LOG.error("problem closing file " + e.getMessage(), e);
		        }
		      }
		      offset = 0;
		      in = null;
		      buf.clear();
		    }

	public FileOffsetObj getFoo() {
		return foo;
	}
}
