package tictactoe2;

public class ServerService {
    // 메모리 DB
    private String[] shouldPlayRSP = new String[2];
    private int[] clientRSP = new int[2];     // 0: 가위, 1: 바위, 2: 보
    private final int[][] resultRSP = {       // 가위바위보 결과 테이블(3: 무승부, 1: 승자, 2: 패자)
            {3, 2, 1},
            {1, 3, 2},
            {2, 1, 3}
    };
    public int nowTurn = 0;
    private TicTacToe ticTacToe = new TicTacToeImpl();

    // 싱글톤 패턴
    private static ServerService instance = new ServerService();

    private ServerService() {
        // 생성자는 외부에서 호출못하게 private 으로 지정해야 한다.
    }

    public static ServerService getInstance() {
        return instance;
    }

    /**
     * 가위바위보의 진행 여부 판단 메소드
     * 클라이언트 호출 메소드: @1
     * @param id
     * @param message
     * @return  true: 가위바위보 진행, false: 가위바위보 진행 안함
     */
    public synchronized boolean shouldPlayRSP(int id, String message) {
        shouldPlayRSP[id] = message;

        notify();
        while(shouldPlayRSP[0] == null || shouldPlayRSP[1] == null) {
            try {
                wait();
            } catch(InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        return "1".equals(shouldPlayRSP[0]) && "1".equals(shouldPlayRSP[1]);
    }

    /**
     * 가위바위보의 승자를 판단하는 메소드
     * 클라이언트 호출 메소드: @2
     * @param message   1: 가위, 2: 바위, 3: 보
     * @return 1: 승자, 2: 패자, 3: 무승부
     */
    public int playRSP(int id, String message) {
        if(clientRSP[0] != 0 && clientRSP[1] != 0) {
            clientRSP[0] = 0;
            clientRSP[1] = 0;
        }

        clientRSP[id] = Integer.parseInt(message);

        synchronized(this) {
            notifyAll();
            while(clientRSP[0] == 0 || clientRSP[1] == 0) {
                try {
                    wait();
                } catch(InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        int resultOfGame = resultRSP[clientRSP[id] - 1][clientRSP[(id + 1) % 2] - 1];
        if(resultOfGame == 1) nowTurn = id;
        return resultOfGame;
    }

    public boolean playTicTacToe(int id, String message) {   // 클라이언트 호출 메소드: @3
        String[] split = message.split(" ", 2);
        int x = Integer.parseInt(split[0]);
        int y = Integer.parseInt(split[1]);

        if(getNowTurn() != id) return false;  // 잘못된 차례

        try {
            ticTacToe.putStone(getNowTurn() + 1, x, y);
        } catch(RuntimeException e) {
            return false;
        }

        if(!ticTacToe.isFinished())
            setNowTurn(getNowTurn() == 1 ? 0 : 1);   // 차례 변경

        return true;
    }

    public String isFinished(int id) {
        if(ticTacToe.isFinished()) {
            if(ticTacToe.getWinner() == -1) return "3";    // 무승부
            else if(id == ticTacToe.getWinner() - 1) return "1";    // 승리
            else return "2";        // 패배
        } else return "0";            // 게임 진행 중
    }

    public String getBoard() {
        return ticTacToe.getBoard();
    }

     public int getNowTurn() {
         return nowTurn;
     }

     private void setNowTurn(int nowTurn) {
         this.nowTurn = nowTurn;
     }
}
