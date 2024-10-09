package com.paymybuddy.webapp.utils;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Alert {
	private String type;
	private String message;
}
