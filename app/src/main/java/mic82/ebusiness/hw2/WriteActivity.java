package mic82.ebusiness.hw2;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

public class WriteActivity extends AppCompatActivity {
    // Acquire a reference to the system Location Manager
    Locator m_locator;
    Context context;
    Button submit;
    Button cancel;
    private String MakeTwoDigits(int in)
    {
        boolean x=in<10;
        String res=String.valueOf(in);
        if(x)
        {
            res="0"+res;
        }
        return res;
    }
    Location loc=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        context=this;

        m_locator=new Locator(context);

        cancel=(Button)findViewById(R.id.button4);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });

        submit=(Button)findViewById(R.id.button3);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText ed1=(EditText)findViewById(R.id.editText);
                EditText ed2=(EditText)findViewById(R.id.editText2);
                if(ed1.getText().toString().matches("\\s*")||ed2.getText().toString().matches("\\s*"))
                {
                    Toast.makeText(WriteActivity.this, "Please complete the title and content.", Toast.LENGTH_SHORT).show();
                    return;
                }

                m_locator.getLocation(Locator.Method.NETWORK_THEN_GPS, new Locator.Listener() {
                    @Override
                    public void onLocationFound(Location location) {
                        loc=location;
                    }

                    @Override
                    public void onLocationNotFound() {
                        loc=null;
                    }
                });

                if(loc==null)
                {
                    Toast.makeText(WriteActivity.this, "Cannot Retrieve location.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Calendar cldr = Calendar.getInstance();

                int year=cldr.get(Calendar.YEAR);
                int month=cldr.get(Calendar.MONTH)+1;
                int day=cldr.get(Calendar.DAY_OF_MONTH);

                int hour=cldr.get(Calendar.HOUR_OF_DAY);
                int minute=cldr.get(Calendar.MINUTE);

                String date=String.valueOf(year)+"/"+MakeTwoDigits(month)+"/"+MakeTwoDigits(day);
                String time=MakeTwoDigits(hour)+":"+MakeTwoDigits(minute);

                String x=String.valueOf(loc.getLongitude());
                String y=String.valueOf(loc.getLatitude());
                Data data=new Data(ed1.getText().toString(),ed2.getText().toString(),x,y,date,time);

                Intent intent = new Intent();
                intent.putExtra("obj", data);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

}
