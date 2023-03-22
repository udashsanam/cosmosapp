package com.cosmos.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cosmos.astrologer.dto.AstrologerDto;
import com.cosmos.astrologer.service.impl.AstrologerServiceImpl;

@RestController
@RequestMapping("/api/admin/manage/astrologers")
public class AstrologerManagerController {

	@Autowired
	private AstrologerServiceImpl astrologerService;

	@GetMapping(value = "", produces = "application/json")
	public List<AstrologerDto> getAstrologers() {
		return astrologerService.findAllAstrologer();
	}

	@PostMapping(value = "", consumes = "application/json", produces = "application/json")
	public AstrologerDto save(@RequestBody AstrologerDto astrologerDto) {
		return astrologerService.addAstrologer(astrologerDto);
	}

	@PutMapping(value = "/{astrologerId}", consumes = "application/json", produces = "application/json")
	public AstrologerDto update(@PathVariable Long astrologerId, @RequestBody AstrologerDto astrologerDto){
		return astrologerService.updateAstrologer(astrologerId, astrologerDto);
	}
	
	@GetMapping(value = "/{id}", produces = "application/json")
	public AstrologerDto getById(@PathVariable Long id) {
		return astrologerService.findById(id);
	}
	
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id){
		astrologerService.deleteAstrologerById(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
