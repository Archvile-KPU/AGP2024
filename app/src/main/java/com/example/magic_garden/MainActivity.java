package com.example.magic_garden;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private int gridSize = 7; // For example, a 7x7 grid
    private char[][] letters;
    private boolean[][] visited;
    private StringBuilder currentWord = new StringBuilder();
    private GridLayout gridLayout;
    private TextView timerTextView;
    private TextView currentWordTextView;
    private TextView scoreTextView;
    private Button enterButton;
    private HashSet<String> dictionary = new HashSet<>();
    private int score = 0;  // Initialize the score at the beginning
    private int coins = 0;  // Initialize the coins

    // List to track selected letters' positions
    private ArrayList<int[]> selectedLetters = new ArrayList<>();
    // Array to store references to TextViews in the GridLayout
    private TextView[][] letterTextViews;

    // Timer variables
    private CountDownTimer gameTimer;
    private long gameTimeInMillis = 60000;  // 1 minute in milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        gridLayout = findViewById(R.id.gridLayout);
        timerTextView = findViewById(R.id.timerTextView);
        currentWordTextView = findViewById(R.id.currentWordTextView);
        scoreTextView = findViewById(R.id.scoreTextView);
        enterButton = findViewById(R.id.enterButton);

        gridLayout.setColumnCount(gridSize);
        gridLayout.setRowCount(gridSize);

        letters = generateRandomLetters(gridSize);
        visited = new boolean[gridSize][gridSize];
        letterTextViews = new TextView[gridSize][gridSize];
        loadDictionary();

        initializeGrid();
        setupEnterButton();  // Ensure this method is called

        loadCoins();  // Load coins from SharedPreferences

        startGameTimer();  // Start the game timer
    }

    private char[][] generateRandomLetters(int size) {
        char[][] tempLetters = new char[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                tempLetters[i][j] = (char) ('A' + Math.random() * 26);
            }
        }
        return tempLetters;
    }

    private void loadDictionary() {
        String[] words = getResources().getStringArray(R.array.dictionary_words);
        dictionary.addAll(Arrays.asList(words));  // Adding words to the HashSet
    }

    private void initializeGrid() {
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                TextView textView = new TextView(this);
                textView.setText(String.valueOf(letters[row][col]));
                textView.setTextSize(24);
                textView.setTextColor(getResources().getColor(android.R.color.white));  // Set initial text color to white
                textView.setPadding(20, 20, 20, 20);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.rowSpec = GridLayout.spec(row);
                params.columnSpec = GridLayout.spec(col);
                textView.setLayoutParams(params);

                final int finalRow = row;
                final int finalCol = col;
                textView.setOnClickListener(v -> {
                    handleLetterClick(finalRow, finalCol, textView);
                });
                gridLayout.addView(textView);
                letterTextViews[row][col] = textView; // Store the reference to the TextView
            }
        }
    }

    private void handleLetterClick(int row, int col, TextView textView) {
        if (!visited[row][col]) {  // Check if the letter has not been used yet
            visited[row][col] = true;  // Mark this cell as visited
            currentWord.append(letters[row][col]);  // Append the letter to the current word
            selectedLetters.add(new int[]{row, col});  // Track the selected letter's position
            textView.setTextColor(getResources().getColor(android.R.color.holo_blue_light));  // Change text color to blue to indicate selection
            updateCurrentWordView();  // Update the display to show the new current word
        }
    }

    private char generateRandomLetter() {
        return (char) ('A' + Math.random() * 26);  // Generate a random letter from A to Z
    }

    private void updateCurrentWordView() {
        currentWordTextView.setText(currentWord.toString());  // Set the text of the TextView to the current word
    }

    private void setupEnterButton() {
        System.out.println("Setting up enter button...");  // Debug message
        enterButton.setOnClickListener(v -> {
            System.out.println("Enter button clicked!");  // Debug message
            String formedWord = currentWord.toString().toUpperCase();
            if (isValidWord(formedWord)) {
                score += formedWord.length() * 5; // Update the score calculation
                System.out.println("Valid word: " + formedWord + ", Score: " + score);
                replaceSelectedLetters();  // Replace the letters used to form the word
            } else {
                System.out.println("Invalid word: " + formedWord);
                resetGrid(false);  // Reset the grid without replacing letters
            }
            updateScoreDisplay();  // Update the score display
            clearCurrentWord();  // Clear the current word in the TextView
        });
    }

    private void updateScoreDisplay() {
        scoreTextView.setText("Score: " + score);  // Update the TextView to show the current score
    }

    private void replaceSelectedLetters() {
        System.out.println("Replacing selected letters...");  // Debug message
        for (int[] position : selectedLetters) {
            int row = position[0];
            int col = position[1];
            char newLetter = generateRandomLetter();  // Generate a new letter
            letters[row][col] = newLetter;  // Update the letters array
            TextView textView = letterTextViews[row][col];
            textView.setText(String.valueOf(newLetter));  // Update the TextView with the new letter
            textView.setTextColor(getResources().getColor(android.R.color.white));  // Reset text color to white
            visited[row][col] = false;  // Reset visited status
            System.out.println("Updated (" + row + ", " + col + ") to " + newLetter);  // Debug message
        }
        selectedLetters.clear();  // Clear the list of selected letters
    }

    private boolean isValidWord(String word) {
        return dictionary.contains(word);
    }

    private void resetGrid(boolean resetLetters) {
        updateCurrentWordView();  // Update the display to show no current word
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                visited[i][j] = false;
                TextView textView = letterTextViews[i][j];
                textView.setEnabled(true);  // Ensure all TextViews are re-enabled
                textView.setTextColor(getResources().getColor(android.R.color.white)); // Ensure color reset to white
                if (resetLetters) {
                    letters[i][j] = generateRandomLetter();
                    textView.setText(String.valueOf(letters[i][j]));  // Update the TextView with the new letter
                }
            }
        }
    }

    private void clearCurrentWord() {
        currentWord.setLength(0);
        updateCurrentWordView();
    }

    private void startGameTimer() {
        gameTimer = new CountDownTimer(gameTimeInMillis, 1000) {
            public void onTick(long millisUntilFinished) {
                // Update the timer display
                long secondsRemaining = millisUntilFinished / 1000;
                timerTextView.setText("Time: " + secondsRemaining);
            }

            public void onFinish() {
                endGame();
            }
        }.start();
    }

    private void endGame() {
        coins += score;  // Set the coins to the current score
        System.out.println("Game over! Coins earned: " + coins);  // Debug message
        saveCoins();  // Save coins to SharedPreferences
        Intent intent = new Intent(MainActivity.this, GardenActivity.class);
        intent.putExtra("COINS", coins);
        startActivity(intent);
        finish();  // Finish the current activity to prevent the user from going back
    }

    private void loadCoins() {
        SharedPreferences prefs = getSharedPreferences("game_data", MODE_PRIVATE);
        coins = prefs.getInt("coins", 0);
    }

    private void saveCoins() {
        SharedPreferences prefs = getSharedPreferences("game_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("coins", coins);
        editor.apply();
    }
}
