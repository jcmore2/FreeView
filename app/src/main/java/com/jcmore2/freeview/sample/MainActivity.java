package com.jcmore2.freeview.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.jcmore2.freeview.FreeView;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        findViewById(R.id.dismiss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FreeView.get().dismissFreeView();
            }
        });

        findViewById(R.id.show).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FreeView.init(MainActivity.this).withView(R.layout.content).showFreeView(new FreeView.FreeViewListener() {
                    @Override
                    public void onShow() {
                        Toast.makeText(MainActivity.this, "onShow", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDismiss() {
                        Toast.makeText(MainActivity.this, "onDismiss", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        findViewById(R.id.showBG).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FreeView.init(MainActivity.this).withView(R.layout.content).dismissOnBackground(false).showFreeView(new FreeView.FreeViewListener() {
                    @Override
                    public void onShow() {
                        Toast.makeText(MainActivity.this, "onShow", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDismiss() {
                        Toast.makeText(MainActivity.this, "onDismiss", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
