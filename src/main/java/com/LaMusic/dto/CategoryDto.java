package com.LaMusic.dto;

import com.LaMusic.entity.Category;

public record CategoryDto(String name) {

	public Category toCategory() {
		return new Category(); 
	}

}
