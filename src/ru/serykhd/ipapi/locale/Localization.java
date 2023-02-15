package ru.serykhd.ipapi.locale;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Localization {
	
	English("en"),
	Deutsch("de"),
	Spanish("es"),
	Portuguese("pt-BR"),
	French("fr"),
	Japanese("ja"),
	Chinese("zh-CN"),
	Russian("ru");
	
	private final String locale;
}
