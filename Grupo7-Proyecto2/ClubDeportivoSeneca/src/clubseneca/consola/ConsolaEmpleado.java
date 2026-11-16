package clubseneca.consola;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import clubseneca.comercial.SugerenciaProducto;
import clubseneca.deportes.Equipo;
import clubseneca.deportes.Partido;
import clubseneca.deportes.PartidoInterno;
import clubseneca.deportes.PartidoCompetencia;
import clubseneca.deportes.JugadorEquipo;
import clubseneca.deportes.EstadisticaJugador;
import clubseneca.deportes.EstadisticaFutbol;
import clubseneca.deportes.EstadisticaBaloncesto;
import clubseneca.deportes.EstadisticaVoleibol;
import clubseneca.individual.Asistencia;
import clubseneca.individual.DisciplinaIndividual;
import clubseneca.individual.Instalacion;
import clubseneca.individual.SesionEntrenamiento;
import clubseneca.torneos.InscripcionTorneo;
import clubseneca.torneos.Torneo;
import clubseneca.turnos.SolicitudCambioTurno;
import clubseneca.turnos.TurnoSemanal;
import clubseneca.usuarios.Empleado;
import clubseneca.usuarios.Entrenador;
import clubseneca.usuarios.Socio;
import clubseneca.usuarios.Usuario;

public class ConsolaEmpleado extends ConsolaBase {
    public static void main(String[] args) { new ConsolaEmpleado().ejecutar(); }

