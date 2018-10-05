package com.example.elcomplus.loaddata;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private CompositeDisposable compositeDisposable;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        compositeDisposable = new CompositeDisposable();
        setContentView(R.layout.activity_main);
        compositeDisposable.add(RetrofitClient.getClient().create(APIHelp.class).getData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(string -> {
                    Log.e(TAG, "onCreate: " + string);

                })
        );
    }


    static class RetrofitClient {
        private static Retrofit retrofit = null;
        private static String baseURL = "http://101.99.23.175:5566/api/vietlott/";

        public static Retrofit getClient() {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder().connectTimeout(5000, TimeUnit.SECONDS);
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClient.addInterceptor(chain -> {
                Request original = chain.request();

                Request request = original.newBuilder()
                        .header("Accept", "application/json")
                        .header("Content-Type", "application/json")
                        .header("service-api-key", "ESC-VIETLOTT-P2018")
                        .header("service-session-id", "8i55H19Bzoyb2000e4a2")
                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);
            });
            httpClient.addInterceptor(interceptor);
            OkHttpClient client = httpClient.build();
            return retrofit = new Retrofit.Builder()
                    .baseUrl(baseURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(client)
                    .build();
        }
    }

}
