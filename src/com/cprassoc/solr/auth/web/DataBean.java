package com.cprassoc.solr.auth.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
A generalized bean to create a set of attributes, structures, objects or collections.

 */

public class DataBean extends Object
{
     
     public DataBean()
     {
          super();
     }
     
    public DataBean(String name,String value)
    {
         super();
         setValue("name",name);
         setValue("value",value);
    }
    /**
     *Copy the attributes from one the passed record into this record.
     * @param rec
     */
    public void copyFrom(DataBean rec){
            Iterator innerColumNameList = rec.getColumnNames();
                    
            if( innerColumNameList != null )
            {
                String tKey;
                while( innerColumNameList.hasNext() )
                {
                        tKey = innerColumNameList.next().toString();
                        setValue(tKey,rec.getString(tKey));
                }
            }
    }
    public void setIgnoreCase(boolean ignoreCase){
    	this.ignoreCase = ignoreCase;
    	
    	if( ignoreCase ){
    		if( internalValues != null ){
    			Iterator cols = internalValues.keySet().iterator();
    		
	            String tStr;
	            HashMap newInternalValues = new HashMap();
	            
	  			while( cols.hasNext() )
	  			{
	  				tStr = cols.next().toString();
	  				newInternalValues.put(tStr.toLowerCase(), internalValues.get(tStr));
	  			}
	  			
	  			internalValues = newInternalValues;
    		}
    	}
    }
    
    public boolean getIgnoreCase(){
    	return( ignoreCase );
    }
     private boolean ignoreCase = false;
     
     public DataBean copyDataBean(boolean copyIgnoreCase){
          DataBean result = new DataBean();
          result.setIgnoreCase(copyIgnoreCase);
          
          Iterator cols = getColumnNames();

	       if( cols != null )
	       {
	           String tStr;
               while( cols.hasNext() )
               {
                   tStr = cols.next().toString();
                   result.setValue(tStr,getValue(tStr));
               }
	       }
	       
	       result.setStructures(getStructures());
	       result.setObjects(getObjects());
	       result.setCollections(getCollections());
	         
         
         return( result );
     }
     
     private HashMap structures;
     
     public void setStructures(HashMap structures){
    	 this.structures = structures;
     }

     public void setStructure(String name,DataBean value)
     {
        if( structures == null )
               structures = new HashMap();

        structures.put(ignoreCase ? name.toLowerCase() : name,value);
     }

     public boolean hasStructure(String name){
    	 return( structures != null && structures.get(ignoreCase ? name.toLowerCase() : name) != null );
     }
     
     public DataBean getStructure(String name)
     {
         return( structures != null && structures.get(ignoreCase ? name.toLowerCase() : name) != null ? (DataBean)structures.get(ignoreCase ? name.toLowerCase() : name) : new DataBean() );
     }
     
    private HashMap objects;
    
    public HashMap getObjects()
    {
         return( objects );
    }
    
    public void setObjects(HashMap objects){
    	this.objects = objects;
    }

    public void setObject(String name,Object value)
    {
       if( objects == null )
              objects = new HashMap();

       objects.put(ignoreCase ? name.toLowerCase() : name,value);
    }

    public Object getObject(String name)
    {
        return( objects != null && objects.get(ignoreCase ? name.toLowerCase() : name) != null ? (Object)objects.get(ignoreCase ? name.toLowerCase() : name) : null );
    }

     private HashMap collections;
     public void setCollections(HashMap collections){
    	 this.collections = collections;
     }
     
     public boolean hasCollection(String name){
    	 return( collections != null && collections.get(ignoreCase ? name.toLowerCase() : name) != null );
     }

     public void setCollection(String name,ArrayList value)
     {
        if( collections == null )
               collections = new HashMap();

        collections.put(ignoreCase ? name.toLowerCase() : name,value);
     }

     public ArrayList getCollection(String name)
     {
         return( collections != null && collections.get(ignoreCase ? name.toLowerCase() : name) != null ? (ArrayList)collections.get(ignoreCase ? name.toLowerCase() : name) : null );
     }
     
     public void addToCollection(String name,Object value)
     {
          ArrayList entryList = getCollection(name);
          
          if( entryList == null )
          {
               setCollection(name,new ArrayList());
               entryList = getCollection(name);
          }

          entryList.add(value);
     }
     public HashMap getCollections()
     {
          return( collections );
     }
     
     public ArrayList resetCollection(String name){
         setCollection(name,new ArrayList());
         return( getCollection(name) );
     }
     
    public DataBean resetStructure(String name){
        setStructure(name,new DataBean());
        return( getStructure(name) );
    }
    
     public void removeCollection(String name){
         if( collections != null ){
             collections.remove(ignoreCase ? name.toLowerCase() : name);
         }
     }
     
    public void removeStructure(String name){
        if( structures != null ){
            structures.remove(ignoreCase ? name.toLowerCase() : name);
        }
    }
     
     public HashMap getStructures()
     {
          return( structures );
     }

     public Iterator getColumnNames()
     {
          Iterator result = null;
          
          if( internalValues != null )
               result = internalValues.keySet().iterator();
          
          return( result );
     }
     
     private HashMap internalValues;

