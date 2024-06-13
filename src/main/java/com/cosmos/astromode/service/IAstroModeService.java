package com.cosmos.astromode.service;

import com.cosmos.astromode.dto.AstroModeDto;
import com.cosmos.user.dto.UserDto;
import com.cosmos.user.entity.User;

public interface IAstroModeService {

    AstroModeDto saveAstroMode(AstroModeDto astroModeDto);

    AstroModeDto updateModerator(Long astroModeId, AstroModeDto astroModeDto);

    AstroModeDto findModeratorById(Long id);

    void deleteModeratorById(Long id);
}
