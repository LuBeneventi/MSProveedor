package com.Proveedor.MSProveedor.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import com.Proveedor.MSProveedor.model.Proveedor;
import com.Proveedor.MSProveedor.model.estadoProveedor;
import com.Proveedor.MSProveedor.service.proveedorService;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(proveedorController.class)
class proveedorControllerTest {
        @Autowired
        private MockMvc mockMvc;

        @SuppressWarnings("removal")
        @MockBean
        private proveedorService pService;

        @Autowired
        private ObjectMapper objectMapper;

        @Test // Registro de proveedores
        void RegistroTestOK() throws Exception {
                Proveedor nuevo = new Proveedor(0, "Lacosta", "lacost@gmail.com", null, "56952482123",
                                "Los Hulmos 785");
                Proveedor registrado = new Proveedor(1, "Lacosta", "lacost@gmail.com", estadoProveedor.ACTIVO,
                                "56952482123",
                                "Los Hulmos 785");

                when(pService.registrarProveedor(any(Proveedor.class))).thenReturn(registrado);

                mockMvc.perform(post("/api/v1/proveedor")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(nuevo)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.idProveedor").value(1))
                                .andExpect(jsonPath("$.nomProv").value("Lacosta"))
                                .andExpect(jsonPath("$.correoProv").value("lacost@gmail.com"))
                                .andExpect(jsonPath("$.estado").value("ACTIVO"))
                                .andExpect(jsonPath("$.telProv").value("56952482123"))
                                .andExpect(jsonPath("$.dirProv").value("Los Hulmos 785"));
        }

        @Test // Registro de proveedores - Si el ID esta repetido
        void RegistroTestBAD() throws Exception {
                Proveedor nuevo = new Proveedor(10, "Carnes Mauricio", "carnesmau@gmail.com", estadoProveedor.ACTIVO,
                                "+56993326556", "Las Rosas 503");

                when(pService.existePorId(10)).thenReturn(true);

                mockMvc.perform(post("/api/v1/proveedor")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(nuevo)))
                                .andExpect(status().isBadRequest());
        }

        @Test // Listar Proveedores
        void ListarTestOK() throws Exception {
                Proveedor p1 = new Proveedor(10, "Carnes Mauricio", "carnesmau@gmail.com", estadoProveedor.ACTIVO,
                                "+56993326556", "Las Rosas 503");
                Proveedor p2 = new Proveedor(1, "Lacosta", "lacost@gmail.com", estadoProveedor.ACTIVO, "56952482123",
                                "Los Hulmos 785");

                when(pService.listarProveedores()).thenReturn(Arrays.asList(p1, p2));

                mockMvc.perform(get("/api/v1/proveedor")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.length()").value(2))
                                .andExpect(jsonPath("$[0].nomProv").value("Carnes Mauricio"))
                                .andExpect(jsonPath("$[1].nomProv").value("Lacosta"));
        }

        @Test // Listar Proveedores - Lista Vacia
        void ListarTestBAD() throws Exception {
                when(pService.listarProveedores()).thenReturn(Arrays.asList());

                mockMvc.perform(get("/api/v1/proveedor")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNoContent());
        }

        @Test // Obtener Proveedor
        void ObtenerTestOK() throws Exception {
                Proveedor nuevo = new Proveedor(5, "Costa", "costa1998@gmail.com", estadoProveedor.ACTIVO,
                                "+56921546938", "Los Canarios 4578");

                when(pService.buscarProveedor(5)).thenReturn(Optional.of(nuevo));

                mockMvc.perform(get("/api/v1/proveedor/5")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.idProveedor").value(5))
                                .andExpect(jsonPath("$.nomProv").value("Costa"))
                                .andExpect(jsonPath("$.correoProv").value("costa1998@gmail.com"))
                                .andExpect(jsonPath("$.estado").value("ACTIVO"))
                                .andExpect(jsonPath("$.telProv").value("+56921546938"))
                                .andExpect(jsonPath("$.dirProv").value("Los Canarios 4578"));
        }

        @Test // Obtener Proveedor - No existe el proveedor buscado
        void ObtenerTestNotFound() throws Exception {
                when(pService.buscarProveedor(999)).thenReturn(Optional.empty());

                mockMvc.perform(get("/proveedores/999")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound());
        }

        @Test // Editar datos de proveedor
        void EditarTestOK() throws Exception {
                Proveedor original = new Proveedor(10, "Castle", "castle@gmail.com", estadoProveedor.ACTIVO,
                                "+56978124545", "Las amapolas 127");

                Proveedor actualizado = new Proveedor(10, "Marco Polo", "marcopolo@gmail.com", estadoProveedor.ACTIVO,
                                "+56997856323", "Las Rosas 7425");

                when(pService.actualizarInfo(eq(10), any(Proveedor.class))).thenReturn(actualizado);

                mockMvc.perform(put("/api/v1/proveedor/10")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(actualizado)))
                                .andExpect(status().isOk())
                                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.idProveedor").value(10))
                                .andExpect(jsonPath("$.nomProv").value("Marco Polo"))
                                .andExpect(jsonPath("$.correo").value("marcopolo@gmail.com"))
                                .andExpect(jsonPath("$.estado").value("ACTIVO"))
                                .andExpect(jsonPath("$.telefono").value("+56997856323"))
                                .andExpect(jsonPath("$.direccion").value("Las Rosas 7425"));
        }

        @Test // Editar datos de proveedor - ID no vinculado a ningun Proveedor
        void EditarTestNotFound() throws Exception {
                Proveedor ejemplo = new Proveedor(45, "Karolina", "kperfumes@gmail.com", estadoProveedor.INACTIVO,
                                "+56947235684", "Maria Magdalena 745");

                when(pService.actualizarInfo(eq(999), any(Proveedor.class))).thenReturn(null);

                mockMvc.perform(put("/api/v1/proveedor/999")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(ejemplo)))
                                .andExpect(status().isNotFound());
        }

