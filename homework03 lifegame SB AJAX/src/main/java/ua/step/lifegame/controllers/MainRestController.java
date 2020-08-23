package ua.step.lifegame.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import ua.step.lifegame.beans.Cell;
import ua.step.lifegame.beans.LifeLogic;
import ua.step.lifegame.beans.ResponseWithInfo;

import java.io.*;

@RestController
public class MainRestController {

    private LifeLogic lifeLogic;
    
    @Autowired
    public void setLifeLogic (LifeLogic lifeLogic) {
    	this.lifeLogic = lifeLogic;
    }
    

    @PostMapping("/click")
    public ResponseEntity<ResponseWithInfo> catchLeftClick(@RequestBody Cell cell) {
        if (lifeLogic.getCell(cell) == 0) {
            lifeLogic.setCell(cell);
        } else {
            lifeLogic.removeCell(cell);
        }
        return new ResponseEntity<>(prepareResponse(), HttpStatus.OK);
    }

    @PostMapping("/move")
    public ResponseEntity<ResponseWithInfo> catchMove(@RequestBody Cell cell) {
        lifeLogic.moveAll(cell.getX(), cell.getY());
        return new ResponseEntity<>(prepareResponse(), HttpStatus.OK);
    }

    @PostMapping("/load")
    public ResponseEntity<ResponseWithInfo> catchLoad(@RequestParam("file") MultipartFile file){
        lifeLogic.clearField();
        lifeLogic.loadFile(file);   
        return new ResponseEntity<>(prepareResponse(), HttpStatus.OK);
    }

    @GetMapping("/getData")
    public ResponseEntity<ResponseWithInfo> catchGetData() {
        return new ResponseEntity<>(prepareResponse(), HttpStatus.OK);
    }

    @GetMapping("/step")
    public ResponseEntity<ResponseWithInfo> catchStep() {
        lifeLogic.next();
        return new ResponseEntity<>(prepareResponse(), HttpStatus.OK);
    }

    @GetMapping("/clear")
    public ResponseEntity<ResponseWithInfo> catchClear() {
        lifeLogic.clearField();
        return new ResponseEntity<>(prepareResponse(), HttpStatus.OK);
    }

    @GetMapping("/save")
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
