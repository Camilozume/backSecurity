package com.registraduria.securityBackend.services;

import com.registraduria.securityBackend.models.Permission;
import com.registraduria.securityBackend.repositories.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PermissionServices {

    @Autowired
    private PermissionRepository permissionRepository;

    /**
     *
     * @return
     */
    public List<Permission> index(){
        return (List<Permission>)this.permissionRepository.findAll();
    }

    /**
     *
     * @param id
     * @return
     */
    public Optional<Permission> show(int id){
        return this.permissionRepository.findById(id);
    }

    /**
     *
     * @param newPermission
     * @return
     */
    public Permission create(Permission newPermission){
        if (newPermission.getId() == null){
            if (newPermission.getMethod() != null && newPermission.getUrl() != null)
                return this.permissionRepository.save(newPermission);
            else{
                //TODO 400 BAD REQUEST
                return newPermission;
            }
        }
        else{
            // TODO validate if id exists
            return newPermission;
        }
    }

    /**
     *
     * @param id
     * @param updatedPermission
     * @return
     */
    public Permission update(int id, Permission updatedPermission){
        if (id > 0){
            Optional<Permission> tempPermission = this.show(id);
            if (tempPermission.isPresent()){
                if (updatedPermission.getUrl() != null)
                    tempPermission.get().setUrl(updatedPermission.getUrl());
                if (updatedPermission.getMethod() != null)
                    tempPermission.get().setMethod(updatedPermission.getMethod());
                return this.permissionRepository.save(tempPermission.get());
            }
            else{
                //TODO 404 NOT FOUND
                return updatedPermission;
            }
        }
        else{
            //TODO 400 BAD REQUEST
            return updatedPermission;
        }
    }

    /**
     *
     * @param id
     * @return
     */
    public boolean delete(int id){
        Boolean success = this.show(id).map( permission -> {
            this.permissionRepository.delete(permission);
            return true;
        }).orElse(false);
        return success;

    }
}
