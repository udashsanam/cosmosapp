package com.cosmos.email.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmailElementsView {
    private boolean showNavbar;
    private boolean showBanner;
    private boolean showEmailBody;
    private boolean showEventCode;
    private boolean showMobileTicket;
    private boolean showUserDetails;
    private boolean showBillingDetails;
    private boolean showAccomodations;
}
