import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class tictactoe implements ActionListener {

    private JFrame frame;
    private JPanel panel;
    private JButton[][] buttons;
    private JButton restartButton;
    private JLabel playerTurnLabel;
    private JLabel scoreLabel;
    private int p1Score = 0;
    private int p2Score = 0;
    private boolean p1_turn = true;
    private int boardSize = 3; // Default board size
    private boolean aiEnabled;
    private boolean hardDifficulty;

    public tictactoe(int size) {
        UIManager.put("OptionPane.messageFont", new Font("Arial", Font.BOLD, 14));
        UIManager.put("OptionPane.buttonFont", new Font("Arial", Font.PLAIN, 12));

        UIManager.put("OptionPane.background", Color.WHITE);
        UIManager.put("Panel.background", Color.WHITE);
        boardSize = size;

        frame = new JFrame("Tic Tac Toe");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JPanel gamePanel = new JPanel();
        gamePanel.setLayout(new GridLayout(boardSize, boardSize));

        int response = JOptionPane.showConfirmDialog(null, "Do you want to play against an AI?", "Choose opponent",
                JOptionPane.YES_NO_OPTION);
        aiEnabled = (response == JOptionPane.YES_OPTION);

        if (aiEnabled) {
            // Ask the user if they want to play against a hard AI
            response = JOptionPane.showConfirmDialog(null,
                    "Do you want to play against a hard AI? NOTE:  LARGER BOARDS WILL CAUSE LONGER COMPUTE TIMES",
                    "Choose difficulty", JOptionPane.YES_NO_OPTION);
            hardDifficulty = (response == JOptionPane.YES_OPTION);
        }

        buttons = new JButton[boardSize][boardSize];

        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                buttons[i][j] = new JButton("", null);
                buttons[i][j].addActionListener(this);
                buttons[i][j].setFont(new Font("Arial", Font.BOLD, 20));

                // Set the background color of the button
                buttons[i][j].setBackground(Color.WHITE);

                // Set the foreground (text) color of the button
                buttons[i][j].setForeground(Color.BLACK);

                // Set the border of the button
                buttons[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK));
                gamePanel.add(buttons[i][j]);
            }
        }

        restartButton = new JButton("Restart");
        restartButton.addActionListener(e -> restartGame());

        playerTurnLabel = new JLabel("Player X's Turn", JLabel.CENTER);
        scoreLabel = new JLabel("Player X: 0 | Player O: 0", JLabel.CENTER);

        panel.add(playerTurnLabel, BorderLayout.NORTH);
        panel.add(gamePanel, BorderLayout.CENTER);
        panel.add(restartButton, BorderLayout.SOUTH);
        panel.add(scoreLabel, BorderLayout.SOUTH);

        frame.add(panel);
        frame.setSize(400, 450);
        frame.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        JButton button = (JButton) e.getSource();

        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (buttons[i][j] == button && button.getText().equals("")) {
                    button.setText(p1_turn ? "X" : "O");
                    button.setEnabled(false);
                    if (wincond(p1_turn ? "X" : "O")) {
                        updateScore(p1_turn ? "X" : "O");
                        showWinDialog("Player " + (p1_turn ? "X" : "O") + " wins!");
                        return;
                    } else if (tiecond()) {
                        showTieDialog();
                        return;
                    }

                    p1_turn = !p1_turn;

                    // If AI is enabled and game is not over, AI makes a move
                    if (aiEnabled && !p1_turn && !gameOver()) {
                        aiTurn();
                    }
                }
            }
        }
    }

    private boolean wincond(String player) {
        // Check for a win condition based on the dynamic board size
        for (int i = 0; i < boardSize; i++) {
            // Check rows
            boolean rowWin = true;
            for (int j = 0; j < boardSize; j++) {
                if (!buttons[i][j].getText().equals(player)) {
                    rowWin = false;
                    break;
                }
            }
            if (rowWin)
                return true;

            // Check columns
            boolean colWin = true;
            for (int j = 0; j < boardSize; j++) {
                if (!buttons[j][i].getText().equals(player)) {
                    colWin = false;
                    break;
                }
            }
            if (colWin)
                return true;
        }

        // Check diagonals
        boolean diag1Win = true;
        boolean diag2Win = true;
        for (int i = 0; i < boardSize; i++) {
            if (!buttons[i][i].getText().equals(player)) {
                diag1Win = false;
            }
            if (!buttons[i][boardSize - i - 1].getText().equals(player)) {
                diag2Win = false;
            }
        }
        return diag1Win || diag2Win;
    }

    private boolean tiecond() {
        // Check for a tie based on the dynamic board size
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (buttons[i][j].isEnabled()) {
                    return false;
                }
            }
        }
        return true;
    }

    private void updateScore(String player) {
        if (player.equals("X")) {
            p1Score++;
        } else {
            p2Score++;
        }
        scoreLabel.setText("Player X: " + p1Score + " | Player O: " + p2Score);
    }

    private void showWinDialog(String message) {
        JOptionPane.showMessageDialog(frame, message);
        restartGame();
    }

    private void showTieDialog() {
        JOptionPane.showMessageDialog(frame, "It's a tie!");
        restartGame();
    }

    private void restartGame() {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                buttons[i][j].setText("");
                buttons[i][j].setEnabled(true);
                buttons[i][j].setFont(new Font("Arial", Font.BOLD, 20));

                // Set the background color of the button
                buttons[i][j].setBackground(Color.WHITE);

                // Set the foreground (text) color of the button
                buttons[i][j].setForeground(Color.BLACK);

                // Set the border of the button
                buttons[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK));
            }
        }
        p1_turn = false;
        playerTurnLabel.setText("Player X's Turn");
    }

    private void aiTurn() {
        if (hardDifficulty) {
            hardAiTurn();
        } else {
            easyAiTurn();
        }
    }

    private void easyAiTurn() {
        Random random = new Random();
        int i, j;
        do {
            i = random.nextInt(boardSize);
            j = random.nextInt(boardSize);
        } while (!buttons[i][j].getText().equals(""));

        buttons[i][j].setText("O");
        buttons[i][j].setEnabled(false);
        if (wincond("O")) {
            updateScore("O");
            showWinDialog("Player O wins!");
        } else if (tiecond()) {
            showTieDialog();
        }
        p1_turn = !p1_turn;
    }

    private void hardAiTurn() {
        int bestScore = Integer.MIN_VALUE;
        int[] move = new int[2];
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (buttons[i][j].getText().equals("")) {
                    buttons[i][j].setText("O");
                    int score = minimax(buttons, 0, false);
                    buttons[i][j].setText("");
                    if (score > bestScore) {
                        bestScore = score;
                        move[0] = i;
                        move[1] = j;
                    }
                }
            }
        }
        buttons[move[0]][move[1]].setText("O");
        buttons[move[0]][move[1]].setEnabled(false);
        if (wincond("O")) {
            updateScore("O");
            showWinDialog("Player O wins!");
        } else if (tiecond()) {
            showTieDialog();
        }
        p1_turn = !p1_turn;
    }

    private int minimax(JButton[][] board, int depth, boolean isMaximizing) {
        if (wincond("O")) {
            return 1;
        } else if (wincond("X")) {
            return -1;
        } else if (tiecond() || depth == 5) { // depth limit of 3
            return 0;
        }

        if (isMaximizing) {
            int bestScore = Integer.MIN_VALUE;
            for (int i = 0; i < boardSize; i++) {
                for (int j = 0; j < boardSize; j++) {
                    if (board[i][j].getText().equals("")) {
                        board[i][j].setText("O");
                        int score = minimax(board, depth + 1, false);
                        board[i][j].setText("");
                        bestScore = Math.max(score, bestScore);
                    }
                }
            }
            return bestScore;
        } else {
            int bestScore = Integer.MAX_VALUE;
            for (int i = 0; i < boardSize; i++) {
                for (int j = 0; j < boardSize; j++) {
                    if (board[i][j].getText().equals("")) {
                        board[i][j].setText("X");
                        int score = minimax(board, depth + 1, true);
                        board[i][j].setText("");
                        bestScore = Math.min(score, bestScore);
                    }
                }
            }
            return bestScore;
        }
    }

    private boolean gameOver() {
        return wincond("X") || wincond("O") || tiecond();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String input = JOptionPane.showInputDialog(null, "Enter the board size (e.g., 3 for 3x3):");
            try {
                int boardSize = Integer.parseInt(input);
                new tictactoe(boardSize);
            } catch (NumberFormatException e) {
                System.err.println("Invalid input. Using the default size.");
                new tictactoe(3); // Default size
            }
        });
    }
}