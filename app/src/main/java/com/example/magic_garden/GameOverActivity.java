package com.example.magic_garden;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class GameOverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        // Get the score from the intent
        Intent intent = getIntent();
        int score = intent.getIntExtra("SCORE", 0);

        // Display the score
        TextView scoreTextView = findViewById(R.id.scoreTextView);
        scoreTextView.setText("Your score: " + score);

        // Optionally, add buttons or other UI elements to restart the game or exit
    }
}
