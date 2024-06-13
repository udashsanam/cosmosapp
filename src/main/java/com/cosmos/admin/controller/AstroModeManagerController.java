package com.cosmos.admin.controller;

import com.cosmos.astrologer.dto.AstrologerDto;
import com.cosmos.astromode.dto.AstroModeDto;
import com.cosmos.astromode.service.IAstroModeService;
import com.cosmos.moderator.dto.ModeratorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/manage/astromode")
public class AstroModeManagerController {

    private final IAstroModeService iAstroModeService;

    public AstroModeManagerController(IAstroModeService iAstroModeService) {
        this.iAstroModeService = iAstroModeService;
    }

    @PostMapping(value = "", consumes = "application/json", produces = "application/json")
    public AstroModeDto save(@RequestBody AstroModeDto astrologerDto) {
        return iAstroModeService.saveAstroMode(astrologerDto);
    }


    @PutMapping(value = "/{astromodeid}", consumes = "application/json", produces = "application/json")
    public AstroModeDto update(@PathVariable("astromodeid") Long astroModeId, @RequestBody AstroModeDto astroModeDto) {
        return iAstroModeService.updateModerator(astroModeId,astroModeDto);
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public AstroModeDto getById(@PathVariable Long id) {
        return iAstroModeService.findModeratorById(id);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        iAstroModeService.deleteModeratorById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
