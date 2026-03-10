package com.alura.literalura.service;

import com.alura.literalura.dto.DatosLibro;
import com.alura.literalura.dto.DatosRespuesta;
import com.alura.literalura.model.Autor;
import com.alura.literalura.model.Libro;
import com.alura.literalura.repository.AutorRepository;
import com.alura.literalura.repository.LibroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LibroService {

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private AutorRepository autorRepository;

    @Autowired
    private ConsumoAPI consumoAPI;

    @Autowired
    private ConvierteDatos convierteDatos;

    private static final String URL_BASE = "https://gutendex.com/books/?search=";

    public void buscarLibroPorTitulo(String titulo) {
        try {
            String url = URL_BASE + titulo.replace(" ", "%20");
            String json = consumoAPI.obtenerDatos(url);
            DatosRespuesta respuesta = convierteDatos.obtenerDatos(json, DatosRespuesta.class);

            if (respuesta.getResultados() != null && !respuesta.getResultados().isEmpty()) {
                DatosLibro datosLibro = respuesta.getResultados().get(0);

                // Verificar si el libro ya existe
                Optional<Libro> libroExistente = libroRepository.findByTitulo(datosLibro.getTitulo());
                if (libroExistente.isPresent()) {
                    System.out.println("El libro ya está registrado en la base de datos");
                    return;
                }

                // Procesar autor
                Autor autor = null;
                if (datosLibro.getAutores() != null && !datosLibro.getAutores().isEmpty()) {
                    var datosAutor = datosLibro.getAutores().get(0);

                    // Buscar si el autor ya existe
                    Optional<Autor> autorExistente = autorRepository.findByNombre(datosAutor.getNombre());
                    if (autorExistente.isPresent()) {
                        autor = autorExistente.get();
                    } else {
                        autor = new Autor(
                                datosAutor.getNombre(),
                                datosAutor.getFechaNacimiento(),
                                datosAutor.getFechaFallecimiento()
                        );
                        autor = autorRepository.save(autor);
                    }
                }

                // Procesar idioma (tomamos el primero)
                String idioma = "desconocido";
                if (datosLibro.getIdiomas() != null && !datosLibro.getIdiomas().isEmpty()) {
                    idioma = datosLibro.getIdiomas().get(0);
                }

                // Crear y guardar libro
                Libro libro = new Libro(
                        datosLibro.getTitulo(),
                        idioma,
                        datosLibro.getDescargas().intValue(),
                        autor
                );

                libroRepository.save(libro);
                System.out.println("Libro guardado exitosamente: " + libro.getTitulo());

            } else {
                System.out.println("No se encontró el libro");
            }
        } catch (Exception e) {
            System.out.println("Error al buscar el libro: " + e.getMessage());
        }
    }

    public List<Libro> listarLibros() {
        return libroRepository.findAll();
    }

    public List<Autor> listarAutores() {
        return autorRepository.findAll();
    }

    public List<Autor> autoresVivosEn(int anio) {
        return autorRepository.autoresVivosEn(anio);
    }

    public List<Libro> librosPorIdioma(String idioma) {
        return libroRepository.findByIdioma(idioma);
    }
}