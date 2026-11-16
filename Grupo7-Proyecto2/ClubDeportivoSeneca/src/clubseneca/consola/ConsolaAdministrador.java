package clubseneca.consola;

public class ConsolaAdministrador extends ConsolaBase {
    public static void main(String[] args) { new ConsolaAdministrador().ejecutar(); }

    public void ejecutar() {
        if (!ingresar("ADMIN")) return;
        AdministracionUsuariosDeportes deportiva = new AdministracionUsuariosDeportes(this);
        AdministracionComercial comercial = new AdministracionComercial(this);
        AdministracionTurnos turnos = new AdministracionTurnos(this);
        AdministracionTorneos torneos = new AdministracionTorneos(this);
        int opcion = -1;
        while (opcion != 0) {
            System.out.println("\n=== MENU ADMINISTRADOR ===");
            System.out.println("1. Registrar usuario (socio, entrenador, fisio, invitado)");
            System.out.println("2. Dar de baja un socio");
            System.out.println("3. Crear equipo deportivo");
            System.out.println("4. Crear instalacion o disciplina");
            System.out.println("5. Registrar competencia externa");
            System.out.println("6. Gestionar productos e inventario");
            System.out.println("7. Gestionar turnos y solicitudes");
            System.out.println("8. Gestionar torneos internos");
            System.out.println("9. Consultar reportes y registros");
            System.out.println("10. Guardar datos ahora");
            System.out.println("11. Programar partido interno");
            System.out.println("12. Dar de baja equipo");
            System.out.println("13. Registrar partido de competencia externa");
            System.out.println("0. Guardar y salir");
            opcion = leerEntero(0,13);
            if (opcion == 1) deportiva.crearUsuario();
            if (opcion == 2) deportiva.bajaUsuario();
            if (opcion == 3) deportiva.crearEquipo();
            if (opcion == 4) deportiva.crearInstalacion();
            if (opcion == 5) deportiva.competencia();
            if (opcion == 6) comercial.menu();
            if (opcion == 7) turnos.menu();
            if (opcion == 8) torneos.menu();
            if (opcion == 9) comercial.reportes();
            if (opcion == 10) { club.guardarDatos(); System.out.println("Datos guardados."); }
            if (opcion == 11) deportiva.partidoInterno();
            if (opcion == 12) deportiva.bajaEquipo();
            if (opcion == 13) deportiva.partidoCompetencia();
        }
        finalizar();
    }
}