        @Test // Editar dato de proveedor - Error al editar
        void EditarTestBAD() throws Exception {
                Proveedor ex = new Proveedor(78, "Cosa", "lacosa@gmail.com", estadoProveedor.INACTIVO, "+56923568974",
                                "Las Cosas 999");

                when(pService.actualizarInfo(eq(5), any(Proveedor.class)))
                                .thenThrow(new RuntimeException("Error en servicio"));

                mockMvc.perform(put("/api/v1/proveedor/5")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(ex)))
                                .andExpect(status().isBadRequest());
        }

        @Test // Activar Proveedor
        void ActivarTestOK() throws Exception {
                Proveedor p1 = new Proveedor(7, "Sis", "siscorreo@gmail.com", estadoProveedor.ACTIVO, "+56978451236",
                                "Si 111");

                when(pService.activaProveedor(7)).thenReturn(p1);

                mockMvc.perform(put("/api/v1/proveedor/7/activar")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.idProveedor").value(7))
                                .andExpect(jsonPath("$.nomProv").value("Sis"))
                                .andExpect(jsonPath("$.estado").value("ACTIVO"));
        }

        @Test // Activar Proveedor - Proveedor no encontrado
        void ActivarTestNotFound() throws Exception {
                when(pService.activaProveedor(999))
                                .thenThrow(new RuntimeException("Proveedor no encontrado"));

                mockMvc.perform(put("/api/v1/proveedor/999/activar")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound());
        }

        @Test // Desactivar Proveedor
        void DesactivarTestOK() throws Exception {
                Proveedor p1 = new Proveedor(7, "Sis", "siscorreo@gmail.com", estadoProveedor.INACTIVO, "+56978451236",
                                "Si 111");

                when(pService.desactivaProveedor(7)).thenReturn(p1);

                mockMvc.perform(put("/api/v1/proveedor/7/desactivar")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.idProveedor").value(7))
                                .andExpect(jsonPath("$.nomProv").value("Sis"))
                                .andExpect(jsonPath("$.estado").value("INACTIVO"));
        }

        @Test // Desactivar Proveedor - Proveedor no encontrado
        void DesactivarTestNotFound() throws Exception {
                when(pService.desactivaProveedor(999))
                                .thenThrow(new RuntimeException("Proveedor no encontrado"));

                mockMvc.perform(put("/api/v1/proveedor/999/desactivar")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound());
        }

}
