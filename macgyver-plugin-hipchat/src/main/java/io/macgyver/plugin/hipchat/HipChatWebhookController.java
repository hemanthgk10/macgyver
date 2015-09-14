package io.macgyver.plugin.hipchat;

import io.macgyver.core.Kernel;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RequestMapping("/api/plugin/hipchat")
@Controller
public class HipChatWebhookController {

	
	@Autowired
	HipChatBot hipChatBot;
	
	ObjectMapper mapper = new ObjectMapper();
	
	@RequestMapping(value = "/webhook", method=RequestMethod.POST, produces = { "application/json" })
	@ResponseBody
	@PreAuthorize("permitAll")
	public String receiveWebhook(@RequestBody String json, HttpServletRequest request) throws IOException {

		System.out.println("<<"+json+">>");
		JsonNode n = mapper.readTree(json);
		
		hipChatBot.dispatch(n);
		
	
		return "{}";

	}
}
