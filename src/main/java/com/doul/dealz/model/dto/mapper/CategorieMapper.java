package com.doul.dealz.model.dto.mapper;

import com.doul.dealz.model.Categorie;
import com.doul.dealz.model.dto.request.CategorieRequestDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategorieMapper {
    @Mapping(target = "id", ignore = true)
    Categorie toCategorieEntity(CategorieRequestDTO categorieRequestDTO);
}
