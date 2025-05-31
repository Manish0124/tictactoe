import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;
import javax.swing.Timer;

public class Main extends JFrame implements ActionListener {
    private static final int BOARD_SIZE = 3;
    private static final Color BACKGROUND_COLOR = new Color(45, 52, 54);
    private static final Color BUTTON_COLOR = new Color(99, 110, 114);
    private static final Color BUTTON_HOVER_COLOR = new Color(116, 125, 140);
    private static final Color X_COLOR = new Color(255, 107, 107);
    private static final Color O_COLOR = new Color(72, 219, 251);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 48);
    private static final Font STATUS_FONT = new Font("Arial", Font.BOLD, 18);

    private JButton[][] buttons;
    private char[][] board;
    private char currentPlayer;
    private boolean gameActive;
    private JLabel statusLabel;
    private JButton resetButton;

    // Enhanced features
    private int playerXScore = 0;
    private int playerOScore = 0;
    private int drawCount = 0;
    private JLabel scoreLabel;
    private boolean isAIMode = false;
    private char aiPlayer = 'O';
    private char humanPlayer = 'X';
    private AILevel aiLevel = AILevel.MEDIUM;
    private JComboBox<String> gameModeCombo;
    private JComboBox<AILevel> difficultyCombo;
    private Timer animationTimer;
    private List<JButton> winningButtons = new ArrayList<>();

    // Game modes and AI levels
    enum AILevel {
        EASY("Easy"), MEDIUM("Medium"), HARD("Hard");
        private final String displayName;
        AILevel(String displayName) { this.displayName = displayName; }
        @Override
        public String toString() { return displayName; }
    }

    public Main() {
        initializeGame();
        setupGUI();
    }

    private void initializeGame() {
        buttons = new JButton[BOARD_SIZE][BOARD_SIZE];
        board = new char[BOARD_SIZE][BOARD_SIZE];
        currentPlayer = 'X';
        gameActive = true;

        // Initialize board with empty spaces
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                board[row][col] = ' ';
            }
        }
    }

    private void setupGUI() {
        setTitle("Tic-Tac-Toe");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);
        getContentPane().setBackground(BACKGROUND_COLOR);

        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create status panel
        JPanel statusPanel = createStatusPanel();
        mainPanel.add(statusPanel, BorderLayout.NORTH);

        // Create game board panel
        JPanel boardPanel = createBoardPanel();
        mainPanel.add(boardPanel, BorderLayout.CENTER);

        // Create control panel
        JPanel controlPanel = createControlPanel();
        mainPanel.add(controlPanel, BorderLayout.SOUTH);

        add(mainPanel);
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(BACKGROUND_COLOR);
        statusPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Game status
        statusLabel = new JLabel("Player X's Turn");
        statusLabel.setFont(STATUS_FONT);
        statusLabel.setForeground(TEXT_COLOR);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Score display
        scoreLabel = new JLabel("X: 0  |  O: 0  |  Draws: 0");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 14));
        scoreLabel.setForeground(new Color(200, 200, 200));
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Game mode and difficulty controls
        JPanel controlsPanel = createGameControlsPanel();

        statusPanel.add(statusLabel, BorderLayout.CENTER);
        statusPanel.add(scoreLabel, BorderLayout.NORTH);
        statusPanel.add(controlsPanel, BorderLayout.SOUTH);

        return statusPanel;
    }

    private JPanel createGameControlsPanel() {
        JPanel controlsPanel = new JPanel(new FlowLayout());
        controlsPanel.setBackground(BACKGROUND_COLOR);
        controlsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Game mode selection
        JLabel modeLabel = new JLabel("Mode:");
        modeLabel.setForeground(TEXT_COLOR);
        modeLabel.setFont(new Font("Arial", Font.BOLD, 12));

        gameModeCombo = new JComboBox<>(new String[]{"Player vs Player", "Player vs AI"});
        gameModeCombo.setBackground(BUTTON_COLOR);
        gameModeCombo.setForeground(TEXT_COLOR);
        gameModeCombo.addActionListener(e -> {
            isAIMode = gameModeCombo.getSelectedIndex() == 1;
            difficultyCombo.setEnabled(isAIMode);
            resetGame();
        });

        // AI difficulty selection
        JLabel diffLabel = new JLabel("Difficulty:");
        diffLabel.setForeground(TEXT_COLOR);
        diffLabel.setFont(new Font("Arial", Font.BOLD, 12));

        difficultyCombo = new JComboBox<>(AILevel.values());
        difficultyCombo.setBackground(BUTTON_COLOR);
        difficultyCombo.setForeground(TEXT_COLOR);
        difficultyCombo.setEnabled(false);
        difficultyCombo.addActionListener(e -> {
            aiLevel = (AILevel) difficultyCombo.getSelectedItem();
        });

        controlsPanel.add(modeLabel);
        controlsPanel.add(gameModeCombo);
        controlsPanel.add(Box.createHorizontalStrut(20));
        controlsPanel.add(diffLabel);
        controlsPanel.add(difficultyCombo);

        return controlsPanel;
    }

    private JPanel createBoardPanel() {
        JPanel boardPanel = new JPanel(new GridLayout(BOARD_SIZE, BOARD_SIZE, 5, 5));
        boardPanel.setBackground(BACKGROUND_COLOR);
        boardPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                JButton button = createGameButton(row, col);
                buttons[row][col] = button;
                boardPanel.add(button);
            }
        }

        return boardPanel;
    }

    private JButton createGameButton(int row, int col) {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(120, 120));
        button.setFont(BUTTON_FONT);
        button.setBackground(BUTTON_COLOR);
        button.setForeground(TEXT_COLOR);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.addActionListener(this);
        button.putClientProperty("row", row);
        button.putClientProperty("col", col);

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button.getText().isEmpty() && gameActive) {
                    button.setBackground(BUTTON_HOVER_COLOR);
                }
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button.getText().isEmpty() && gameActive) {
                    button.setBackground(BUTTON_COLOR);
                }
            }
        });

        return button;
    }

    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.setBackground(BACKGROUND_COLOR);

        resetButton = new JButton("New Game");
        resetButton.setFont(STATUS_FONT);
        resetButton.setBackground(new Color(130, 88, 159));
        resetButton.setForeground(TEXT_COLOR);
        resetButton.setFocusPainted(false);
        resetButton.setBorder(BorderFactory.createRaisedBevelBorder());
        resetButton.setPreferredSize(new Dimension(120, 40));
        resetButton.addActionListener(event -> resetGame());

        controlPanel.add(resetButton);
        return controlPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameActive) return;

        JButton clickedButton = (JButton) e.getSource();
        int row = (Integer) clickedButton.getClientProperty("row");
        int col = (Integer) clickedButton.getClientProperty("col");

        // Check if the cell is already occupied
        if (board[row][col] != ' ') {
            playErrorSound();
            return;
        }

        // Make the human move
        makeMove(row, col, clickedButton);
        playMoveSound();

        // Check for win or draw after human move
        if (checkWin()) {
            handleGameEnd(currentPlayer + " Wins!");
            return;
        } else if (isBoardFull()) {
            handleGameEnd("It's a Draw!");
            return;
        }

        // Switch players
        currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';

        // If AI mode and it's AI's turn, make AI move
        if (isAIMode && currentPlayer == aiPlayer) {
            statusLabel.setText("AI is thinking...");

            // Add a small delay for better UX
            Timer aiTimer = new Timer(500, aiEvent -> {
                makeAIMove();

                // Check for win or draw after AI move
                if (checkWin()) {
                    handleGameEnd("AI Wins!");
                } else if (isBoardFull()) {
                    handleGameEnd("It's a Draw!");
                } else {
                    currentPlayer = humanPlayer;
                    statusLabel.setText("Your Turn");
                }
            });
            aiTimer.setRepeats(false);
            aiTimer.start();
        } else {
            statusLabel.setText("Player " + currentPlayer + "'s Turn");
        }
    }

    private void makeMove(int row, int col, JButton button) {
        board[row][col] = currentPlayer;
        button.setText(String.valueOf(currentPlayer));

        // Set color based on player
        if (currentPlayer == 'X') {
            button.setForeground(X_COLOR);
        } else {
            button.setForeground(O_COLOR);
        }

        button.setBackground(BUTTON_COLOR);
    }

    private boolean checkWin() {
        // Check rows
        for (int row = 0; row < BOARD_SIZE; row++) {
            if (board[row][0] == currentPlayer &&
                board[row][1] == currentPlayer &&
                board[row][2] == currentPlayer) {
                return true;
            }
        }

        // Check columns
        for (int col = 0; col < BOARD_SIZE; col++) {
            if (board[0][col] == currentPlayer &&
                board[1][col] == currentPlayer &&
                board[2][col] == currentPlayer) {
                return true;
            }
        }

        // Check diagonals
        if (board[0][0] == currentPlayer &&
            board[1][1] == currentPlayer &&
            board[2][2] == currentPlayer) {
            return true;
        }

        if (board[0][2] == currentPlayer &&
            board[1][1] == currentPlayer &&
            board[2][0] == currentPlayer) {
            return true;
        }

        return false;
    }

    private boolean isBoardFull() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (board[row][col] == ' ') {
                    return false;
                }
            }
        }
        return true;
    }

    private void highlightWinningCells() {
        Color winColor = new Color(46, 204, 113);
        winningButtons.clear();

        // Check rows
        for (int row = 0; row < BOARD_SIZE; row++) {
            if (board[row][0] == currentPlayer &&
                board[row][1] == currentPlayer &&
                board[row][2] == currentPlayer) {
                for (int col = 0; col < BOARD_SIZE; col++) {
                    buttons[row][col].setBackground(winColor);
                    winningButtons.add(buttons[row][col]);
                }
                return;
            }
        }

        // Check columns
        for (int col = 0; col < BOARD_SIZE; col++) {
            if (board[0][col] == currentPlayer &&
                board[1][col] == currentPlayer &&
                board[2][col] == currentPlayer) {
                for (int row = 0; row < BOARD_SIZE; row++) {
                    buttons[row][col].setBackground(winColor);
                    winningButtons.add(buttons[row][col]);
                }
                return;
            }
        }

        // Check main diagonal
        if (board[0][0] == currentPlayer &&
            board[1][1] == currentPlayer &&
            board[2][2] == currentPlayer) {
            for (int i = 0; i < BOARD_SIZE; i++) {
                buttons[i][i].setBackground(winColor);
                winningButtons.add(buttons[i][i]);
            }
            return;
        }

        // Check anti-diagonal
        if (board[0][2] == currentPlayer &&
            board[1][1] == currentPlayer &&
            board[2][0] == currentPlayer) {
            for (int i = 0; i < BOARD_SIZE; i++) {
                buttons[i][2-i].setBackground(winColor);
                winningButtons.add(buttons[i][2-i]);
            }
        }
    }

    // AI Logic
    private void makeAIMove() {
        int[] move = getBestMove();
        if (move != null) {
            makeMove(move[0], move[1], buttons[move[0]][move[1]]);
            playMoveSound();
        }
    }

    private int[] getBestMove() {
        switch (aiLevel) {
            case EASY:
                return getRandomMove();
            case MEDIUM:
                return getMediumMove();
            case HARD:
                return getHardMove();
            default:
                return getRandomMove();
        }
    }

    private int[] getRandomMove() {
        List<int[]> availableMoves = getAvailableMoves();
        if (availableMoves.isEmpty()) return null;

        Random random = new Random();
        return availableMoves.get(random.nextInt(availableMoves.size()));
    }

    private int[] getMediumMove() {
        // 70% chance to play optimally, 30% chance to play randomly
        Random random = new Random();
        if (random.nextDouble() < 0.7) {
            return getHardMove();
        } else {
            return getRandomMove();
        }
    }

    private int[] getHardMove() {
        // Try to win first
        int[] winMove = findWinningMove(aiPlayer);
        if (winMove != null) return winMove;

        // Block opponent from winning
        int[] blockMove = findWinningMove(humanPlayer);
        if (blockMove != null) return blockMove;

        // Take center if available
        if (board[1][1] == ' ') {
            return new int[]{1, 1};
        }

        // Take corners
        int[][] corners = {{0,0}, {0,2}, {2,0}, {2,2}};
        for (int[] corner : corners) {
            if (board[corner[0]][corner[1]] == ' ') {
                return corner;
            }
        }

        // Take any available move
        return getRandomMove();
    }

    private int[] findWinningMove(char player) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (board[row][col] == ' ') {
                    // Try this move
                    board[row][col] = player;
                    boolean isWin = checkWinForPlayer(player);
                    board[row][col] = ' '; // Undo move

                    if (isWin) {
                        return new int[]{row, col};
                    }
                }
            }
        }
        return null;
    }

    private boolean checkWinForPlayer(char player) {
        // Check rows
        for (int row = 0; row < BOARD_SIZE; row++) {
            if (board[row][0] == player && board[row][1] == player && board[row][2] == player) {
                return true;
            }
        }

        // Check columns
        for (int col = 0; col < BOARD_SIZE; col++) {
            if (board[0][col] == player && board[1][col] == player && board[2][col] == player) {
                return true;
            }
        }

        // Check diagonals
        if (board[0][0] == player && board[1][1] == player && board[2][2] == player) {
            return true;
        }
        if (board[0][2] == player && board[1][1] == player && board[2][0] == player) {
            return true;
        }

        return false;
    }

    private List<int[]> getAvailableMoves() {
        List<int[]> moves = new ArrayList<>();
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (board[row][col] == ' ') {
                    moves.add(new int[]{row, col});
                }
            }
        }
        return moves;
    }

    private void handleGameEnd(String message) {
        statusLabel.setText(message);
        gameActive = false;

        // Update scores
        if (message.contains("X Wins") || (isAIMode && message.contains("Your") && message.contains("Win"))) {
            playerXScore++;
        } else if (message.contains("O Wins") || message.contains("AI Wins")) {
            playerOScore++;
        } else if (message.contains("Draw")) {
            drawCount++;
        }

        updateScoreDisplay();
        highlightWinningCells();
        playWinSound();
        startWinAnimation();
    }

    private void updateScoreDisplay() {
        if (isAIMode) {
            scoreLabel.setText("You: " + playerXScore + "  |  AI: " + playerOScore + "  |  Draws: " + drawCount);
        } else {
            scoreLabel.setText("X: " + playerXScore + "  |  O: " + playerOScore + "  |  Draws: " + drawCount);
        }
    }

    private void startWinAnimation() {
        if (winningButtons.isEmpty()) return;

        animationTimer = new Timer(200, e -> {
            for (JButton button : winningButtons) {
                Color currentBg = button.getBackground();
                if (currentBg.equals(new Color(46, 204, 113))) {
                    button.setBackground(new Color(39, 174, 96));
                } else {
                    button.setBackground(new Color(46, 204, 113));
                }
            }
        });
        animationTimer.start();

        // Stop animation after 3 seconds
        Timer stopTimer = new Timer(3000, e -> {
            if (animationTimer != null) {
                animationTimer.stop();
            }
        });
        stopTimer.setRepeats(false);
        stopTimer.start();
    }

    // Sound effects (simple beep sounds)
    private void playMoveSound() {
        Toolkit.getDefaultToolkit().beep();
    }

    private void playWinSound() {
        // Play a series of beeps for win
        Timer soundTimer = new Timer(100, null);
        final int[] beepCount = {0};
        soundTimer.addActionListener(e -> {
            Toolkit.getDefaultToolkit().beep();
            beepCount[0]++;
            if (beepCount[0] >= 3) {
                soundTimer.stop();
            }
        });
        soundTimer.start();
    }

    private void playErrorSound() {
        // Lower pitch beep for error (simulated with double beep)
        Toolkit.getDefaultToolkit().beep();
        Timer errorTimer = new Timer(50, e -> Toolkit.getDefaultToolkit().beep());
        errorTimer.setRepeats(false);
        errorTimer.start();
    }

    private void resetGame() {
        // Stop any running animations
        if (animationTimer != null) {
            animationTimer.stop();
        }
        winningButtons.clear();

        // Reset game state
        currentPlayer = 'X';
        gameActive = true;

        if (isAIMode) {
            statusLabel.setText("Your Turn");
        } else {
            statusLabel.setText("Player X's Turn");
        }

        // Reset board array
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                board[row][col] = ' ';
            }
        }

        // Reset button appearance
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                buttons[row][col].setText("");
                buttons[row][col].setBackground(BUTTON_COLOR);
                buttons[row][col].setForeground(TEXT_COLOR);
            }
        }
    }

    public static void main(String[] args) {
        // Create and show the game
        SwingUtilities.invokeLater(() -> {
            new Main().setVisible(true);
        });
    }
}