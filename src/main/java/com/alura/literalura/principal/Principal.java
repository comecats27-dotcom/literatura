package com.alura.literalura.principal;

import com.alura.literalura.model.Autor;
import com.alura.literalura.model.Libro;
import com.alura.literalura.service.LibroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
public class Principal {
    private Scanner teclado = new Scanner(System.in);

    @Autowired
    private LibroService servicio;

    // Constructor vacío necesario para @Autowired
    public Principal() {}

    public void muestraElMenu() {
        int opcion = -1;
        while (opcion != 0) {
            System.out.println("""
                \n*** LITERALURA - CATÁLOGO DE LIBROS ***
                1 - Buscar libro por título
                2 - Listar libros registrados
                3 - Listar autores registrados
                4 - Listar autores vivos en determinado año
                5 - Listar libros por idioma
                0 - Salir
                
                Elija una opción:
                """);

            try {
                opcion = teclado.nextInt();
                teclado.nextLine(); // Limpiar buffer

                switch (opcion) {
                    case 1:
                        buscarLibro();
                        break;
                    case 2:
                        listarLibros();
                        break;
                    case 3:
                        listarAutores();
                        break;
                    case 4:
                        autoresVivos();
                        break;
                    case 5:
                        librosPorIdioma();
                        break;
                    case 0:
                        System.out.println("Cerrando aplicación...");
                        break;
                    default:
                        System.out.println("Opción inválida. Intente nuevamente.");
                }
            } catch (Exception e) {
                System.out.println("Error: Ingrese un número válido");
                teclado.nextLine(); // Limpiar buffer en caso de error
            }
        }
    }

    private void buscarLibro() {
        System.out.println("Ingrese el título del libro a buscar:");
        String titulo = teclado.nextLine();

        if (titulo == null || titulo.trim().isEmpty()) {
            System.out.println("El título no puede estar vacío");
            return;
        }

        servicio.buscarLibroPorTitulo(titulo.trim());
    }

    private void listarLibros() {
        List<Libro> libros = servicio.listarLibros();

        if (libros.isEmpty()) {
            System.out.println("No hay libros registrados en la base de datos");
        } else {
            System.out.println("\n*** LIBROS REGISTRADOS ***");
            libros.forEach(libro -> {
                System.out.println("-------------------");
                System.out.println("Título: " + libro.getTitulo());
                System.out.println("Autor: " + (libro.getAutor() != null ? libro.getAutor().getNombre() : "Desconocido"));
                System.out.println("Idioma: " + libro.getIdioma());
                System.out.println("Descargas: " + libro.getDescargas());
            });
            System.out.println("-------------------");
        }
    }

    private void listarAutores() {
        List<Autor> autores = servicio.listarAutores();

        if (autores.isEmpty()) {
            System.out.println("No hay autores registrados en la base de datos");
        } else {
            System.out.println("\n*** AUTORES REGISTRADOS ***");
            autores.forEach(autor -> {
                System.out.println("-------------------");
                System.out.println("Nombre: " + autor.getNombre());
                System.out.println("Año de nacimiento: " + autor.getFechaNacimiento());
                System.out.println("Año de fallecimiento: " +
                        (autor.getFechaFallecimiento() != null ? autor.getFechaFallecimiento() : "Aún vivo"));
            });
            System.out.println("-------------------");
        }
    }

    private void autoresVivos() {
        try {
            System.out.println("Ingrese el año para buscar autores vivos:");
            int anio = teclado.nextInt();
            teclado.nextLine(); // Limpiar buffer

            List<Autor> autores = servicio.autoresVivosEn(anio);

            if (autores.isEmpty()) {
                System.out.println("No se encontraron autores vivos en el año " + anio);
            } else {
                System.out.println("\n*** AUTORES VIVOS EN " + anio + " ***");
                autores.forEach(autor -> {
                    System.out.println("-------------------");
                    System.out.println("Nombre: " + autor.getNombre());
                    System.out.println("Nacimiento: " + autor.getFechaNacimiento());
                    System.out.println("Fallecimiento: " +
                            (autor.getFechaFallecimiento() != null ? autor.getFechaFallecimiento() : "Aún vivo"));
                });
                System.out.println("-------------------");
            }
        } catch (Exception e) {
            System.out.println("Error: Ingrese un año válido");
            teclado.nextLine(); // Limpiar buffer
        }
    }

    private void librosPorIdioma() {
        System.out.println("""
            Ingrese el idioma (código de 2 letras):
            es - Español
            en - Inglés
            fr - Francés
            pt - Portugués
            """);
        String idioma = teclado.nextLine().toLowerCase().trim();

        if (idioma.isEmpty()) {
            System.out.println("El idioma no puede estar vacío");
            return;
        }

        List<Libro> libros = servicio.librosPorIdioma(idioma);

        if (libros.isEmpty()) {
            System.out.println("No se encontraron libros en el idioma: " + idioma);
        } else {
            System.out.println("\n*** LIBROS EN " + idioma.toUpperCase() + " ***");
            libros.forEach(libro -> {
                System.out.println("-------------------");
                System.out.println("Título: " + libro.getTitulo());
                System.out.println("Autor: " + (libro.getAutor() != null ? libro.getAutor().getNombre() : "Desconocido"));
                System.out.println("Descargas: " + libro.getDescargas());
            });
            System.out.println("-------------------");
        }
    }
}