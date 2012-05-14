package uk.co.marcoratto.file.maketree;

public class MakeTreeException extends Exception {

   /**
	 * 
	 */
	private static final long serialVersionUID = -8963227593079453L;

public MakeTreeException(Exception e) {
      super(e.toString());
   }

   public MakeTreeException(Throwable t) {
	    super(t.toString());
   }

   public MakeTreeException(String s) {
      super(s);
   }

   public MakeTreeException() {
      super();
   }
}