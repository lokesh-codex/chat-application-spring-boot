package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@Slf4j
@RequestMapping("/api/chat")
public class Controller {

    @Autowired
    DatabaseLocal databaseLocal;

    @GetMapping("/history")
    public ResponseEntity<List<ChatMessage>> getHistory(@RequestParam(value = "roomNumber", required = false) int roomNumber,@RequestParam("currentState") int currentState) throws Exception {
        if(databaseLocal.data.containsKey(roomNumber)){
            List<ChatMessage> chatMessageList=databaseLocal.data.get(roomNumber);

            if(chatMessageList.size() < currentState){
                return ResponseEntity.badRequest().body(null);
            }else {
                if (chatMessageList.size() == currentState){
                    return ResponseEntity.ok(null);
                }else {
                    List<ChatMessage> temp = new ArrayList<>();
                    for (int i = currentState; i < chatMessageList.size(); i++) {
                        temp.add(chatMessageList.get(i));

                    }
                    return ResponseEntity.ok(temp);

                }
            }

        }
        return ResponseEntity.ok(null);
    }

    @PostMapping("/send")
    public ResponseEntity<Void> sendMessage(@RequestParam int roomNumber, @RequestBody ChatMessage message) {
        databaseLocal.data.compute(roomNumber, (key, existingMessages) -> {
            if (existingMessages == null) {
                // If roomNumber doesn't exist, create a new list
                List<ChatMessage> newMessages = new ArrayList<>();
                newMessages.add(message);
                return newMessages;
            } else {
                // If roomNumber exists, append to the existing list
                existingMessages.add(message);
                return existingMessages;
            }
        });

        return ResponseEntity.ok().build();
    }

    @GetMapping("/subscribe")
    public Flux<ServerSentEvent<List<ChatMessage>>> subscribe(
            @RequestParam("roomNumber") int roomNumber,
            @RequestParam("currentState") int currentState) {

        AtomicInteger serverState = new AtomicInteger();
        log.info(Arrays.toString(databaseLocal.data.get(roomNumber).toArray()));
        return Flux.interval(Duration.ofSeconds(2))
                .delayElements(Duration.ofSeconds(1))  // Add a delay before the first event
                .map(sequence -> {
                    List<ChatMessage> chatMessageList = databaseLocal.data.get(roomNumber);
                    if (chatMessageList != null && chatMessageList.size() > serverState.get()) {
                        List<ChatMessage> newMessages = chatMessageList.subList(serverState.get(), chatMessageList.size());
                        serverState.set(chatMessageList.size());
                        return ServerSentEvent.<List<ChatMessage>>builder()
                                .event("message")
                                .data(newMessages)
                                .build();
                    } else {
                        return ServerSentEvent.<List<ChatMessage>>builder()
                                .event("ping")
                                .build();
                    }
                })
                .takeWhile(sequence -> databaseLocal.data.containsKey(roomNumber));
    }




}
