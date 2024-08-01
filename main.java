package com.example.diagonal_check;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private TextView timerTextView;
    private Button startGameButton;
    private Button checkButton;
    private GridLayout chessboard;
    private long startTime = 0L;
    private Handler timerHandler = new Handler();
    private int[][] board = new int[4][4];

    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = SystemClock.uptimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;

            timerTextView.setText(String.format("Timer: %02d:%02d", minutes, seconds));
            timerHandler.postDelayed(this, 500);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timerTextView = findViewById(R.id.timer);
        startGameButton = findViewById(R.id.start_game_button);
        checkButton = findViewById(R.id.check_button);
        chessboard = findViewById(R.id.chessboard);

        if (timerTextView == null || startGameButton == null || checkButton == null || chessboard == null) {
            throw new NullPointerException("One or more views are not initialized correctly.");
        }

        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame();
            }
        });

        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkSolution();
            }
        });

        // Initialize board with empty cells
        initializeBoard();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timerHandler.removeCallbacks(timerRunnable);
    }

    private void startGame() {
        initializeBoard();
        startTime = SystemClock.uptimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);
    }

    private void initializeBoard() {
        // Clear the board
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                board[i][j] = 0;
                ImageView cell = (ImageView) chessboard.getChildAt(i * 4 + j);
                if (cell == null) {
                    Log.e(TAG, "Cell at position " + i + "," + j + " is null.");
                    continue;
                }
                cell.setImageResource(android.R.color.transparent);
                cell.setOnClickListener(new CellClickListener(i, j));

                // Set background color for alternating black and white cells
                if ((i + j) % 2 == 0) {
                    cell.setBackgroundColor(getResources().getColor(R.color.white));
                } else {
                    cell.setBackgroundColor(getResources().getColor(R.color.black));
                }
            }
        }
    }

    private void checkSolution() {
        NQueenSolver solver = new NQueenSolver();
        if (solver.solveNQ(board)) {
            Toast.makeText(MainActivity.this, "Solution is correct!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "Solution is incorrect.", Toast.LENGTH_SHORT).show();
        }
    }

    private class CellClickListener implements View.OnClickListener {
        private int row;
        private int col;

        CellClickListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void onClick(View v) {
            ImageView cell = (ImageView) v;
            if (board[row][col] == 0) {
                board[row][col] = 1;
                if (R.drawable.queen != 0) {
                    cell.setImageResource(R.drawable.queen); // Add an image of a queen piece
                } else {
                    Log.e(TAG, "Queen image resource not found.");
                }
            } else {
                board[row][col] = 0;
                cell.setImageResource(android.R.color.transparent);
            }
        }
    }

    public class NQueenSolver {
        private static final int N = 4;

        public boolean solveNQUtil(int board[][], int col) {
            if (col >= N) return true;

            for (int i = 0; i < N; i++) {
                if (isSafe(board, i, col)) {
                    board[i][col] = 1;
                    if (solveNQUtil(board, col + 1)) return true;
                    board[i][col] = 0;
                }
            }
            return false;
        }

        private boolean isSafe(int board[][], int row, int col) {
            for (int i = 0; i < col; i++)
                if (board[row][i] == 1) return false;

            for (int i = row, j = col; i >= 0 && j >= 0; i--, j--)
                if (board[i][j] == 1) return false;

            for (int i = row, j = col; j >= 0 && i < N; i++, j--)
                if (board[i][j] == 1) return false;

            return true;
        }

        public boolean solveNQ(int[][] board) {
            return solveNQUtil(board, 0);
        }
    }
}