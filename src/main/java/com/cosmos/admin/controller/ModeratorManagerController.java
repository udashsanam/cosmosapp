package com.cosmos.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cosmos.moderator.dto.ModeratorDto;
import com.cosmos.moderator.service.impl.ModeratorServiceImpl;

@RestController
@RequestMapping("/api/admin/manage/moderators")
public class ModeratorManagerController {
	@Autowired
	private ModeratorServiceImpl moderatorService;

	@GetMapping(value = "", produces = "application/json")
	public List<ModeratorDto> getModerators() {
		return moderatorService.findAllModerator();
	}

	@PostMapping(value = "", consumes = "application/json", produces = "application/json")
	public ModeratorDto save(@RequestBody ModeratorDto moderatorDto) {
		return moderatorService.addModerator(moderatorDto);
	}

	@PutMapping(value = "/{moderatorId}", consumes = "application/json", produces = "application/json")
	public ModeratorDto update(@PathVariable Long moderatorId, @RequestBody ModeratorDto moderatorDto) {
		return moderatorService.updateModerator(moderatorId,moderatorDto);
	}
	
	@GetMapping(value = "/{id}", produces = "application/json")
	public ModeratorDto getById(@PathVariable Long id) {
		return moderatorService.findModeratorById(id);
	}
	
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id){
		moderatorService.deleteModeratorById(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
