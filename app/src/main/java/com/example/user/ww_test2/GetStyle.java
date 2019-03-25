package com.example.user.ww_test2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class GetStyle extends AppCompatActivity {
    Spinner spi_gender,spi_style;
    ListView list;
    Button btn;
    Bitmap bitmap;
    ArrayList<String> items;
    ArrayAdapter adapter,adapter2;

    String date,weather,location,select_gender,select_style;
    String[] genderData ={"성별","남","여"};
    String[] styleData ={"스타일","베이직","러블리","섹시","유니크","시크모던"};
    double temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_style);

        try {
            //전해준 값 받아오기
            Intent it = getIntent();
            date = it.getStringExtra("CurDate");
            weather = it.getStringExtra("CurWeather");
            temp = it.getDoubleExtra("CurTemp", 300);

            if (temp == 300 || date == null || weather == null) {
                Toast.makeText(this, "날씨정보를 받아오지 못했습니다", Toast.LENGTH_LONG).show();
                Intent it2 = new Intent(this, MainActivity.class);
                this.startActivity(it2);
                this.finish();
            }

            spi_gender = (Spinner) findViewById(R.id.gender);
            spi_style = (Spinner) findViewById(R.id.style);
            list = (ListView) findViewById(R.id.list);
            btn  = (Button)findViewById(R.id.btn);


            adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, genderData);
            spi_gender.setAdapter(adapter);
            spi_gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    //position이 몇번째것이 선택되었는지 알려줌.
                    ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                    select_gender = genderData[position];
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            adapter2 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, styleData);
            spi_style.setAdapter(adapter2);
            spi_style.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                    select_style = styleData[position];
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            list.setVisibility(View.INVISIBLE); //조건을 선택하지 않으면 리스트뷰가 안보이게
        }catch (Exception e)
        {
            System.out.println("에러에용:"+e);
            Toast.makeText(this,e+"/"+e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }


    public void clickBtn(View v) {
        try {
            SearchCody sc = new SearchCody();
            sc.start();
        }catch (Exception e){
            System.out.println("ddd"+e);
        }
    }

    class SearchCody extends Thread {
        public void run(){
            try{
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String Season,Temp_w,Gender,Style;
                        String []file_data= new String[5];
                        Season=getSeason(); //3Type { sf,sum,win }
                        Temp_w=getTemp();   // { 1-7 }
                        Style=StyletoCode(); // { basic,lovely,vintage,sexy,unique,modern }
                        if(spi_gender.getSelectedItem().toString().equals("남"))
                            Gender="m"; //man
                        else if(spi_gender.getSelectedItem().toString().equals("여"))
                            Gender="w"; //woman
                        else
                            Gender="성별";

                        System.out.println("찾을거:"+Season+"/"+Temp_w+"/"+Style+"/"+Gender);
                        //Toast.makeText(this,"찾을거:"+Season+"/"+Temp_w+"/"+Style+"/"+Gender,Toast.LENGTH_LONG).show();

                        list.setVisibility(View.VISIBLE);
                        items = new ArrayList<>();

                        if(Gender.equals("성별")) {
                            Toast.makeText(getApplicationContext(), "성별을 선택하세요", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Field[] drawables = R.raw.class.getFields();
                        for (Field f : drawables) {
                            StringTokenizer st = new StringTokenizer(f.getName().toString(),"_");
                            if(st.countTokens()>1) {
                                int i;
                                for (i = 0; st.countTokens() > 1 && st.hasMoreTokens(); i++) {
                                    file_data[i] = st.nextToken(); //파일 정보 분해 {파일제목, 계절, 온도, 성별, 스타일}
                                }
                                file_data[i] = st.nextToken();
                                if (Style.equals("스타일")) { //스타일 선택 안했을 경우
                                    if (file_data[1].equals(Season) && file_data[2].equals(Temp_w) && file_data[3].equals(Gender))
                                        items.add(f.getName());
                                }
                                else if (file_data[1].equals(Season) && file_data[2].equals(Temp_w) && file_data[3].equals(Gender) && file_data[4].equals(Style)) {
                                    items.add(f.getName());
                                }
                            }
                        }
                        CustomerAdapter adapter3 = new CustomerAdapter(getApplicationContext(), 0, items);
                        list.setAdapter(adapter3);
                    }
                });

            }catch (Exception e) {
                System.out.println("에러:"+e);
                //Toast.makeText(getApplicationContext(),"에러:"+e,Toast.LENGTH_LONG).show();
            }
        }

        public String getSeason(){
            int month = Integer.parseInt(date.substring(5,7));
            String season;

            if(month==12||month==1||month==2) //겨울
                season="win";
            else if(month==6||month==7||month==8)
                season="sum";
            else
                season="sf";

            return season;
        }
        public String getTemp() {
            String tempStep = "0";
        /*온도를 단계별로 나누기
            1-30~ 매우더움
            2-25-30 좀 더움
            3-20-25 약간 더움
            4-15-20 선선
            5-10-15 약간 추움
            6-5-10 좀 추움
            7- ~5 매우추움

            평균온도
            1- 678
            2- 678 //59
            3- 56789
            4- 45910
            5- 341011
            6- 31011 //12
            7- 12 1 2
            --------------------
            여름 : 1,2단계 많이, 3단계 조금
            겨울 : 7단계 많이, 6단계조금
            봄가을 : 3,4,5,6단계 골고루

            출처:http://www.kma.go.kr/weather/climate/past_table.jsp?stn=108&yy=2016&x=27&y=5&obs=07
         */
            if (temp >= 30.0)
                tempStep = "1";
            else if (30.0 > temp && temp >= 25.0)
                tempStep = "2";
            else if (25.0 > temp && temp >= 20.0)
                tempStep = "3";
            else if (20.0 > temp && temp >= 15.0)
                tempStep = "4";
            else if (15.0 > temp && temp >= 10.0)
                tempStep = "5";
            else if (10.0 > temp && temp >= 5.0)
                tempStep = "6";
            else if (5.0 > temp && temp>=-100.0)
                tempStep = "7";
            return tempStep;
        }
        public String StyletoCode(){
            String style = spi_style.getSelectedItem().toString();
            String sc;
            if(style.equals("베이직"))
                sc="basic";
            else if(style.equals("러블리"))
                sc="lovely";
            else if(style.equals("섹시"))
                sc="sexy";
            else if(style.equals("유니크"))
                sc="unique";
            else if(style.equals("시크모던"))
                sc="modern";
            else
                sc=style;

            return sc;
        }
    }


    public class CustomerAdapter extends ArrayAdapter<String>{
        private ArrayList<String> items;
        public CustomerAdapter(Context context, int textViewResourceId, ArrayList<String> objects){
            super(context,textViewResourceId,objects);
            this.items = objects;
        }
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.list_view, null);
            }

            // ImageView 인스턴스
            ImageView imageView = (ImageView)v.findViewById(R.id.listImage);
            //imageView.setImageBitmap(null);
            int resId=getResources().getIdentifier("@raw/"+items.get(position), "raw", "com.example.user.ww_test2");



            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(getResources(), resId, bmOptions);
            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = 2;
            bmOptions.inPurgeable = true;

            bitmap = BitmapFactory.decodeResource(getResources(), resId, bmOptions);
            imageView.setImageBitmap(bitmap);
            //imageView.setImageDrawable(new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), resId)));
            return v;
        }
    }

    @Override
    public void onBackPressed() {
        if(bitmap!=null)
            bitmap.recycle();
        Intent it2=new Intent(this,MainActivity.class);
        this.startActivity(it2);
        this.finish();
    }

    @Override
    protected void onPause() {
        if(bitmap!=null)
            bitmap.recycle();
        super.onPause();
    }

    @Override
    protected void onStop() {
        if(bitmap!=null)
            bitmap.recycle();
        super.onStop();
    }
}
