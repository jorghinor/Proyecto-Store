package com.gutti.store.services;

import com.gutti.store.dtos.SaveStockItemPropertyDto;
import com.gutti.store.dtos.StockItemPropertyDto;

import java.util.List;
import java.util.Optional;

public interface StockItemPropertyService {

    List<StockItemPropertyDto> findAll();

    Optional<StockItemPropertyDto> findById(Integer id);

    StockItemPropertyDto save(SaveStockItemPropertyDto stockItemPropertyDto);

    Optional<StockItemPropertyDto> update(Integer id, SaveStockItemPropertyDto stockItemPropertyDto);

    boolean delete(Integer id);
}
