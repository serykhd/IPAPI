package ru.serykhd.ipapi.error;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class IPAPIError {
	
	private String status;
	private String message;
	private String query;

}
