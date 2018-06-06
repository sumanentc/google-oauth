package com.fractal.oauth;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

/**
 * Created by suman.das on 11/1/16.
 */
@Controller
public class OauthController {
    @RequestMapping("/hello")
    public void time(Model model, @RequestParam(value = "name",required = false) String name){
        model.addAttribute("date" ,  new Date());
        if(name != null){
            model.addAttribute("name","Welcome " + name);
        }

    }
}
