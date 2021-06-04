package tabs;

import java.util.ArrayList;

public interface RefreshDB {

	ArrayList<RefreshDB> list = new ArrayList<>() {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public boolean add (RefreshDB l) {
			
			// dane ne se updatevat po dva puti
			if(this.contains(l)) {
				return false;
			}			
		    return super.add(l);
		}		
	};

    abstract void onUpdate();

    default void register() {
	    
    	if(!list.add(this)) {
	       	System.out.println("Not added!");
	    }
    }
    
    default void performUpdate(RefreshDB l) {
    	    	
    	if(list.contains(l))
    	   	for(var v : list) {
    		    v.onUpdate();    		
    	    }    
   }    
}



