package com.example.magic_garden;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class GardenActivity extends AppCompatActivity {

    private int gridSize = 5; // For example, a 5x5 grid
    private int coins;
    private GridLayout gardenGridLayout;
    private TextView coinsTextView;
    private Button restartGameButton;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_garden);

        // Get the coins from the intent
        Intent intent = getIntent();
        coins = intent.getIntExtra("COINS", 0);

        // Initialize views
        gardenGridLayout = findViewById(R.id.gardenGridLayout);
        coinsTextView = findViewById(R.id.coinsTextView);
        restartGameButton = findViewById(R.id.restartGameButton);

        gardenGridLayout.setColumnCount(gridSize);
        gardenGridLayout.setRowCount(gridSize);

        prefs = getSharedPreferences("game_data", MODE_PRIVATE);

        updateCoinsDisplay();
        initializeGardenGrid();

        restartGameButton.setOnClickListener(v -> restartGame());
    }

    private void initializeGardenGrid() {
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                Button button = new Button(this);
                button.setText(""); // Set initial text to blank
                int finalRow = row;
                int finalCol = col;
                button.setOnClickListener(v -> plantItem(button, finalRow, finalCol));
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.rowSpec = GridLayout.spec(row);
                params.columnSpec = GridLayout.spec(col);
                button.setLayoutParams(params);
                gardenGridLayout.addView(button);

                // Load saved state for each button
                boolean isPlanted = prefs.getBoolean("button_" + row + "_" + col, false);
                if (isPlanted) {
                    button.setText("*");
                    button.setEnabled(false);
                }
            }
        }
    }

    private void plantItem(Button button, int row, int col) {
        int plantCost = 10; // Example cost for planting an item
        if (coins >= plantCost) {
            coins -= plantCost;
            updateCoinsDisplay();
            button.setText("*");
            button.setEnabled(false);
            Toast.makeText(this, "Item planted!", Toast.LENGTH_SHORT).show();

            // Save button state
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("button_" + row + "_" + col, true);
            editor.apply();
        } else {
            Toast.makeText(this, "Not enough coins!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateCoinsDisplay() {
        coinsTextView.setText("Coins: " + coins);
    }

    private void restartGame() {
        saveCoins();  // Save coins before restarting the game
        Intent intent = new Intent(GardenActivity.this, MainActivity.class);
        startActivity(intent);
        finish();  // Finish the current activity to prevent the user from going back
    }

    private void saveCoins() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("coins", coins);
        editor.apply();
    }
}
