package com.example.magic_garden;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

public class GardenActivity extends AppCompatActivity {

    private int gridSize = 5; // For example, a 5x5 grid
    private int coins;
    private GridLayout gardenGridLayout;
    private TextView coinsTextView;
    private Button restartGameButton;
    private SharedPreferences prefs;
    private Map<String, Plant> plantsMap; // Map to store plants by their position

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
        plantsMap = new HashMap<>(); // Initialize the map

        loadCoins(); // Load the saved coins
        updateCoinsDisplay();
        initializeGardenGrid();

        restartGameButton.setOnClickListener(v -> restartGame());
    }

    private void initializeGardenGrid() {
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                Button button = new Button(this);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.rowSpec = GridLayout.spec(row);
                params.columnSpec = GridLayout.spec(col);
                params.width = 200;  // Set button width
                params.height = 200; // Set button height
                button.setLayoutParams(params);
                button.setBackgroundResource(android.R.color.transparent); // Set initial background to transparent
                int finalRow = row;
                int finalCol = col;
                button.setOnClickListener(v -> {
                    String key = finalRow + "_" + finalCol;
                    if (plantsMap.containsKey(key)) {
                        sellPlant(button, finalRow, finalCol);  // Sell the plant if it is already planted
                    } else {
                        plantItem(button, finalRow, finalCol);  // Plant the item otherwise
                    }
                });
                gardenGridLayout.addView(button);

                // Load saved state for each button
                boolean isPlanted = prefs.getBoolean("button_" + row + "_" + col, false);
                if (isPlanted) {
                    long plantTime = prefs.getLong("plant_time_" + row + "_" + col, 0);
                    Plant plant = new Plant(row, col, plantTime, this);
                    plantsMap.put(row + "_" + col, plant);
                    updatePlantImage(row, col, plant.getStage());
                    button.setText("*"); // Indicate planted state
                    button.setEnabled(true);  // Ensure the button is enabled for selling
                }
            }
        }
    }

    private void plantItem(Button button, int row, int col) {
        int plantCost = 10; // Cost for planting an item
        if (coins >= plantCost) {
            coins -= plantCost;
            updateCoinsDisplay();
            long plantTime = System.currentTimeMillis();
            Plant plant = new Plant(row, col, plantTime, this);
            plantsMap.put(row + "_" + col, plant);
            updatePlantImage(row, col, plant.getStage());
            button.setText("*"); // Indicate planted state
            Toast.makeText(this, "Item planted!", Toast.LENGTH_SHORT).show();

            // Save button state
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("button_" + row + "_" + col, true);
            editor.putLong("plant_time_" + row + "_" + col, plantTime);
            editor.apply();
        } else {
            Toast.makeText(this, "Not enough coins!", Toast.LENGTH_SHORT).show();
        }
    }

    public void updatePlantImage(int row, int col, int stage) {
        Button button = (Button) gardenGridLayout.getChildAt(row * gridSize + col);
        int[] plantImages = {
                R.drawable.plant_stage_0,
                R.drawable.plant_stage_1,
                R.drawable.plant_stage_2,
                R.drawable.plant_stage_3,
                R.drawable.plant_stage_4,
                R.drawable.plant_stage_5
        };
        button.setBackgroundResource(plantImages[stage]);
    }

    private void sellPlant(Button button, int row, int col) {
        String key = row + "_" + col;
        if (plantsMap.containsKey(key)) {
            Plant plant = plantsMap.get(key);
            int sellPrice = plant.getStage() * 10; // Selling price based on the stage
            coins += sellPrice;
            updateCoinsDisplay();
            button.setBackgroundResource(android.R.color.transparent); // Change background to transparent
            button.setText(""); // Remove the planted state indication
            Toast.makeText(this, "Item sold for " + sellPrice + " coins!", Toast.LENGTH_SHORT).show();

            // Remove plant from map and stop its growth
            plant.stopGrowth();
            plantsMap.remove(key);

            // Save button state
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("button_" + row + "_" + col, false);
            editor.remove("plant_time_" + row + "_" + col);
            editor.apply();
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

    private void loadCoins() {
        coins = prefs.getInt("coins", 0);
    }

    private void saveCoins() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("coins", coins);
        editor.apply();
    }
}
