package com.autohome.kafka.tools;

import java.io.File;
import java.util.regex.Pattern;

public class RegexFileFilter implements FileFilter {
	  Pattern p; // not thread safe

	  public RegexFileFilter(String regex) {
	    this.p = Pattern.compile(regex);
	  }

	  public boolean isSelected(File f) {
		
	    return p.matcher(f.getName()).matches();
	  }

	
}
