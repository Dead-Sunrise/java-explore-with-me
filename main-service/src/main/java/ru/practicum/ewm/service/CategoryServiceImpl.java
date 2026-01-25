package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.category.NewCategoryDto;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.mapper.CategoryMapper;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.repository.EventRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CategoryDto adminCreateCategory(NewCategoryDto newCategoryDto) {
        if (categoryRepository.existsByName(newCategoryDto.getName())) {
            throw new ValidationException("Категория с названием " + newCategoryDto.getName() + " уже существует.");
        }
        Category category = CategoryMapper.newCategoryDtoToCategory(newCategoryDto);
        return CategoryMapper.categoryToCategoryDto(categoryRepository.save(category));
    }

    @Transactional
    @Override
    public CategoryDto adminUpdateCategory(Long categoryId, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ConflictException("Категория с id: " + categoryDto + " не существует."));
        if (categoryRepository.existsByName(categoryDto.getName()) &&
                !category.getName().equals(categoryDto.getName())) {
            throw new ConflictException("Категория с таким именем уже существует.");
        }
        category.setName(categoryDto.getName());
        return CategoryMapper.categoryToCategoryDto(categoryRepository.save(category));
    }

    @Override
    public void adminDeleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ConflictException("Категория с id: " + categoryId + " не существует."));
        if (eventRepository.existsByCategoryId(categoryId)) {
            throw new ConflictException("Удаление невозможно, существуют события с данной категорией.");
        }
        categoryRepository.deleteById(categoryId);
    }

    @Override
    public CategoryDto publicGetCategoryById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ConflictException("Категория с id: " + categoryId + " не существует."));
        return CategoryMapper.categoryToCategoryDto(category);
    }

    @Override
    public List<CategoryDto> publicGetAllCategories(Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return categoryRepository.findAll(pageable).stream()
                .map(CategoryMapper::categoryToCategoryDto)
                .collect(Collectors.toList());
    }
}