package com.kongapi.controller;

import com.kongapi.kongapiclientsdk.model.User;
import com.kongapi.kongapiclientsdk.utils.SignUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/name")
public class userController {
    @GetMapping("/get")
    public String getNameByGet(String name){
        return "Get+"+name;
    }
    @PostMapping("/post")
    public String getusernameByPost(String name){
        return "Post+"+name;
    }
    @PostMapping("/user")
    public String getusernameByPost(@RequestBody User user, HttpServletRequest request){
        return "POST,"+ user.toString();
    }

}
