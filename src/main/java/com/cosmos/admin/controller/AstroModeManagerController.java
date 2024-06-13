package com.cosmos.admin.controller;

import com.cosmos.astrologer.dto.AstrologerDto;
import com.cosmos.astromode.dto.AstroModeDto;
import com.cosmos.astromode.service.IAstroModeService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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



}
