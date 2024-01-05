package tictactoe;

public interface TicTacToe {
    public void putStone(int userId, int x, int y);     // userId: 1 or 2, x: 0~2, y: 0~2
    public String getBoard();   // 3x3 보드판을 문자열로 반환
    public boolean isFinished();    // 게임이 끝났는지 여부 반환
    public int getWinner();    // 1 or 2 or -1(무승부)
}
