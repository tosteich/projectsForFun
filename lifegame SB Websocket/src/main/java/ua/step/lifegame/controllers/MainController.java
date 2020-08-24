package ua.step.lifegame.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;

import java.io.IOException;
import java.security.Principal;

import ua.step.lifegame.beans.Cell;
import ua.step.lifegame.beans.LifeLogic;
import ua.step.lifegame.beans.ResponseWithInfo;

@Controller
public class MainController {

	private LifeLogic lifeLogic;

	@Autowired
	public void setLifeLogic(LifeLogic lifeLogic) {
		this.lifeLogic = lifeLogic;
	}

	@GetMapping("/")
	public String goToPage() {
		return "redirect:lifelogic";
	}

	@GetMapping("/lifelogic")
	public String mainPage() {
		return "index";
	}

	@MessageMapping("getdata")
	@SendToUser("/updatedField")
	public ResponseWithInfo catchGetData(Principal principal) throws Exception {
		return prepareResponse();
	}
	
	@MessageMapping("step")
	@SendToUser("/updatedField")
    public ResponseWithInfo catchStep() throws Exception {
        lifeLogic.next();
        return prepareResponse();
    }
	
	@MessageMapping("click")
	@SendToUser("/updatedField")
    public ResponseWithInfo catchLeftClick(Cell cell, Principal principal) {
        if (lifeLogic.getCell(cell) == 0) {
            lifeLogic.setCell(cell);
        } else {
            lifeLogic.removeCell(cell);
        }
        return prepareResponse();
    }
	
	@MessageMapping("move")
	@SendToUser("/updatedField")
    public ResponseWithInfo catchMove(Cell cell) {
		lifeLogic.moveAll(cell.getX(), cell.getY());
        return prepareResponse();
    }
	
	@MessageMapping("clear")
	@SendToUser("/updatedField")
    public ResponseWithInfo catchClear() {
		lifeLogic.clearField();
        return prepareResponse();
    }
	
	@MessageMapping("load")
	@SendToUser("/updatedField")
	public ResponseWithInfo catchLoad(String file) {
		lifeLogic.clearField();
		lifeLogic.loadFile(file);
		return prepareResponse();
	}
	
	@MessageMapping("save")
	@SendToUser("/save")
    public String saveAsFile() throws IOException  {
        return lifeLogic.getPattern();
    }
    
    private ResponseWithInfo prepareResponse() {
        int generation = lifeLogic.getCountPopulation();
        int liveCells = lifeLogic.getLiveCells();
        int isGameOver = lifeLogic.isGameOver()?1:0;
        Object [] cells = lifeLogic.getCells().toArray();
        return new ResponseWithInfo(cells, generation, liveCells, isGameOver);
    }

}
