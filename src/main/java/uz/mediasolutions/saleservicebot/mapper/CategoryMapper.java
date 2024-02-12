package uz.mediasolutions.saleservicebot.mapper;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import uz.mediasolutions.saleservicebot.entity.Category;
import uz.mediasolutions.saleservicebot.payload.CategoryDTO;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    List<CategoryDTO> toDTOList(List<Category> categories);

    CategoryDTO toDTO(Category category);

    Category toEntity(CategoryDTO categoryDTO);

}
