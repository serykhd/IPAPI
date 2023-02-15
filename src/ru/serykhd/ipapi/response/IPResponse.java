package ru.serykhd.ipapi.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
public class IPResponse {
	
	@Setter
	private String query,message,status;
	private String continent,continentCode,country,countryCode,region,regionName,city,district,zip,timezone,currency,isp,org,as,asname,reverse;
	private float lat,lon;
	private int offset;
	@Setter
	private boolean mobile,proxy,hosting;
	
//	@Setter
//	@Deprecated
//	private RequestsLimit requestsLimit;
}
