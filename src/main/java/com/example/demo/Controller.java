package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class Controller {

    @Autowired
    DatabaseLocal databaseLocal;

    @GetMapping("/history")
    public ResponseEntity<List<ChatMessage>> getHistory(@RequestParam(value = "roomNumber", required = false) int roomNumber,@RequestParam("currentState") int currentState) throws Exception {
        if(databaseLocal.data.containsKey(roomNumber)){
            List<ChatMessage> chatMessageList=databaseLocal.data.get(roomNumber);
            if(chatMessageList.size() < currentState){
                throw new Exception();
            }else {
                if (chatMessageList.size() == currentState){
                    return ResponseEntity.ok(null);
                }else {
                    List<ChatMessage> temp = new ArrayList<>();
                    for (int i = currentState; i < chatMessageList.size(); i++) {
                        temp.add(chatMessageList.get(i));
                        return ResponseEntity.ok(temp);
                    }

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
}
