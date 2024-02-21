package uz.mediasolutions.saleservicebot.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import uz.mediasolutions.saleservicebot.entity.Category;
import uz.mediasolutions.saleservicebot.payload.CategoryDTO;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    List<CategoryDTO> toDTOList(List<Category> categories);

    CategoryDTO toDTO(Category category);

}
