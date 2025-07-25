package com.team5.librarymanager.service;

import com.team5.librarymanager.entity.Category;
import com.team5.librarymanager.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public List<Category> searchCates(String categoryName) {
        return categoryRepository.findAllByNameContainingIgnoreCase(categoryName);
    }

    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }

    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    public void deleteById(Long id) {
        Optional<Category> optional = categoryRepository.findById(id);
        Category category = optional.get();
        category.setStatus(false);
        categoryRepository.save(category);
    }

    public Long count() {
        return categoryRepository.count();
    }

    public void saveAll(List<Category> list) {
        categoryRepository.saveAll(list);
    }

    public boolean existsCategory(String name) {
        return categoryRepository.existsByName(name);
    }
}