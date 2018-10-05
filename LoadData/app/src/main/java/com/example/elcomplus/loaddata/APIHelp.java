package com.example.elcomplus.loaddata;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface APIHelp {
    @GET("agent/history/get_orders?agent_id=1&from_date=01/09/2018&to_date=02/10/2018&category=-1&status=-1&order_mask=&msisdn=&from_money=-1&to_money=-1&page_index=1&page_size=99999&order=")
    Observable<String> getData();
}
