package com.example.magic_garden;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class StageSelectionActivity extends AppCompatActivity {

    private Button stage1Button;
    private Button stage2Button;
    private Button stage3Button;
    private SharedPreferences prefs;
    private int coins;
    private boolean stage2Unlocked;
    private boolean stage3Unlocked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stage_selection);

        stage1Button = findViewById(R.id.stage1Button);
        stage2Button = findViewById(R.id.stage2Button);
        stage3Button = findViewById(R.id.stage3Button);

        prefs = getSharedPreferences("game_data", MODE_PRIVATE);
        coins = prefs.getInt("coins", 0);
        stage2Unlocked = prefs.getBoolean("stage2Unlocked", false);
        stage3Unlocked = prefs.getBoolean("stage3Unlocked", false);

        updateButtonState();

        stage1Button.setOnClickListener(v -> startGame(1, 60000, 1)); // 1 minute, 1x coins
        stage2Button.setOnClickListener(v -> unlockOrStartGame(2, 500, 30000, 5)); // 30 seconds, 5x coins
        stage3Button.setOnClickListener(v -> unlockOrStartGame(3, 1000, 10000, 10)); // 10 seconds, 10x coins
    }

    private void updateButtonState() {
        if (stage2Unlocked) {
            stage2Button.setText("Stage 2 (Unlocked)");
        }
        if (stage3Unlocked) {
            stage3Button.setText("Stage 3 (Unlocked)");
        }
    }

    private void unlockOrStartGame(int stage, int cost, long timeLimit, int coinMultiplier) {
        if (stage == 2 && !stage2Unlocked) {
            if (coins >= cost) {
                coins -= cost;
                stage2Unlocked = true;
                saveStageData();
                updateButtonState();
                Toast.makeText(this, "Stage 2 unlocked!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Not enough coins!", Toast.LENGTH_SHORT).show();
                return;
            }
        } else if (stage == 3 && !stage3Unlocked) {
            if (coins >= cost) {
                coins -= cost;
                stage3Unlocked = true;
                saveStageData();
                updateButtonState();
                Toast.makeText(this, "Stage 3 unlocked!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Not enough coins!", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        startGame(stage, timeLimit, coinMultiplier);
    }

    private void saveStageData() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("coins", coins);
        editor.putBoolean("stage2Unlocked", stage2Unlocked);
        editor.putBoolean("stage3Unlocked", stage3Unlocked);
        editor.apply();
    }

    private void startGame(int stage, long timeLimit, int coinMultiplier) {
        Intent intent = new Intent(StageSelectionActivity.this, MainActivity.class);
        intent.putExtra("STAGE", stage);
        intent.putExtra("TIME_LIMIT", timeLimit);
        intent.putExtra("COIN_MULTIPLIER", coinMultiplier);
        startActivity(intent);
        finish(); // Finish StageSelectionActivity so it won't stay in the back stack
    }
}
