package com.gutti.store.controllers;

import com.gutti.store.domain.ItemProperty;
import com.gutti.store.services.ItemPropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(ItemPropertyController.PATH)
public class ItemPropertyController {
    public static final String PATH = Constants.BASE_PATH + "/item-properties";

    @Autowired
    private ItemPropertyService service;

    @GetMapping
    public List<ItemProperty> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Optional<ItemProperty> getById(@PathVariable Integer id) {
        return service.findById(id);
    }

    @PostMapping
    public ItemProperty create(@RequestBody ItemProperty property) {
        return service.save(property);
    }

    @PutMapping("/{id}")
    public ItemProperty update(@PathVariable Integer id, @RequestBody ItemProperty property) {
        property.setId(id);
        return service.save(property);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.deleteById(id);
    }
}
