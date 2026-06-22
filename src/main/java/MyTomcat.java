import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class MyTomcat {
    public static void main(String[] args){
        // try-catch 문으로 try 블록이 끝날 때 자바가 알아서 serverSocket.close()를 호출해 포트를 OS 반환
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            // "ServerSocket serverSocket = new ServerSocket(8080)"은 자바 OS에게 8080번 포트 권한을 요청하는 코드
            // OS 커널은 8080번 포트를 다른 프로그램이 쓰지 못하도록 락을 걸어 선점하고, 이 포트로 들어오는 TCP 연결 요청(SYN 패킷)을 임시로 보관할 '대기 큐'를 컴퓨터 메모리에 만듬
            System.out.println("2단계: 8080 포트에서 요청을 기다림");

            // 브라우저가 접속할 때까지 여기서 프로그램이 잠시 멈춤 (Blocking)
            // 브라우저 주소창에 localhost:8080을 입력하면, 브라우저와 OS 커널 간에 TCP 3-Way Handshake가 일어남
            // OS가 Handshake가 끝나면 큐에 집어넣음
            // accept()는 큐를 지켜보다가 요청이 들어오는 순간 Blocking을 풀고 요청을 자바의 Socket 객체로 만들어서 반환
            Socket clientSocket = serverSocket.accept();

            // 브라우저가 네트워크를 통해 보내는 바이트 데이터 읽기
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String line;

            // HTTP 프로토콜 규격상, 브라우저가 보내는 요청 메시지는 헤더 뒤에 빈줄을 한 줄 넣음 -> "!line.isEmpty()" 조건으로 Blocking 막음
            while((line = reader.readLine()) != null && !line.isEmpty()) {
                System.out.println(line);
            }

            // 브라우저에게 답장을 보내기 위한 Output Stream 개설
            DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());

            // 1. 브라우저 화면에 띄울 진짜 알맹이(HTML 바디) 준비
            String htmlBody = "<h1>Hello from MyTomcat!</h1><p>Success to connect!</p>";
            byte[] bodyBytes = htmlBody.getBytes("UTF-8"); // 영문/한글 깨짐 방지를 위해 바이트 배열로 변환

            System.out.println("\n--- ✉️ [서버가 브라우저에게 보내는 정식 HTTP 응답] ---");

            // 2. HTTP 응답 규격 약속 지켜서 보내기
            // (1) 상태 라인: "HTTP 1.1 버전을 쓸 거고, 네 요청은 성공(200 OK)했어!"
            out.writeBytes("HTTP/1.1 200 OK \r\n");

            // (2) 헤더 정보: "내가 보내는 데이터는 HTML 텍스트고, 인코딩은 UTF-8이야."
            out.writeBytes("Content-Type: text/html;charset=utf-8 \r\n");

            // (3) 헤더 정보: "내가 보낼 진짜 바디 데이터의 총 길이는 이만큼이야."
            out.writeBytes("Content-Length: " + bodyBytes.length + " \r\n");

            // (4) 빈 줄 : "내 헤더 설명은 끝났으니 이제부터 진짜 데이터(바디) 나간다!"
            out.writeBytes("\r\n");

            // (5) 바디 전송: 진짜 HTML 알맹이를 전선에 태워 보냅니다.
            out.write(bodyBytes, 0, bodyBytes.length);

            // 3. 전선에 남아있는 찌꺼기 데이터까지 쫙 밀어내기 (새로고침 반영)
            out.flush();
            System.out.println("[알림] 성공적으로 응답을 보냈습니다. 프로그램을 종료합니다.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
