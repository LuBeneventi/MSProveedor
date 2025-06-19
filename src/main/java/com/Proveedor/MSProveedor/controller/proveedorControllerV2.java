package com.Proveedor.MSProveedor.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Proveedor.MSProveedor.assembler.ProveedorModelAssembler;
import com.Proveedor.MSProveedor.model.Proveedor;
import com.Proveedor.MSProveedor.model.estadoProveedor;
import com.Proveedor.MSProveedor.service.proveedorService;

import org.springframework.hateoas.MediaTypes;

@RestController
@RequestMapping("/api/v2/proveedor")
public class proveedorControllerV2 {

    @Autowired
    private proveedorService pService;

    @Autowired
    private ProveedorModelAssembler assembler;

    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<CollectionModel<EntityModel<Proveedor>>> listar() {
        List<EntityModel<Proveedor>> proveedores = pService.listarProveedores().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        if (proveedores.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        CollectionModel<EntityModel<Proveedor>> collectionModel = CollectionModel.of(
                proveedores,
                linkTo(methodOn(proveedorControllerV2.class).listar()).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<Proveedor>> Obtener(@PathVariable int id) {
        Optional<Proveedor> prov = pService.buscarProveedor(id);
        if (prov.isPresent()) {
            EntityModel<Proveedor> model = assembler.toModel(prov.get());
            return ResponseEntity.ok(model);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<Proveedor>> registrar(@RequestBody Proveedor proveedor) {
        if (proveedor.getIdProveedor() != 0 && pService.existePorId(proveedor.getIdProveedor())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        proveedor.setEstado(estadoProveedor.ACTIVO);
        Proveedor newProv = pService.registrarProveedor(proveedor);

        return ResponseEntity
                .created(linkTo(methodOn(proveedorControllerV2.class).Obtener(newProv.getIdProveedor())).toUri())
                .body(assembler.toModel(newProv));
    }

    @PutMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<Proveedor>> editarDatos(@RequestBody Proveedor proveedor, @PathVariable int id) {
        try {
            Proveedor actualizado = pService.actualizarInfo(id, proveedor);
            return ResponseEntity
                    .ok(assembler.toModel(actualizado));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping(value = "activar/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<Proveedor>> activarProv(@PathVariable int id) {
        try {
            Proveedor activar = pService.activaProveedor(id);
            return ResponseEntity.ok(assembler.toModel(activar));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping(value = "desactivar/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<Proveedor>> desactivarProv(@PathVariable int id) {
        try {
            Proveedor desactivar = pService.desactivaProveedor(id);
            return ResponseEntity.ok(assembler.toModel(desactivar));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

}
