package uk.co.marcoratto.file.maketree;

import java.io.File;

public interface MakeTreeInterface {
	
   public void onFileFound(File file) throws MakeTreeException;
   
   public void onDirFound(File file) throws MakeTreeException;
}