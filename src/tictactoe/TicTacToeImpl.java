package tictactoe;

import java.awt.event.WindowFocusListener;

/**
 * TicTacToe 인터페이스를 구현한 구현체 클래스
 * @author : 김승규
 */
public class TicTacToeImpl implements TicTacToe {
    private final int SIZE = 3;
    private String[][] board = new String[SIZE][SIZE];
    private int count = 0;
    private int lastPlayer = 0;
    private boolean isFinished = false;

    /**
     * 게임이 끝났는지 확인한다.
     */
    private void checkFinish() {
        if(count == SIZE * SIZE) {
            isFinished = true;      // 보드판이 꽉 찬 경우
        } else if(board[0][0] != null && board[0][0].equals(board[1][1]) && board[0][0].equals(board[2][2]) ||
                board[0][2] != null && board[0][2].equals(board[1][1]) && board[0][2].equals(board[2][0])) {
            isFinished = true;      // 대각선 승리
        } else {
            for(int i = 0; i < SIZE; ++i) {
                if(board[i][0] != null && board[i][0].equals(board[i][1]) && board[i][0].equals(board[i][2]) ||
                        board[0][i] != null && board[0][i].equals(board[1][i]) && board[0][i].equals(board[2][i])) {
                    isFinished = true;  // 직선 승리
                    break;
                }
            }
        }
    }

    @Override
    public void putStone(int userId, int x, int y) {
        if(isFinished()) throw new RuntimeException("이미 끝난 게임입니다.");
        if(lastPlayer == userId) throw new RuntimeException("잘못된 차례입니다.");
        if(x < 0 || x >= SIZE || y < 0 || y >= SIZE) throw new RuntimeException("잘못된 좌표입니다.");
        if(board[x][y] != null) throw new RuntimeException("이미 돌이 놓여진 자리입니다.");

        // 정상 작동
        ++count;
        lastPlayer = userId;
        if(board[x][y] == null) board[x][y] = userId == 1 ? "O" : "X";
        checkFinish();
    }

    @Override
    public String getBoard() {
        StringBuilder printBoard = new StringBuilder();
        for(int i = 0; i < SIZE; ++i) {
            for(int j = 0; j < SIZE; ++j) {
                printBoard.append("[").append(board[i][j] == null ? " " : board[i][j]).append("] ");
            }
            printBoard.append("-");
        }
        return printBoard.toString();
    }

    @Override
    public boolean isFinished() {
        return isFinished;
    }

    @Override
    public int getWinner() {
        if(isFinished()) {
            if(count == SIZE * SIZE) return -1;
            else return lastPlayer;
        }
        return 0;
    }

    /**
     * 테스트용
     */
    public static void main(String[] args) {
        TicTacToe ticTacToe = new TicTacToeImpl();

        ticTacToe.putStone(1, 0, 0);
        if(extracted(ticTacToe)) return;

        ticTacToe.putStone(2, 0, 1);
        if(extracted(ticTacToe)) return;


        ticTacToe.putStone(1, 0, 2);
        if(extracted(ticTacToe)) return;

        ticTacToe.putStone(2, 1, 0);
        if(extracted(ticTacToe)) return;


        ticTacToe.putStone(1, 1, 1);
        if(extracted(ticTacToe)) return;


        ticTacToe.putStone(2, 1, 2);
        if(extracted(ticTacToe)) return;



        ticTacToe.putStone(1, 2, 1);
//        ticTacToe.putStone(1, 2, 0);

        if(extracted(ticTacToe)) return;


        ticTacToe.putStone(2, 2, 0);
//        ticTacToe.putStone(2, 2, 1);
        if(extracted(ticTacToe)) return;


        ticTacToe.putStone(1, 2, 2);
        if(extracted(ticTacToe)) return;


        ticTacToe.putStone(2, 1, 1);
        if(extracted(ticTacToe)) return;

    }

    /**
     * 테스트용
     */
    private static boolean extracted(TicTacToe ticTacToe) {
        System.out.println(ticTacToe.getBoard());
        if(ticTacToe.isFinished()) {
            System.out.println("게임이 끝났습니다.");
            if(ticTacToe.getWinner() != -1) System.out.println("승자는 " + ticTacToe.getWinner() + "입니다.");
            else System.out.println("무승부입니다.");
            return true;
        }
        return false;
    }
}
