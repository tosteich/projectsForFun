package ua.step.lifegame.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

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
	@SendTo("/updatedField")
	public ResponseWithInfo catchGetData() throws Exception {
		return prepareResponse();
	}
	
	@MessageMapping("step")
	@SendTo("/updatedField")
    public ResponseWithInfo catchStep() throws Exception {
        lifeLogic.next();
        return prepareResponse();
    }
	
	@MessageMapping("click")
	@SendTo("/updatedField")
    public ResponseWithInfo catchLeftClick(Cell cell) {
        if (lifeLogic.getCell(cell) == 0) {
            lifeLogic.setCell(cell);
        } else {
            lifeLogic.removeCell(cell);
        }
        return prepareResponse();
    }
	
	@MessageMapping("move")
	@SendTo("/updatedField")
    public ResponseWithInfo catchMove(Cell cell) {
		lifeLogic.moveAll(cell.getX(), cell.getY());
        return prepareResponse();
    }
	
	@MessageMapping("clear")
	@SendTo("/updatedField")
    public ResponseWithInfo catchClear() {
		lifeLogic.clearField();
        return prepareResponse();
    }
	
	@PostMapping("/load")
	@ResponseBody
	public ResponseEntity<ResponseWithInfo> catchLoad(@RequestParam("file") MultipartFile file) {
		lifeLogic.clearField();
		lifeLogic.loadFile(file);
		return new ResponseEntity<>(prepareResponse(), HttpStatus.OK);
	}
	
    @GetMapping("/save")
    @ResponseBody
    public ResponseEntity<Object> saveAsFile() throws IOException  {
        InputStream is = new ByteArrayInputStream(lifeLogic.getPattern().getBytes());
        InputStreamResource resource = new InputStreamResource(is);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=\"save.lif\"");
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        return ResponseEntity.ok().headers(headers).contentLength(
                is.available()).contentType(MediaType.parseMediaType("application/txt")).body(resource);
    }

    private ResponseWithInfo prepareResponse() {
        int generation = lifeLogic.getCountPopulation();
        int liveCells = lifeLogic.getLiveCells();
        int isGameOver = lifeLogic.isGameOver()?1:0;
        Object [] cells = lifeLogic.getCells().toArray();
        return new ResponseWithInfo(cells, generation, liveCells, isGameOver);
    }

}