    public void ejecutar() {
        if (!ingresar("EMPLEADO")) return;
        int opcion = -1;
        while (opcion != 0) {
            System.out.println("\n=== MENU EMPLEADO ===");
            System.out.println("1. Consultar mis turnos");
            System.out.println("2. Solicitar cambio de turno");
            System.out.println("3. Programar entrenamiento (entrenador)");
            System.out.println("4. Registrar asistencia (entrenador)");
            System.out.println("5. Evaluar asistencia (entrenador)");
            System.out.println("6. Consultar habilitados de un equipo");
            System.out.println("7. Sugerir un producto");
            System.out.println("8. Inscribirme a torneo individual");
            System.out.println("9. Salirme de un torneo");
            System.out.println("10. Ver torneos y resultados");
            System.out.println("11. Comprar usando descuento de empleado");
            System.out.println("12. Registrar estadisticas de un partido");
            System.out.println("0. Guardar y salir");
            opcion = leerEntero(0, 12);
            if (opcion == 1) verTurnos();
            if (opcion == 2) pedirCambio();
            if (opcion == 3) programarSesion();
            if (opcion == 4) registrarAsistencia();
            if (opcion == 5) evaluar();
            if (opcion == 6) habilitados();
            if (opcion == 7) sugerencia();
            if (opcion == 8) inscribirTorneo();
            if (opcion == 9) salirTorneo();
            if (opcion == 10) mostrarTorneo(seleccionarTorneo());
            if (opcion == 11) realizarCompra();
            if (opcion == 12) estadisticas();
        }
        finalizar();
    }
    private Entrenador entrenador() {
        if (!usuario.esEntrenador()) { System.out.println("Solo para entrenadores."); return null; }
        return (Entrenador) usuario;
    }
    private void verTurnos() {
        for (TurnoSemanal turno : club.getGestionTurnos().getTurnosEmpleado((Empleado) usuario)) {
            System.out.println(turno.getDia()+" " + turno.getHoraInicio()+" a " + turno.getHoraFin());
        }
    }
    private void pedirCambio() {
        List<TurnoSemanal> turnos = club.getGestionTurnos().getTurnosEmpleado((Empleado) usuario);
        if (turnos.isEmpty()) { System.out.println("No tienes turnos asignados."); return; }
        for (int i=0; i<turnos.size(); i++) System.out.println((i+1)+". "+turnos.get(i).getDia()+" "+turnos.get(i).getHoraInicio());
        TurnoSemanal anterior = turnos.get(leerEntero(1, turnos.size()) - 1);
        System.out.println("Dia propuesto (LUNES a DOMINGO):");
        String dia = leerDia();
        String inicio = leerHora();
        String fin = leerHora();
        if (fin.compareTo(inicio) <= 0) { System.out.println("Horario invalido."); return; }
        TurnoSemanal nuevo = new TurnoSemanal(dia, inicio, fin, (Empleado) usuario);
        SolicitudCambioTurno solicitud = new SolicitudCambioTurno(new Date(), (Empleado) usuario, anterior, nuevo);
        club.getGestionTurnos().registrarSolicitud(solicitud);
        System.out.println("Solicitud registrada para revision del administrador.");
    }
    private SesionEntrenamiento elegirSesion() {
        if (entrenador() == null) return null;
        List<SesionEntrenamiento> sesiones = new ArrayList<>();
        for (SesionEntrenamiento actual : club.getGestionDeportiva().getSesionesEntrenamiento()) {
            if (actual.getEntrenador().getLogin().equals(usuario.getLogin())) sesiones.add(actual);
        }
        if (sesiones.isEmpty()) { System.out.println("No tienes sesiones."); return null; }
        for (int i=0; i<sesiones.size(); i++) System.out.println((i+1)+". "+sesiones.get(i).getFecha()+" "+sesiones.get(i).getHora());
        return sesiones.get(leerEntero(1, sesiones.size())-1);
    }
    private void programarSesion() {
        Entrenador entrenador = entrenador();
        if (entrenador == null) return;
        System.out.println("1=Disciplina individual, 2=Equipo");
        int tipo = leerEntero(1,2);
        DisciplinaIndividual d = seleccionarDisciplina();
        Instalacion instalacion = seleccionarInstalacion(d);
        if (instalacion == null) return;
        Equipo equipo = null;
        if (tipo == 2) {
            System.out.println("Nombre de equipo:");
            equipo = club.getGestionDeportiva().buscarEquipo(leerTexto());
            if (equipo == null) { System.out.println("Equipo no encontrado."); return; }
        }
        Date fecha = leerFecha();
        String hora = leerHora();
        System.out.println("Duracion en minutos:");
        int duracion = leerEntero(1,600);
        SesionEntrenamiento sesion;
        if (tipo == 1) sesion = new SesionEntrenamiento(fecha,hora,duracion,entrenador,instalacion,d);
        else sesion = new SesionEntrenamiento(fecha,hora,duracion,entrenador,instalacion,equipo);
        if (!club.getGestionDeportiva().programarSesion(sesion)) { System.out.println("Sesion rechazada."); return; }
        System.out.println("¿Cuantos socios deseas convocar?");
        int cantidad = leerEntero(0,100);
        for (int i=0; i<cantidad; i++) {
            System.out.println("Login del socio:");
            Usuario candidato = club.getGestionUsuarios().buscarUsuario(leerTexto());
            if (candidato == null || !candidato.esSocio()) System.out.println("No es un socio registrado.");
            else if (sesion.convocarSocio((Socio) candidato)) System.out.println("Convocado.");
            else System.out.println("No se pudo convocar.");
        }
        System.out.println("Sesion programada.");
    }
    private void registrarAsistencia() {
        SesionEntrenamiento sesion = elegirSesion();
        if (sesion == null) return;
        System.out.println("Login del convocado:");
        Usuario candidato = club.getGestionUsuarios().buscarUsuario(leerTexto());
        if (candidato == null || !candidato.esSocio()) { System.out.println("No se encontro el socio."); return; }
        System.out.println("Estado: ASISTIO / FALTO / LLEGO TARDE");
        Asistencia asistencia = new Asistencia(sesion, (Socio) candidato, leerTexto());
        if (sesion.registrarAsistencia(asistencia)) System.out.println("Asistencia guardada.");
        else System.out.println("Estado invalido o socio no convocado.");
    }
    private void evaluar() {
        SesionEntrenamiento sesion = elegirSesion();
        if (sesion == null) return;
        System.out.println("Login del socio evaluado:");
        String login = leerTexto();
        for (Asistencia asistencia : sesion.getAsistencias()) {
            if (asistencia.getSocio().getLogin().equals(login)) {
                System.out.println("Nota entre 1 y 5:");
                int nota = leerEntero(1,5);
                System.out.println("Comentario:");
                if (asistencia.registrarEvaluacion(nota,leerTexto())) System.out.println("Evaluacion registrada.");
                else System.out.println("No se pudo evaluar.");
                return;
            }
        }
        System.out.println("El jugador no tiene asistencia en esta sesion.");
    }
    private void habilitados() {
        if (entrenador() == null) return;
        System.out.println("Nombre del equipo:");
        Equipo equipo = club.getGestionDeportiva().buscarEquipo(leerTexto());
        if (equipo == null) { System.out.println("Equipo no encontrado."); return; }
        System.out.println("Habilitados: " + equipo.cantidadJugadoresHabilitados());
        if (equipo.puedeJugarPartido()) System.out.println("Puede jugar.");
        else System.out.println("No cumple el minimo.");
    }
    private void estadisticas() {
        if (entrenador() == null) return;
        List<Partido> lista = club.getGestionDeportiva().getPartidos();
        if (lista.isEmpty()) { System.out.println("No hay partidos registrados."); return; }
        for (int i=0;i<lista.size();i++) System.out.println((i+1) + ". " + lista.get(i).getFecha());
        Partido partido = lista.get(leerEntero(1,lista.size())-1);
        System.out.println("Login del jugador:");
        Usuario jugador = club.getGestionUsuarios().buscarUsuario(leerTexto());
        if (jugador == null || !jugador.esSocio()) { System.out.println("Socio no encontrado."); return; }
        JugadorEquipo integrante = null;
        if (partido.esInterno()) {
            PartidoInterno interno = (PartidoInterno)partido;
            integrante = interno.getEquipo1().buscarJugador((Socio)jugador);
            if (integrante == null) integrante = interno.getEquipo2().buscarJugador((Socio)jugador);
        }
        if (partido.esCompetencia()) {
            PartidoCompetencia externo = (PartidoCompetencia)partido;
            integrante = externo.getInscripcion().getEquipo().buscarJugador((Socio)jugador);
        }
        if (integrante == null) { System.out.println("Jugador no pertenece al partido."); return; }
        String deporte = integrante.getEquipo().getDeporte().getNombre();
        EstadisticaJugador estadistica = null;
        if (deporte.equalsIgnoreCase("Futbol")) {
            EstadisticaFutbol futbol = new EstadisticaFutbol(integrante,partido);
            System.out.println("1 Gol, 2 Asistencia, 3 Amarilla, 4 Roja:");
            int tipo = leerEntero(1,4);
            if (tipo==1) futbol.registrarGol();
            if (tipo==2) futbol.registrarAsistencia();
            if (tipo==3) futbol.registrarAmarilla();
            if (tipo==4) futbol.registrarRoja();
            estadistica = futbol;
        } else if (deporte.equalsIgnoreCase("Baloncesto")) {
            EstadisticaBaloncesto basket = new EstadisticaBaloncesto(integrante,partido);
            System.out.println("1 Puntos, 2 Rebote:");
            int tipo = leerEntero(1,2);
            if (tipo == 1) { System.out.println("Cuantos puntos:"); basket.agregarPuntos(leerEntero(1,200)); }
            if (tipo == 2) basket.registrarRebote();
            estadistica = basket;
        } else {
            EstadisticaVoleibol volei = new EstadisticaVoleibol(integrante,partido);
            System.out.println("1 Ace, 2 Bloqueo:");
            int tipo = leerEntero(1,2);
            if (tipo == 1) volei.registrarAce();
            if (tipo == 2) volei.registrarBloqueo();
            estadistica = volei;
        }
        partido.agregarEstadistica(estadistica);
        System.out.println("Estadistica registrada.");
    }
    private void sugerencia() {
        System.out.println("Tipo (Articulo o Snack):");
        String tipo = leerTexto();
        System.out.println("Nombre propuesto:");
        String nombre = leerTexto();
        System.out.println("Descripcion:");
        String descripcion = leerTexto();
        club.getGestionComercial().registrarSugerencia(new SugerenciaProducto(new Date(),tipo,nombre,descripcion,(Empleado)usuario));
        System.out.println("Sugerencia enviada.");
    }
    private void inscribirTorneo() {
        Torneo torneo = seleccionarTorneo();
        if (torneo == null || !torneo.isIndividual()) { System.out.println("Se requiere torneo individual."); return; }
        List<Usuario> jugadores = new ArrayList<>();
        jugadores.add(usuario);
        if (torneo.isDobles()) {
            System.out.println("Login de la pareja:");
            Usuario segundo = club.getGestionUsuarios().buscarUsuario(leerTexto());
            if (segundo == null) { System.out.println("Usuario no encontrado."); return; }
            jugadores.add(segundo);
        }
        InscripcionTorneo inscripcion = club.getGestionTorneos().inscribirIndividual(torneo,jugadores,club.getGestionDeportiva(),club.getGestionTurnos());
        if (inscripcion == null) System.out.println("Inscripcion rechazada: revisa turno, nivel y cupos.");
        else System.out.println("Inscrito. Tarifa: $"+inscripcion.getTarifa());
    }
    private void salirTorneo() {
        Torneo torneo = seleccionarTorneo();
        if (torneo == null) return;
        boolean exito = false;
        for (InscripcionTorneo inscripcion : torneo.getInscripciones()) {
            if (inscripcion.contieneJugador(usuario)) {
                exito = club.getGestionTorneos().retirar(torneo,inscripcion.getIdentificador());
                break;
            }
        }
        if (exito) System.out.println("Cupo liberado.");
        else System.out.println("No fue posible retirar la inscripcion.");
    }
}
