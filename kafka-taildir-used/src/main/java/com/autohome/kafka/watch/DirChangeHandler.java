package com.autohome.kafka.watch;

import java.io.File;

public interface DirChangeHandler {
	 /**
	   * File deleted or moved elsewhere.
	   */
	  void fileDeleted(File f);

	  /**
	   * File created/moved into this directory
	   */
	  void fileCreated(File f);
}
