package com.wuc.fish;

import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    // ImageView ivFish = findViewById(R.id.iv_fish);
    // ivFish.setImageDrawable(new FishDrawable());
  }
}