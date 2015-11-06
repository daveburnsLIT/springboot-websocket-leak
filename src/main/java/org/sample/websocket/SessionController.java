package org.sample.websocket;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SessionController {

    @RequestMapping(value = "/hello", method=RequestMethod.GET)
    @ResponseBody
    public String getHello(HttpServletRequest request){
    	request.getSession();
    	return "hello";
    }
	
}
