package com.autohome.kafka.tools;

import java.io.File;

public interface FileFilter {
	/**
	   * True if file means specified criteria
	   */
	  boolean isSelected(File f);
}
