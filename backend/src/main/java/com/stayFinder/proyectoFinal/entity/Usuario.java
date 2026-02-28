package com.stayFinder.proyectoFinal.entity;

import java.io.Serializable;

import com.stayFinder.proyectoFinal.entity.enums.Role;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "usuarios")
public class Usuario implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    private String nombre;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "usuarioId", nullable = false, unique = true) // 👈 la columna se sigue llamando usuario_id en la BD
    private Long usuarioId; // 👈 el campo en Java ahora sí se llama usuarioId

    private String contrasena;
    private String telefono;
    private String fechaNacimiento;

    @Enumerated(EnumType.STRING)
    private Role role;
}

