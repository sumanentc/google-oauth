package com.fractal.oauth;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by suman.das on 11/4/16.
 */
@Controller
public class RedirectController {
    @RequestMapping("/response")
    public String sendResponse(Model model){
        String url = (String)model.asMap().get("url");
        return url;

    }
}
