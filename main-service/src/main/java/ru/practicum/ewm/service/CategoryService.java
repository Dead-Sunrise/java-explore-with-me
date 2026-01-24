package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.category.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto adminCreateCategory(NewCategoryDto newCategoryDto);

    CategoryDto adminUpdateCategory(Long categoryId, CategoryDto categoryDto);

    void adminDeleteCategory(Long categoryId);

    CategoryDto publicGetCategoryById(Long categoryId);

    List<CategoryDto> publicGetAllCategories(Integer from, Integer size);
}
