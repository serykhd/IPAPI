package ru.serykhd.ipapi.fields;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum FieldType {
	
	CONTINENT(1024 * 1024),
	CONTINENT_CODE(1024 * 1024 * 2),
	
	COUNTRY(1),
	COUNTRY_CODE(2),
	
	REGION(4),
	REGION_NAME(8),
	
	CITY(16),
	DISTRICT(1024 * 512),
	
	ZIP(32),
	GPS_LATITUDE(64),
	GPS_LONGITUDE(128),
	
	TIMEZONE(256),
	OFFSET(1024 * 1024 * 32),
	CURRENCY(1024 * 1024 * 8),
	
	NET_ISP(512),
	NET_ORG(1024),
	NET_AS(1024 * 2),
	NET_ASNAME(1024 * 1024 * 4),
	NET_REVERSE(1024 * 4),
	NET_MOBILE(1024 * 64),
	NET_PROXY(1024 * 128),
	NET_HOSTING(1024 * 1024 * 16),
	
	STATUS(1024 * 16),
	MESSAGE(1024 * 32),
	QUERY(1024 * 8);
	
	private final int weight;
}
