import java.net.ServerSocket;
import java.net.Socket;

public class MyTomcat {
    public static void main(String[] args){
        // try-catch 문으로 try 블록이 끝날 때 자바가 알아서 serverSocket.close()를 호출해 포트를 OS 반환
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            // "ServerSocket serverSocket = new ServerSocket(8080)"은 자바 OS에게 8080번 포트 권한을 요청하는 코드
            // OS 커널은 8080번 포트를 다른 프로그램이 쓰지 못하도록 락을 걸어 선점하고, 이 포트로 들어오는 TCP 연결 요청(SYN 패킷)을 임시로 보관할 '대기 큐'를 컴퓨터 메모리에 만듬
            System.out.println("1단계: 서버가 8080 포트에서 대기 중입니다.");

            // 브라우저가 접속할 때까지 여기서 프로그램이 잠시 멈춤 (Blocking)
            // 브라우저 주소창에 localhost:8080을 입력하면, 브라우저와 OS 커널 간에 TCP 3-Way Handshake가 일어남
            // OS가 Handshake가 끝나면 큐에 집어넣음
            // accept()는 큐를 지켜보다가 요청이 들어오는 순간 Blocking을 풀고 요청을 자바의 Socket 객체로 만들어서 반환
            Socket clientSocket = serverSocket.accept();
            System.out.println("브라우저가 서버에 접속했습니다.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
