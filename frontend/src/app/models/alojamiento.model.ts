export interface AlojamientoRequestDTO {
    nombre: string;
    direccion: string;
    precio: number;
    descripcion: string;
    capacidadMaxima: number;
}

export interface ImagenAlojamientoResponseDTO {
    id: number;
    url: string;
    alojamientoId: number;
}

export interface AlojamientoResponseDTO {
    id: number;
    nombre: string;
    direccion: string;
    precio: number;
    descripcion: string;
    capacidadMaxima?: number;
    ownerId: number;
    imagenes?: ImagenAlojamientoResponseDTO[];
}
