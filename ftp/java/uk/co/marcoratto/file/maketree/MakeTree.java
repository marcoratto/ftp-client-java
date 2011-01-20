package uk.co.marcoratto.file.maketree;

import java.io.File;

public class MakeTree {

  public MakeTree(MakeTreeInterface obj) {
      this.target = obj;
  }

  public void searchDirectoryFile(File f) throws MakeTreeException{
	    this.searchFile(f, null);
  }

  public void searchFile(File f) throws MakeTreeException {
    this.searchFile(f, null);
  }

  public void searchFile(File f, String ext) throws MakeTreeException {
    try {
    if (f.isDirectory()) {
    	String[] list = f.list();
    	for(int i=0; i<list.length; i++) {
    		this.searchFile(new File(f, list[i]), ext);
    	}
    } else {
      if ((ext != null) && (f.getName().toLowerCase().endsWith(ext) == true)) {
        this.target.onFileFound(f);
      } else {
        this.target.onFileFound(f);
      }
    }
    } catch (Exception e) {
      throw new MakeTreeException(e);
    }
  }

  public void searchDirectoryFile(File f, String ext) throws MakeTreeException {
	    try {
	    if (f.isDirectory()) {
	    	this.target.onDirFound(f);
	    	String[] list = f.list();
	    	for(int i=0; i<list.length; i++) {
	    		this.searchDirectoryFile(new File(f, list[i]), ext);
	    	}
	    } else {
	      if ((ext != null) && (f.getName().toLowerCase().endsWith(ext) == true)) {
	        this.target.onFileFound(f);
	      } else {
	        this.target.onFileFound(f);
	      }
	    }
	    } catch (Exception e) {
	      throw new MakeTreeException(e);
	    }
  }
  
  public void searchDirectory(File f) throws MakeTreeException {
	    try {
	    if (f.isDirectory()) {
	    	this.target.onDirFound(f);
	    	String[] list = f.list();
	    	for(int i=0; i<list.length; i++) {
	    		this.searchDirectory(new File(f, list[i]));
	    	}
	    } 
	    } catch (Throwable t) {
	      throw new MakeTreeException(t);
	    }
	  }


  private MakeTreeInterface target = null;
}
