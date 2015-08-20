package com.snowcat.fajnyobed.io;

import org.json.JSONObject;

/**
 * Created by AntikAdmin on 25. 9. 2014.
 */
public interface IDataClient {

    public Retriever createRetriever();

    public static interface Retriever {
        public String execute(JSONObject request) throws Exception;

        public JSONObject executeJSON(JSONObject request);

        public void cancel();
    }
}