     public void setValue(String name,Object value)
     {
        if( internalValues == null )
               internalValues = new HashMap();

		if( value != null )
			internalValues.put(ignoreCase ? name.toLowerCase() : name,value);
     }
     
    public void append(String name,String value){
        setValue(name,getString(name).length() > 0 ? getString(name) + " " + value : value);
    }
    
    public void append(String name,String value,boolean smart){
        if( smart ){
            if( getString(name).indexOf(value) < 0 )
                append(name,value);
        }
        else
            append(name,value);
    }
    public void removeValue(String name){
        if( internalValues != null ){
            internalValues.remove(ignoreCase ? name.toLowerCase() : name);
        }
    }
    public void setValue(String name,int value)
     {
        setValue(name,String.valueOf(value));
     }
        
     public String getString(String name)
     {
         return( internalValues != null && internalValues.get(ignoreCase ? name.toLowerCase() : name) != null ? internalValues.get(ignoreCase ? name.toLowerCase() : name).toString() : "" );
     }
	 
	public String getString(String name,String defaultValue )
	{
		return( internalValues != null && internalValues.get(ignoreCase ? name.toLowerCase() : name) != null ? internalValues.get(ignoreCase ? name.toLowerCase() : name).toString() : defaultValue );
	}

     public float getFloat(String name)
     {
         return( internalValues != null && internalValues.get(ignoreCase ? name.toLowerCase() : name) != null ? Float.valueOf(internalValues.get(ignoreCase ? name.toLowerCase() : name).toString()).floatValue() : 0 );
     }

     public int getInt(String name)
     {
         return( internalValues != null && internalValues.get(ignoreCase ? name.toLowerCase() : name) != null ? Helper.parseInt(internalValues.get(ignoreCase ? name.toLowerCase() : name).toString()) : 0 );
     }
     
     public short getShort(String name)
     {
         return( internalValues != null && internalValues.get(ignoreCase ? name.toLowerCase() : name) != null ? Helper.parseShort(internalValues.get(ignoreCase ? name.toLowerCase() : name).toString()) : 0 );
     }
     
     public boolean getBoolean(String name){
         return( internalValues != null && internalValues.get(ignoreCase ? name.toLowerCase() : name) != null ? Boolean.valueOf(internalValues.get(ignoreCase ? name.toLowerCase() : name).toString()) : false );
     }

     public Object getValue(String name)
     {
         return( internalValues != null && internalValues.get(ignoreCase ? name.toLowerCase() : name) != null ? internalValues.get(ignoreCase ? name.toLowerCase() : name) : null );
     }

     public boolean isValid(String name)
     {
         return( internalValues != null && internalValues.get(ignoreCase ? name.toLowerCase() : name) != null ? internalValues.get(ignoreCase ? name.toLowerCase() : name).toString().length() > 0 : false );
     }

     public boolean isValid()
     {
         return( true );
     }
     public void reset()
     {
          internalValues = new HashMap();
          collections = new HashMap();
     }

	public HashMap getValues()
	{
		return( internalValues );
	}
	
     public String toString()
     {
          StringBuffer result = new StringBuffer();
          
          Iterator cols = getColumnNames();
          
          String tStr;
          
		if( cols != null )
		{
			while( cols.hasNext() )
			{
				tStr = cols.next().toString();
				
				result.append(tStr);
				result.append(":");
				result.append(getString(tStr));
				result.append( " ; ");
			}
		}
          
          HashMap cHash = getCollections();
          
          if( cHash != null )
          {
               cols = cHash.keySet().iterator();
               ArrayList subList;

               while( cols.hasNext() )
               {
                    tStr = cols.next().toString();
                    result.append(tStr);
                    result.append(" collection:");
                    
                    subList = getCollection(tStr);
                    
                   if( subList != null ){
                        for(int j = 0;j < subList.size();j++)
                        {
           
                             result.append(subList.get(j).toString());
                             
                        }
                   }
               }
          }
          
          cHash = getStructures();
          
          if( cHash != null )
          {
               cols = cHash.keySet().iterator();

               while( cols.hasNext() )
               {
                    tStr = cols.next().toString();
                    result.append(tStr);
                    result.append(" structure:");
                    
                    DataBean structure = getStructure(tStr);
                    
                   if( structure != null ){
                        result.append(structure.toString());
                   }
               }
          }
          
          return( result.toString() );
     }

 	/**
 	 * @param name
 	 * @param t
 	 */
 	public void addCollection(String name, DataBean t) {

 		ArrayList entryList = getCollection(name);
        
        if( entryList == null )
        {
             setCollection(name,new ArrayList<>());
             entryList = getCollection(name);
        }

        entryList.add(t);
		
	}
	/**
	 * @param name
	 * @param str
	 */
	public void addCollection(String name, String str) {

        ArrayList entryList = getCollection(name);
        
        if( entryList == null )
        {
             setCollection(name,new ArrayList<>());
             entryList = getCollection(name);
        }

        entryList.add(str);
		
	}
	public void addCollection(String name, Object t) {
 		ArrayList entryList = getCollection(name);
        
        if( entryList == null )
        {
             setCollection(name,new ArrayList<>());
             entryList = getCollection(name);
        }

        entryList.add(t);		
	}
	
}
/*end DataStructure*/
