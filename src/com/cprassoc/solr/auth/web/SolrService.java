package com.cprassoc.solr.auth.web;


/**
 *
 *
 * @author admin
 *
 */
public class SolrService  {
   
    public SolrService() {
        //placeHolder
    }
    
    private String contentType = "CONTENT";

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentType() {
        return( contentType );
    }

    public DataBean getData(DataBean args) {
        DataBean result = new DataBean();
        return (result);
    }

    public DataBean postData(DataBean args) {
        DataBean result = new DataBean();
        return (result);
    }

    public DataBean putData(DataBean args) {
        DataBean result = new DataBean();
        return (result);
    }

    public DataBean deleteData(DataBean args) {
        DataBean result = new DataBean();
        return (result);
    }

    public static void main(String[] args) {
        SolrService gs = new SolrService();
        DataBean foo = gs.getData(new DataBean());
        System.out.println(foo);
    }
}
