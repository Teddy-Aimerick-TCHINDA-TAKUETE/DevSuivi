package devSuivi.mapper;

import devSuivi.dto.ServerListDto;
import devSuivi.dto.ServerNewDto;
import devSuivi.entity.Server;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ServerMapper {

    Server toServer(ServerNewDto serverNewDto);

    ServerListDto toServerListDto(Server server);

    void updateServer(ServerNewDto serverNewDto, @MappingTarget Server server);
}
