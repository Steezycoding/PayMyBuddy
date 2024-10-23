package com.paymybuddy.webapp.utils;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Alert {
	private AlertType type;
	private String message;

	public enum AlertType {
		DANGER,
		INFO,
		SUCCESS,
		WARNING
	}
}
