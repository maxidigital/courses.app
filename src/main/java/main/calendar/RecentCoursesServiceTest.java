package main.calendar;

import blue.underwater.commons.datetime.XDate;
import java.io.IOException;
import java.util.List;

/**
 * Clase de demostración para mostrar cómo usar RecentCoursesService.
 */
public class RecentCoursesServiceTest {
    
    public static void main(String[] args) {
        System.out.println("=== Demostración de RecentCoursesService ===");
        
        try {
            // Asegurar que el ConfigManager se inicialice para cargar las propiedades
            System.out.println("0. Inicializando ConfigManager");
            main.ConfigManager.getInstance().readConfig();
            
            // Paso 1: Crear una instancia de RecentCoursesService
            System.out.println("1. Creando instancia de RecentCoursesService");
            RecentCoursesService service = new RecentCoursesService();
            
            // Mostrar fechas que serán consultadas
            XDate today = XDate.today();
            System.out.println("Fecha actual: " + today.format("dd/MM/yyyy"));
            System.out.println("El servicio consultará los siguientes días:");
            for (int i = 0; i < 3; i++) {
                XDate date = today.minusDays(i);
                System.out.println("- " + date.format("dd/MM/yyyy") + 
                        (i == 0 ? " (hoy)" : (i == 1 ? " (ayer)" : "")));
            }
            
            // Paso 2: Obtener la lista de estudiantes de los últimos 3 días
            System.out.println("\n2. Llamando al método getStudents()");
            List<Student> recentStudents = service.getStudents();
            
            // Paso 3: Procesar los resultados
            System.out.println("\n3. Procesando los resultados");
            System.out.println("Total de estudiantes encontrados: " + recentStudents.size());
            
            if (!recentStudents.isEmpty()) {
                System.out.println("\nLista de estudiantes:");
                for (int i = 0; i < recentStudents.size(); i++) {
                    Student student = recentStudents.get(i);
                    System.out.println((i + 1) + ". " + student.getEmail());
                }
            } else {
                System.out.println("\nNo se encontraron estudiantes en los cursos de los últimos 3 días.");
                System.out.println("Esto puede deberse a que:");
                System.out.println("- No hay cursos programados en los últimos 3 días");
                System.out.println("- Los cursos no tienen estudiantes registrados");
                System.out.println("- Hay un problema de autenticación con Google Calendar");
            }
            
            System.out.println("\nDemostración completada.");
            
        } catch (IOException e) {
            System.err.println("\nError al acceder a los datos de los cursos: " + e.getMessage());
            System.err.println("Esto puede deberse a problemas de conexión o autenticación con Google Calendar.");
        } catch (Exception e) {
            System.err.println("\nError inesperado: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n=== Documentación Rápida ===");
        System.out.println("La clase RecentCoursesService proporciona un método getStudents() que:");
        System.out.println("1. Recupera los cursos de los últimos 3 días (hoy y los 2 días anteriores)");
        System.out.println("2. Extrae todos los estudiantes de esos cursos");
        System.out.println("3. Elimina duplicados (un estudiante que participó en varios cursos aparece una vez)");
        System.out.println("4. Devuelve una lista de objetos Student");
    }
}