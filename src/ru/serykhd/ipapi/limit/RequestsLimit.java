package ru.serykhd.ipapi.limit;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@ToString
@Deprecated
public class RequestsLimit {
	
	private final int available;
	private final int expire;
	
}
