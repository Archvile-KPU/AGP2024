package com.example.magic_garden;

import android.os.Bundle;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private int gridSize = 8; // Change this to scale the grid size
    private char[][] letters;
    private boolean[][] visited;
    private StringBuilder currentWord = new StringBuilder();
    private GridLayout gridLayout;
    private Button enterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gridLayout = findViewById(R.id.gridLayout);
        enterButton = findViewById(R.id.enterButton);
        gridLayout.setColumnCount(gridSize);
        gridLayout.setRowCount(gridSize);

        letters = generateRandomLetters(gridSize);
        visited = new boolean[gridSize][gridSize];

        initializeGrid();
        setupEnterButton();
    }

    private void setupEnterButton() {
        enterButton.setOnClickListener(v -> {
            String formedWord = currentWord.toString();
            if (isValidWord(formedWord)) {
                // Handle valid word: update score, reset grid, etc.
                System.out.println("Valid word: " + formedWord);
            } else {
                // Handle invalid word
                System.out.println("Invalid word: " + formedWord);
            }
            resetGrid();
        });
    }

    private void resetGrid() {
        // Reset the current word and visited flags
        currentWord.setLength(0);
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                visited[i][j] = false;
            }
        }
        // Optionally, refresh the UI to reflect the reset
    }

    private boolean isValidWord(String word) {
        // Placeholder: Implement actual dictionary check here
        return word.length() > 1; // Example check
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

    private void initializeGrid() {
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                TextView textView = new TextView(this);
                textView.setText(String.valueOf(letters[row][col]));
                textView.setTextSize(24);
                textView.setPadding(20, 20, 20, 20);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.rowSpec = GridLayout.spec(row);
                params.columnSpec = GridLayout.spec(col);
                textView.setLayoutParams(params);

                final int finalRow = row;
                final int finalCol = col;
                textView.setOnClickListener(v -> handleLetterClick(finalRow, finalCol));
                gridLayout.addView(textView);
            }
        }
    }

    private void handleLetterClick(int row, int col) {
        if (visited[row][col]) {
            return;
        }
        visited[row][col] = true;
        currentWord.append(letters[row][col]);
        System.out.println("Current word: " + currentWord);
        // Reset or further process the word here
    }
}