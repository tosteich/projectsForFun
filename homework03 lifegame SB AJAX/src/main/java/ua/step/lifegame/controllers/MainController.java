package ua.step.lifegame.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
	
	@GetMapping("/")
	public String goToPage () {
		return "redirect:lifelogic";
	}
	
	@GetMapping("/lifelogic")
	public String mainPage () {
		return "index";
	}

}
