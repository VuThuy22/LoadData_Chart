package com.example.elcomplus.loaddata;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.elcomplus.loaddata.common.Cts;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoadVolleyActivity extends AppCompatActivity {
    private static final String TAG = "LoadVolleyActivity";
    @BindView(R.id.txt_start)
    TextView txtStart;
    @BindView(R.id.txt_end)
    TextView txtEnd;
    @BindView(R.id.choose_rule)
    Spinner chooseRule;
    @BindView(R.id.barchart)
    BarChart barchart;
    private int mYear, mMonth, mDay;
    private String url;
    public static final String baseURL = "http://101.99.23.175:5566/api/vietlott/";
    private static final String[] paths = {Cts.TOTAL_MONEY, Cts.ACTION_TIME, Cts.ORDER_TIME};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_volley);
        ButterKnife.bind(this);
        Init();

    }

    private void Init() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, paths);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chooseRule.setAdapter(adapter);
        txtStart.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            mYear = calendar.get(Calendar.YEAR);
            mMonth = calendar.get(Calendar.MONTH);
            mDay = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    txtStart.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                }
            }, mYear, mMonth, mDay);
            datePickerDialog.show();

        });
        txtEnd.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            mYear = calendar.get(Calendar.YEAR);
            mMonth = calendar.get(Calendar.MONTH);
            mDay = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    txtEnd.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    url = getUrl(convertTime(txtStart.getText().toString()), convertTime(txtEnd.getText().toString()));
                    getRequest(url, Cts.TOTAL_MONEY);
                    chooseRule.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            switch (position) {
                                case 0: {
                                    getRequest(url, Cts.TOTAL_MONEY);
                                    break;
                                }

                                case 1: {
                                    getRequest(url, Cts.ACTION_TIME);
                                    break;
                                }

                                case 2: {
                                    getRequest(url, Cts.ORDER_TIME);
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }
            }, mYear, mMonth, mDay);
            datePickerDialog.show();
        });
    }


    private String getUrl(String time_start, String time_end) {
        return baseURL + "agent/history/get_orders?agent_id=1&from_date="
                + time_start
                + "&to_date="
                + time_end
                + "&category=-1&status=-1&order_mask=&msisdn=&from_money=-1&to_money=-1&page_index=1&page_size=99999&order=";
    }


    public void getRequest(String url, String rule) {
        int[] arr_content = new int[24];
        Arrays.fill(arr_content, 0);
        List<Integer> list = new ArrayList<>();
        JsonObjectRequest postJson = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.e(TAG, "onResponse1: " + response.toString());
                        try {
                            JSONArray paperTicketOrderInfos = response.getJSONArray("paperTicketOrderInfos");
                            for (int i = 0; i < paperTicketOrderInfos.length(); i++) {
                                JSONObject item = paperTicketOrderInfos.getJSONObject(i);
                                int totalMoney = item.getInt("totalMoney");
                                String actionTime = item.getString("actionTime");
                                String orderTime = item.getString("orderTime");
                                Long time1 = convertToHour(orderTime);
                                Long time2 = convertToHour(actionTime);
                                switch (rule) {
                                    case Cts.ORDER_TIME: {
                                        for (int j = 0; j < 24; j++) {
                                            if (time1 == j)
                                                arr_content[j] = arr_content[j] + 1;
                                        }
                                        break;
                                    }
                                    case Cts.TOTAL_MONEY: {
                                        for (int j = 0; j < 24; j++) {
                                            if (time2 == j)
                                                arr_content[j] = arr_content[j] + totalMoney;
                                        }
                                        break;
                                    }

                                    case Cts.ACTION_TIME: {
                                        for (int j = 0; j < 24; j++) {
                                            if (time2 == j)
                                                arr_content[j] = arr_content[j] + 1;
                                        }
                                        break;
                                    }
                                }

                            }
                            for (int j = 0; j < 24; j++) {
                                list.add(arr_content[j]);
                            }
                            InitChart(list);
                            Log.e(TAG, "onResponse: " + response);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    ;
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: " + error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("service-api-key", "ESC-VIETLOTT-P2018");
                headers.put("service-session-id", "DyvKqrbWbtU92000e4e8");
                return headers;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this.getApplicationContext());
        requestQueue.add(postJson).setRetryPolicy(new DefaultRetryPolicy(50 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }



    private void InitChart(List<Integer> integerList) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            entries.add(new BarEntry((float) integerList.get(i), i));
        }
        List<String> labels = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            labels.add(i + "");
        }
        BarDataSet bardataset = new BarDataSet(entries, "Số lượng");
        bardataset.setColor(R.color.colorAccent);
        BarData data = new BarData(labels, bardataset);
        barchart.setData(data);
    }

    public static String convertTime(String string_date) {

        SimpleDateFormat f = new SimpleDateFormat("d/M/yyyy");
        SimpleDateFormat f2 = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date d = f.parse(string_date);
            return f2.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
            return string_date;
        }
    }

    public static Long convertToHour(String string_date) {
        SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        SimpleDateFormat f2 = new SimpleDateFormat("HH");
        try {
            Date d = f.parse(string_date);
            String time = f2.format(d);
            return Long.parseLong(time);
        } catch (ParseException e) {
            e.printStackTrace();
            return Long.parseLong(string_date);
        }
    }

}
