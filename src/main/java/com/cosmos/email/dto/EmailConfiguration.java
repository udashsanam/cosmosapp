package com.cosmos.email.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailConfiguration {
    private EmailSetup emailSetup;
    private EmailElementsView emailElementsView;
}
