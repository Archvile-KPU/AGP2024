package com.example.magic_garden;

import android.os.Handler;

public class Plant {
    private int row;
    private int col;
    private int stage;
    private long plantTime;
    private Handler handler;
    private Runnable growthTask;
    private GardenActivity gardenActivity;

    public Plant(int row, int col, long plantTime, GardenActivity gardenActivity) {
        this.row = row;
        this.col = col;
        this.plantTime = plantTime;
        this.gardenActivity = gardenActivity;
        this.handler = new Handler();
        this.stage = calculateStage(); // Calculate the initial stage based on elapsed time

        // Define the task to update the stage periodically
        growthTask = new Runnable() {
            @Override
            public void run() {
                int newStage = calculateStage();
                if (newStage > stage) {
                    stage = newStage;
                    gardenActivity.updatePlantImage(row, col, stage);
                }
                if (stage < 5) {
                    handler.postDelayed(this, 1000); // Schedule the task to run again after 1 second
                }
            }
        };
        handler.post(growthTask); // Start the task for the first time
    }

    private int calculateStage() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - plantTime;
        int calculatedStage = (int) (elapsedTime / 10000); // 10 seconds per stage
        return Math.min(calculatedStage, 5); // Ensure stage doesn't exceed 5
    }

    public int getStage() {
        return stage;
    }

    public void stopGrowth() {
        handler.removeCallbacks(growthTask); // Stop the growth task
    }
}
