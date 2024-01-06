package tictactoe;

/**
 * TicTacToe 인터페이스
 *
 * @author 김승규
 */
public interface TicTacToe {
    /**
     * 입력된 좌표에 해당하는 돌을 놓는다.(userId -> 1: O, 2: X)
     * @param userId 유저 아이디(1 or 2)
     * @param x x좌표(0~2)
     * @param y y좌표(0~2)
     * @throws RuntimeException
     *      이미 끝난 게임인 경우,
     *      같은 유저가 두 번 연속 둘 경우,
     *      이미 돌이 놓여진 경우,
     *      잘못된 좌표인 경우
     */
    public void putStone(int userId, int x, int y);     // userId: 1 or 2, x: 0~2, y: 0~2

    /**
     * 현제 게임 상황을 반환한다.
     * @return 3x3 보드판을 문자열로 반환
     */
    public String getBoard();   // 3x3 보드판을 문자열로 반환

    /**
     * 게임이 끝났는지 여부를 반환한다.
     * @return 게임이 끝났는지 여부 반환
     */
    public boolean isFinished();    // 게임이 끝났는지 여부 반환

    /**
     * 게임의 승자를 반환한다.
     * 이 메소드는 isFinished() 메소드가 true일 때만 호출해야 한다.
     * 만약 isFinished() 메소드가 false일 때 이 메소드를 호출하면 0을 반환한다.
     * @return 게임의 승자 반환(1 or 2 or -1(무승부))
     */
    public int getWinner();    // 1 or 2 or -1(무승부)
}
