package ua.step.weather.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ua.step.weather.beans.DaysController;

@Controller
public class MainController {
	
	private DaysController daysController;
	@Autowired
	public void setDaysController(DaysController daysController) {
		this.daysController = daysController;
	}

	@GetMapping("/")
	public String mainRedirect() {
		return "redirect:weather";
	}
	
	@GetMapping("weather")
	public String mainPageShow(Model model, String selection) {
		model.addAttribute("daysController", daysController);		
		return "index";
	}
	
	@GetMapping("selection")
	public String select(@RequestParam(required=true) String selection) {
		daysController.setSelection(selection);
		return "redirect:weather";
	}
	
    @GetMapping("weather/getData")
    @ResponseBody
    public ResponseEntity<Object> getData() {
        return new ResponseEntity<>(daysController.getSelectedDays().toArray(), HttpStatus.OK);
    }
	
}
