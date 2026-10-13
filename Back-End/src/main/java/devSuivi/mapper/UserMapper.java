package devSuivi.mapper;

import devSuivi.dto.*;
import devSuivi.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(UserNewDto userNewDto);

    UserListDto toUserListDto(User user);

    UserDetailDto toUserDetailDto(User user, List<ProjectListDto> projects, List<ContributionListDto> contributions);

    void updateUser(UserNewDto userNewDto, @MappingTarget User user);
}
