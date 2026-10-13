package devSuivi.service;

import devSuivi.dto.ProjectListDto;
import devSuivi.dto.ProjectListDto;
import devSuivi.dto.ServerListDto;
import devSuivi.dto.ServerNewDto;
import devSuivi.entity.Project;
import devSuivi.entity.Server;
import devSuivi.mapper.ProjectMapper;
import devSuivi.mapper.ServerMapper;
import devSuivi.repository.ServerRepositoty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ServerService {

    private final ServerRepositoty serverRepositoty;
    private final ServerMapper serverMapper;
    private final ProjectMapper projectMapper;

    @Transactional(readOnly = true)
    public List<ServerListDto> getAllServers(){
        return serverRepositoty.findAll().stream()
                .map(serverMapper::toServerListDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public ServerListDto getServerById(Long id){
        return serverMapper.toServerListDto(getEntityServerById(id));
    }

    @Transactional(readOnly = true)
    public List<ProjectListDto> getProjectsByServerId(Long id){
        return getEntityServerById(id).getProjects().stream()
                .map(projectMapper::toProjectListDto)
                .toList();
    }

    Server getEntityServerById(Long id){
        if(id == null){
            return null;
        } else {
            return serverRepositoty.findById(id).orElseThrow( ()-> new NoSuchElementException("Server inconnue : " + id) );
        }
    }

    @Transactional
    public ServerListDto saveServer(ServerNewDto serverNewDto){
        var server = serverMapper.toServer(serverNewDto);
        var saved = serverRepositoty.save(server);
        return serverMapper.toServerListDto(saved);
    }

    @Transactional
    public ServerListDto updateServer(Long id, ServerNewDto serverNewDto){
        var server = getEntityServerById(id);
        serverMapper.updateServer(serverNewDto, server);
        var saved = serverRepositoty.save(server);
        return serverMapper.toServerListDto(saved);
    }

    @Transactional
    public void deleteServer(Long id){
        serverRepositoty.deleteById(id);
    }
}
