package com.Proveedor.MSProveedor.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.Proveedor.MSProveedor.model.Proveedor;
import com.Proveedor.MSProveedor.model.estadoProveedor;
import com.Proveedor.MSProveedor.repository.proveedorRepository;

class ProveedorServiceTest {
    @Mock
    proveedorRepository pRepo;

    @InjectMocks
    proveedorService pService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Guardar proveedores
    @Test
    void testGuardarProveedor() {
        Proveedor proveedor = new Proveedor(0, "Carniceria Martinez", "camartinez@gmail.com", null, "54528785",
                "Las rosas 75");
        Proveedor proveedorGuardado = new Proveedor(1, "Carniceria Martinez", "camartinez@gmail.com",
                estadoProveedor.ACTIVO, "54528785", "Las rosas 75");
        when(pRepo.findByCorreoProv("camartinez@gmail.com")).thenReturn(null);
        when(pRepo.save(proveedor)).thenReturn(proveedorGuardado);

        Proveedor resultado = pService.registrarProveedor(proveedor);
        assertThat(resultado.getIdProveedor()).isEqualTo(1);
        assertThat(resultado.getEstado()).isEqualTo(estadoProveedor.ACTIVO);
        verify(pRepo).save(proveedor);
    }

    // Guardar proveedores - Comprobar si el correo registrado ya existe.
    @Test
    void TestCorreoExistente() {
        Proveedor existente = new Proveedor();
        existente.setCorreoProv("existe@correo.com");

        Proveedor nuevo = new Proveedor();
        nuevo.setCorreoProv("existe@correo.com");

        when(pRepo.findByCorreoProv("existe@correo.com")).thenReturn(existente);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            pService.registrarProveedor(nuevo);
        });

        assertThat(ex.getMessage()).isEqualTo("Correo ya registrado");

        verify(pRepo, never()).save(any());
    }

    // Actualizar proveedores
    @Test
    void TestActualizarInfo() {
        Proveedor existente = new Proveedor(1, "Antiguo", "antiguo@mail.com", estadoProveedor.ACTIVO, "1111",
                "Calle 1");
        Proveedor nuevo = new Proveedor(1, "Nuevo", "nuevo@mail.com", estadoProveedor.ACTIVO, "2222", "Calle 2");

        when(pRepo.findById(1)).thenReturn(Optional.of(existente));
        when(pRepo.findByCorreoProv("nuevo@mail.com")).thenReturn(null);
        when(pRepo.save(any(Proveedor.class))).thenAnswer(i -> i.getArgument(0));

        Proveedor actualizado = pService.actualizarInfo(1, nuevo);

        assertEquals("Nuevo", actualizado.getNomProv());
        assertEquals("nuevo@mail.com", actualizado.getCorreoProv());
        assertEquals("2222", actualizado.getTelProv());
        assertEquals("Calle 2", actualizado.getDirProv());

        verify(pRepo).save(existente);
    }

    // Actualizar proveedores - Error si el proveedor actualizado no existe
    @Test
    void TestProveedorNoExiste() {
        Proveedor nuevo = new Proveedor(1, "Nuevo", "nuevo@mail.com", estadoProveedor.ACTIVO, "2222", "Calle 2");

        when(pRepo.findById(1)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> pService.actualizarInfo(1, nuevo));

        assertEquals("Proveedor no encontrado", exception.getMessage());
    }

    // Actualizar proveedores - Ver si el correo ya existe
    @Test
    void TestCorreoProveedorYaExiste() {
        Proveedor existente = new Proveedor(1, "Antiguo", "antiguo@mail.com", estadoProveedor.ACTIVO, "1111",
                "Calle 1");
        Proveedor nuevo = new Proveedor(1, "Nuevo", "usado@mail.com", estadoProveedor.ACTIVO, "2222", "Calle 2");
        Proveedor otroProveedor = new Proveedor(2, "Otro", "usado@mail.com", estadoProveedor.ACTIVO, "3333", "Calle 3");

        when(pRepo.findById(1)).thenReturn(Optional.of(existente));
        when(pRepo.findByCorreoProv("usado@mail.com")).thenReturn(otroProveedor);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> pService.actualizarInfo(1, nuevo));

        assertEquals("El correo ya está registrado por otro proveedor.", exception.getMessage());

        verify(pRepo, never()).save(any());
    }

    // Actualizar proveedores - Si el correo existe pero es el mismo proveedor
    @Test
    void TestCorreoProveedorIgual() {
        Proveedor existente = new Proveedor(1, "Original", "igual@mail.com", estadoProveedor.ACTIVO, "1111", "Calle 1");
        Proveedor nuevo = new Proveedor(1, "Actualizado", "igual@mail.com", estadoProveedor.ACTIVO, "9999",
                "Calle Nueva");
        when(pRepo.findById(1)).thenReturn(Optional.of(existente));
        when(pRepo.findByCorreoProv("igual@mail.com")).thenReturn(existente);
        when(pRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        Proveedor actualizado = pService.actualizarInfo(1, nuevo);

        assertEquals("Actualizado", actualizado.getNomProv());
        assertEquals("igual@mail.com", actualizado.getCorreoProv());
        assertEquals("9999", actualizado.getTelProv());
        assertEquals("Calle Nueva", actualizado.getDirProv());
        assertEquals(estadoProveedor.ACTIVO, actualizado.getEstado());

        assertSame(existente, actualizado);

        verify(pRepo).findById(1);
        verify(pRepo).findByCorreoProv("igual@mail.com");
        verify(pRepo).save(existente);
        verifyNoMoreInteractions(pRepo);
    }

    // Activar proveedores
    @Test
    void TestActivarProveedor() {
        Proveedor inactivo = new Proveedor();
        inactivo.setIdProveedor(1);
        inactivo.setEstado(estadoProveedor.INACTIVO);

        Proveedor activado = new Proveedor();
        activado.setIdProveedor(1);
        activado.setEstado(estadoProveedor.ACTIVO);

        when(pRepo.findById(1)).thenReturn(Optional.of(inactivo));
        when(pRepo.save(inactivo)).thenReturn(activado);

        Proveedor resultado = pService.activaProveedor(1);

        assertThat(resultado.getEstado()).isEqualTo(estadoProveedor.ACTIVO);
        verify(pRepo).save(inactivo);
    }

    // Activar proveedores - Ver si se intenta activar un proveedor ya activo
    @Test
    void TestActivarProveedorYaActivo() {
        Proveedor activo = new Proveedor();
        activo.setIdProveedor(2);
        activo.setEstado(estadoProveedor.ACTIVO);

        when(pRepo.findById(2)).thenReturn(Optional.of(activo));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            pService.activaProveedor(2);
        });

        assertThat(ex.getMessage()).isEqualTo("Este proveedor ya esta activo");
        verify(pRepo, never()).save(any());
    }

    // Activar proveedores - Activar un proveedor que no existe
    @Test
    void TestActivarNoExistente() {
        // Arrange
        when(pRepo.findById(3)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> {
            pService.activaProveedor(3);
        });

        verify(pRepo, never()).save(any());
    }

    // Desactivar proveedores
    @Test
    void TestDesactivarProveedor() {
        Proveedor inactivo = new Proveedor();
        inactivo.setIdProveedor(1);
        inactivo.setEstado(estadoProveedor.INACTIVO);

        Proveedor activado = new Proveedor();
        activado.setIdProveedor(1);
        activado.setEstado(estadoProveedor.ACTIVO);

        when(pRepo.findById(1)).thenReturn(Optional.of(activado));
        when(pRepo.save(activado)).thenReturn(inactivo);

        Proveedor resultado = pService.desactivaProveedor(1);

        assertThat(resultado.getEstado()).isEqualTo(estadoProveedor.INACTIVO);
        verify(pRepo).save(activado);
    }

    // Desactivar proveedores - Desactivar proveedor ya desactivado
    @Test
    void TestDesactivarProveedorYaInactivo() {
        Proveedor inactivo = new Proveedor();
        inactivo.setIdProveedor(2);
        inactivo.setEstado(estadoProveedor.INACTIVO);

        when(pRepo.findById(2)).thenReturn(Optional.of(inactivo));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            pService.desactivaProveedor(2);
        });

        assertThat(ex.getMessage()).isEqualTo("Este proveedor ya esta inactivo");
        verify(pRepo, never()).save(any());
    }

    // Desactivar proveedores - Desactivar proveedor que no existe
    @Test
    void TestDesactivarNoExistente() {
        when(pRepo.findById(3)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            pService.desactivaProveedor(3);
        });

        verify(pRepo, never()).save(any());
    }

    // Buscar Proveedor
    @Test
    void TestBuscarProveedor() {
        Proveedor nuevo = new Proveedor();
        nuevo.setIdProveedor(1);

        when(pRepo.findById(1)).thenReturn(Optional.of(nuevo));

        Optional<Proveedor> Resultado = pService.buscarProveedor(1);

        assertThat(Resultado).isPresent();
        assertEquals(1, Resultado.get().getIdProveedor());
        verify(pRepo).findById(1);
    }

    // Buscar Proveedor - Si el proveedor no existe
    @Test
    void TestBuscarProveedorInexistente() {
        int id = 99;
        when(pRepo.findById(id)).thenReturn(Optional.empty());

        Optional<Proveedor> Resultado = pService.buscarProveedor(id);

        assertThat(Resultado).isNotPresent();
        verify(pRepo).findById(id);
    }

    // Buscar todos los proveedores
    @Test
    void TestListarProveedores() {
        Proveedor proveedor1 = new Proveedor();
        proveedor1.setIdProveedor(1);
        Proveedor proveedor2 = new Proveedor();
        proveedor2.setIdProveedor(2);

        List<Proveedor> proveedores = List.of(proveedor1, proveedor2);
        when(pRepo.findAll()).thenReturn(proveedores);

        List<Proveedor> resultado = pService.listarProveedores();

        assertThat(resultado).isNotEmpty();
        assertThat(resultado).hasSize(2).contains(proveedor1, proveedor2);
        verify(pRepo).findAll();
    }

    // Buscar todos los proveedores - La lista esta vacia
    @Test
    void TestListaVacia() {
        when(pRepo.findAll()).thenReturn(List.of());

        List<Proveedor> resultado = pService.listarProveedores();

        assertThat(resultado).isEmpty();
        verify(pRepo).findAll();
    }

    // Comprobar si el id existe
    @Test
    void TestExisteElID() {
        int id = 1;
        when(pRepo.existsById(id)).thenReturn(true);

        boolean resultado = pService.existePorId(id);

        assertThat(resultado).isTrue();
        verify(pRepo).existsById(id);
    }

    // Comprobar si el id existe - Caso que este no exista
    @Test
    void TestNoExisteElID() {
        int id = 999;
        when(pRepo.existsById(id)).thenReturn(false);

        boolean resultado = pService.existePorId(id);

        assertThat(resultado).isFalse();
        verify(pRepo).existsById(id);
    }

}
