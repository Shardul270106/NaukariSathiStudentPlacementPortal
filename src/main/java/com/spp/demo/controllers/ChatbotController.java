package com.spp.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.spp.demo.service.ChatbotService;

import java.util.Map;

@RestController
@RequestMapping("/ai")
@CrossOrigin("*")
public class ChatbotController {

    @Autowired
    private ChatbotService chatbotService;

    @PostMapping("/chat")
    public String chat(@RequestBody Map<String,String> request){

        String question = request.getOrDefault("message", "");

        if(question.isBlank()){
            return "Please ask a valid question.";
        }

        return chatbotService.askAI(question);
    }
}