package io.sse.serversentevent.controller;

import io.sse.serversentevent.RsData;
import io.sse.serversentevent.SseEmitters;
import io.sse.serversentevent.vo.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Controller
@Slf4j
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
    private final SseEmitters sseEmitters;

    private List<ChatMessage> chatMessages = new ArrayList<>();

    public record WriteMessageRequest(String authorName, String content) {
    }

    public record WriteMessageResponse(long id) {
    }

    @GetMapping("/room")
    public String showRoom() {
        return "chat/room";
    }

    @PostMapping("/write-message")
    @ResponseBody
    public RsData<WriteMessageResponse> writeMessage(@RequestBody WriteMessageRequest req) {
        ChatMessage message = new ChatMessage(req.authorName(), req.content());

        chatMessages.add(message);

        sseEmitters.noti("chat__messageAdded");

        return new RsData<>(
                "S-1",
                "메세지가 작성되었습니다.",
                new WriteMessageResponse(message.getId())
        );
    }

    public record MessagesRequest(Long fromId) {
    }

    public record MessagesResponse(List<ChatMessage> messages, long count) {
    }

    @GetMapping("/messages")
    @ResponseBody
    public RsData<MessagesResponse> messages(MessagesRequest req) {
        List<ChatMessage> messages = chatMessages;

        // 번호가 입력되었다면
        if (req.fromId != null) {
            // 해당 번호의 채팅메세지가 전체 리스트에서의 배열인덱스 번호를 구한다.
            // 없다면 -1
            int index = IntStream.range(0, messages.size())
                    .filter(i -> chatMessages.get(i).getId() == req.fromId)
                    .findFirst()
                    .orElse(-1);


            if (index != -1) {
                // 만약에 index가 있다면, 0번 부터 index 번 까지 제거한 리스트를 만든다.
                messages = messages.subList(index + 1, messages.size());
            }
        }

        return new RsData<>(
                "S-1",
                "성공",
                new MessagesResponse(messages, messages.size())
        );
    }
}