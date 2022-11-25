package com.registraduria.securityBackend.services;

import com.registraduria.securityBackend.models.Permission;
import com.registraduria.securityBackend.models.Rol;
import com.registraduria.securityBackend.repositories.PermissionRepository;
import com.registraduria.securityBackend.repositories.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class RolServices {
    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    public List<Rol> index(){
        return (List<Rol>) this.rolRepository.findAll();
    }

    public Optional<Rol> show(int id){
        return this.rolRepository.findById(id);
    }

    public Rol create(Rol newRol){
        if (newRol.getIdRol() == null){
            if (newRol.getName() != null)
                return this.rolRepository.save(newRol);
            else{
                //TODO 400 Badrequest
                return newRol;
            }

        }
        else{
            //TODO validate if exists
            return newRol;
        }

    }

    public Rol update(int id, Rol updateRol){
        if (id > 0){
            Optional<Rol> tempRol = this.show(id);
            if (tempRol.isPresent()){
                if (updateRol.getName() != null)
                    tempRol.get().setName(updateRol.getName());
                return this.rolRepository.save(tempRol.get());
            }
            else{
                //TODO 404 NOT FOUND
                return updateRol;
            }

        }
        else{
            // TODO 400 Badrequest
            return updateRol;
        }

    }

    public boolean delete(int id){
        Boolean success = this.show(id).map(rol -> {
            this.rolRepository.delete(rol);
            return true;
        }).orElse(false);
        return success;

    }

    public ResponseEntity<Rol> updateAddPermission(int idRol, int idPermission){
        Optional<Rol> rol = this.rolRepository.findById(idRol);
        if (rol.isPresent()){
            Optional<Permission> permission = this.permissionRepository.findById(idPermission);
            if (permission.isPresent()){
                Set<Permission> tempPermissions = rol.get().getPermissions();
                if (tempPermissions.contains(permission.get()))
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Ya tiene el permiso");
                else{
                    tempPermissions.add(permission.get());
                    rol.get().setPermissions(tempPermissions);
                    return new ResponseEntity<>(this.rolRepository.save(rol.get()), HttpStatus.CREATED);
                }
            }
            else
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "permission.id doesnt exist");
        }
        else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "rol.id doesnt exist");
    }

    public ResponseEntity<Boolean> validateGrant(int idRol, Permission permission){
        boolean isGrant = false;
        Optional<Rol> rol = this.rolRepository.findById(idRol);
        if (rol.isPresent()){
            for (Permission rolPermission: rol.get().getPermissions()){
                if (rolPermission.getUrl().equals(permission.getUrl()) && rolPermission.getMethod().equals(permission.getMethod())){
                    isGrant= true;
                    break;
                }
            }
            if (isGrant)
                return new ResponseEntity<>(true, HttpStatus.OK);
            else
                return new ResponseEntity<>(false, HttpStatus.UNAUTHORIZED);
        }
        else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "rol.id doesnt exist");
    }
}
