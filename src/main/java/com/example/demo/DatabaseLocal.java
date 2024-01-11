package com.example.demo;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Scope("singleton")
public class DatabaseLocal {

    public Map<Integer, List<ChatMessage>> data= new HashMap<>();
}
