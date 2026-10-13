package devSuivi.mapper;

import devSuivi.dto.ServerListDto;
import devSuivi.dto.ServerNewDto;
import devSuivi.entity.Server;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-05T17:13:45+0200",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.4.1 (Eclipse Adoptium)"
)
@Component
public class ServerMapperImpl implements ServerMapper {

    @Override
    public Server toServer(ServerNewDto serverNewDto) {
        if ( serverNewDto == null ) {
            return null;
        }

        Server server = new Server();

        server.setName( serverNewDto.name() );
        server.setDescription( serverNewDto.description() );

        return server;
    }

    @Override
    public ServerListDto toServerListDto(Server server) {
        if ( server == null ) {
            return null;
        }

        Long id = null;
        String name = null;
        String description = null;

        id = server.getId();
        name = server.getName();
        description = server.getDescription();

        ServerListDto serverListDto = new ServerListDto( id, name, description );

        return serverListDto;
    }

    @Override
    public void updateServer(ServerNewDto serverNewDto, Server server) {
        if ( serverNewDto == null ) {
            return;
        }

        server.setName( serverNewDto.name() );
        server.setDescription( serverNewDto.description() );
    }
}
