package com.gutti.store.controllers;

import com.gutti.store.api.CategoryTypeResponse;
import com.gutti.store.services.CategoryTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Ivan Alban
 */
@RestController
@RequestMapping(CategoryTypeController.PATH)
public class CategoryTypeController {

    public static final String PATH = Constants.BASE_PATH + "/category-types";

    @Autowired
    private CategoryTypeService categoryTypeService;

    @GetMapping
    public List<CategoryTypeResponse> readCategoryTypes() {
        return categoryTypeService.readCategoryTypes();
    }
}
