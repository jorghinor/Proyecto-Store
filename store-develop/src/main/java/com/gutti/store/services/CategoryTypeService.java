package com.gutti.store.services;

import com.gutti.store.api.CategoryTypeResponse;
import com.gutti.store.domain.CategoryType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author Ivan Alban
 */
@Service
public class CategoryTypeService {

    public List<CategoryTypeResponse> readCategoryTypes() {
        List<CategoryTypeResponse> result = new ArrayList<>();
        for (CategoryType categoryType : CategoryType.values()) {
            result.add(toCategoryTypeResponse(categoryType));
        }

        result.sort(Comparator.comparing(CategoryTypeResponse::getLabel));

        return result;
    }

    public CategoryTypeResponse toCategoryTypeResponse(CategoryType source) {
        return CategoryTypeResponse.builder()
                .id(source.name())
                .label(source.getLabel())
                .build();
    }
}
