package io.sse.serversentevent.service;

import com.google.gson.Gson;
import io.sse.serversentevent.repository.EmitterRepository;
import io.sse.serversentevent.vo.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SseService {

    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    private final EmitterRepository emitterRepository;


    public SseEmitter subscribe(Long userId, String lastEventId) {
        // 1
        String emitterId = userId + "_" + System.currentTimeMillis();

        // 2
        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));

        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));

        // 3
        // 503 에러를 방지하기 위한 더미 이벤트 전송
        sendToClient(emitter, emitterId, "{\"userId\":" + userId + "}");

        // 4
        // 클라이언트가 미수신한 Event 목록이 존재할 경우 전송하여 Event 유실을 예방
        if (!lastEventId.isEmpty()) {
            Map<String, Object> events = emitterRepository.findAllEventCacheStartWithId(String.valueOf(userId));
            events.entrySet().stream()
                    .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                    .forEach(entry -> sendToClient(emitter, entry.getKey(), entry.getValue()));
            //TODO: 유실된 데이터를 전송 후 eventCache를 삭제하는 로직이 필요할거같다.
        }

        return emitter;
    }

    // 3
    private void sendToClient(SseEmitter emitter, String emitterId, Object data) {
        try {
            Gson gson = new Gson();
            String jsonData = gson.toJson(data);
            emitter.send(SseEmitter.event()
                    .id(emitterId)
                    .name("sse")
                    .data(jsonData));
        } catch (IOException exception) {
            emitterRepository.deleteById(emitterId);
            emitter.completeWithError(exception);
//            throw new RuntimeException("연결 오류!");
        }
    }

    /**
     * 실제로 알림을 보내고 싶은 로직에서 send 메서드를 호출해주면 된다.
     * @param receiver 알림을 받는사람
     * @param sendData 전달할 데이터
     * @param content 알림 내용
     */
    public void send(String receiver, String sendData, String content) {
        Notification notification = createNotification(receiver, sendData, content);

        // 로그인 한 유저의 SseEmitter 모두 가져오기
        Map<String, SseEmitter> sseEmitters = emitterRepository.findAllStartWithById(receiver);
        sseEmitters.forEach(
                (key, emitter) -> {
                    // 데이터 캐시 저장(유실된 데이터 처리하기 위함)
                    emitterRepository.saveEventCache(key, notification);
                    // 데이터 전송
                    sendToClient(emitter, key, notification);
                }
        );
    }

    private Notification createNotification(String receiver, String sendData, String content) {
        return Notification.builder()
                .receiver(receiver)
                .sendData(sendData)
                .content(content)
                .build();
    }

    /*
    https://velog.io/@max9106/Spring-SSE-Server-Sent-Events%EB%A5%BC-%EC%9D%B4%EC%9A%A9%ED%95%9C-%EC%8B%A4%EC%8B%9C%EA%B0%84-%EC%95%8C%EB%A6%BC

    [코드의 1번 부분]
    subscribe()를 보면 id값을 ${user_id}_${System.currentTimeMillis()} 형태로 사용하는 것을 볼 수 있다. 이렇게 사용하는 이유가 Last-Event-ID 헤더와 상관이 있다.

    Last-Event-ID헤더는 클라이언트가 마지막으로 수신한 데이터의 id값을 의미한다고 했다.
    id값과 전송 데이터를 저장하고 있으면 이 값을 이용하여 유실된 데이터 전송을 다시 해줄 수 있다.
    하지만 만약 id값을 그대로 사용한다면 어떤 문제가 있을까?

    id값을 그대로 사용한다면 Last-Event-Id값이 의미가 없어진다.

    Last-Event-Id = 3

    {3, data1}
    {3, data3}
    {3, data2}

    => 어떤 데이터까지 제대로 전송되었는지 알 수 없다.
    데이터의 id값을 ${userId}_${System.currentTimeMillis()} 형태로 두면 데이터가 유실된 시점을 파악할 수 있으므로 저장된 key값 비교를 통해 유실된 데이터만 재전송 할 수 있게 된다.

    Last-Event-Id = 3_1631593143664

    {3_1631593143664, data1}
    {3_1831593143664, data3}
    {3_1731593143664, data2}

    => data1 까지 제대로 전송되었고, data2, data3을 다시 보내야한다.
    이런 이유로 인해 id값을 ${user_id}_${System.currentTimeMillis()}로 두는 것이다.

    ---

    [코드의 2번 부분]
    클라이언트의 sse연결 요청에 응답하기 위해서는 SseEmitter 객체를 만들어 반환해줘야한다. SseEmitter 객체를 만들 때 유효 시간을 줄 수 있다. 이때 주는 시간 만큼 sse 연결이 유지되고, 시간이 지나면 자동으로 클라이언트에서 재연결 요청을 보내게 된다.

    id를 key로, SseEmitter를 value로 저장해둔다. 그리고 SseEmitter의 시간 초과 및 네트워크 오류를 포함한 모든 이유로 비동기 요청이 정상 동작할 수 없다면 저장해둔 SseEmitter를 삭제한다.

    ---

    [코드의 3번 부분]
    연결 요청에 의해 SseEmitter가 생성되면 더미 데이터를 보내줘야한다. sse 연결이 이뤄진 후,
    하나의 데이터도 전송되지 않는다면 SseEmitter의 유효 시간이 끝나면 503응답이 발생하는 문제가 있다. 따라서 연결시 바로 더미 데이터를 한 번 보내준다.

    ---

    [코드의 4번 부분]
    1번 부분과 관련이 있는 부분이다. Last-Event-ID값이 헤더에 있는 경우, 저장된 데이터 캐시에서 id 값과 Last-Event-ID값을 통해 유실된 데이터들만 다시 보내준다.

     */



}