package clubseneca.consola;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import clubseneca.deportes.Equipo;
import clubseneca.deportes.Partido;
import clubseneca.deportes.PartidoInterno;
import clubseneca.deportes.PartidoCompetencia;
import clubseneca.deportes.EstadisticaJugador;
import clubseneca.individual.DisciplinaIndividual;
import clubseneca.individual.InscripcionDisciplina;
import clubseneca.individual.Instalacion;
import clubseneca.individual.PartidoPractica;
import clubseneca.individual.Reserva;
import clubseneca.torneos.BonoDescuento;
import clubseneca.torneos.InscripcionTorneo;
import clubseneca.torneos.Torneo;
import clubseneca.usuarios.Socio;
import clubseneca.usuarios.Usuario;
import clubseneca.usuarios.InvitadoTorneo;

public class ConsolaSocio extends ConsolaBase {
    public static void main(String[] args) { new ConsolaSocio().ejecutar(); }

    public void ejecutar() {
        if (!ingresar("SOCIO")) return;
        int opcion = -1;
        while (opcion != 0) {
            System.out.println("\n=== MENU SOCIO / PARTICIPANTE ===");
            System.out.println("1. Inscribirme en una disciplina");
            System.out.println("2. Reservar instalacion");
            System.out.println("3. Programar partido de practica");
            System.out.println("4. Comprar en tienda o cafeteria");
            System.out.println("5. Consultar torneos e historial");
            System.out.println("6. Inscribirme a torneo individual");
            System.out.println("7. Salirme de un torneo");
            System.out.println("8. Consultar bonos y puntos");
            System.out.println("9. Inscribirme en equipo");
            System.out.println("10. Consultar mis reservas y partidos");
            System.out.println("11. Consultar permiso de acceso con bebida caliente");
            System.out.println("12. Consultar resultados y estadisticas de partidos");
            System.out.println("13. Consultar ranking en disciplinas");
            System.out.println("0. Guardar y salir");
            opcion = leerEntero(0, 13);
            if (opcion == 1) inscribirDisciplina();
            if (opcion == 2) reservar();
            if (opcion == 3) practica();
            if (opcion == 4) realizarCompra();
            if (opcion == 5) mostrarTorneo(seleccionarTorneo());
            if (opcion == 6) inscribirTorneo();
            if (opcion == 7) salirTorneo();
            if (opcion == 8) verBonos();
            if (opcion == 9) inscribirEquipo();
            if (opcion == 10) misReservas();
            if (opcion == 11) acceso();
            if (opcion == 12) resultados();
            if (opcion == 13) ranking();
        }
        finalizar();
    }
    private Socio socio() {
        if (!usuario.esSocio()) { System.out.println("Solo disponible para socios."); return null; }
        return (Socio) usuario;
    }
    private void inscribirDisciplina() {
        if (socio() == null) return;
        DisciplinaIndividual disciplina = seleccionarDisciplina();
        if (disciplina == null) return;
        System.out.println("Nivel: Principiante, Intermedio, Avanzado:");
        InscripcionDisciplina inscripcion = club.getGestionDeportiva().inscribirUsuarioEnDisciplina(usuario, disciplina, leerNivel());
        if (inscripcion == null) System.out.println("Inscripcion rechazada.");
        else System.out.println("Inscripcion registrada.");
    }
    private void reservar() {
        Socio socio = socio();
        if (socio == null) return;
        DisciplinaIndividual disciplina = seleccionarDisciplina();
        Instalacion instalacion = seleccionarInstalacion(disciplina);
        if (instalacion == null) return;
        Date fecha = leerFecha();
        String inicio = leerHora();
        String fin = leerHora();
        if (fin.compareTo(inicio) <= 0) { System.out.println("La hora final debe ser posterior."); return; }
        System.out.println("Cantidad de jugadores:");
        int cantidad = leerEntero(1, 4);
        System.out.println("Modalidad (Sencillos o Dobles):");
        String modalidad = leerTexto();
        Reserva reserva = club.getGestionDeportiva().reservarInstalacion(socio, instalacion, fecha, inicio, fin, cantidad, modalidad);
        if (reserva == null) System.out.println("Reserva rechazada: verifica capacidad, horario, modalidad y disponibilidad.");
        else System.out.println("Reserva realizada.");
    }
    private void practica() {
        if (socio() == null) return;
        DisciplinaIndividual disciplina = seleccionarDisciplina();
        if (disciplina == null) return;
        InscripcionDisciplina propia = club.getGestionDeportiva().buscarInscripcion(usuario, disciplina);
        if (propia == null) { System.out.println("Primero debes estar inscrito en la disciplina."); return; }
        System.out.println("Login del otro participante:");
        Usuario otro = club.getGestionUsuarios().buscarUsuario(leerTexto());
        InscripcionDisciplina segunda = club.getGestionDeportiva().buscarInscripcion(otro, disciplina);
        if (segunda == null) { System.out.println("El otro participante no esta inscrito."); return; }
        PartidoPractica partido = new PartidoPractica(leerFecha(), disciplina);
        partido.agregarParticipante(propia);
        partido.agregarParticipante(segunda);
        if (club.getGestionDeportiva().programarPartidoPractica(partido)) System.out.println("Partido programado.");
        else System.out.println("Partido rechazado por modalidad o niveles.");
    }
    private void inscribirTorneo() {
        Torneo torneo = seleccionarTorneo();
        if (torneo == null || !torneo.isIndividual()) { System.out.println("Selecciona un torneo individual."); return; }
        List<Usuario> jugadores = new ArrayList<>();
        jugadores.add(usuario);
        if (torneo.isDobles()) {
            System.out.println("Login de la pareja:");
            Usuario pareja = club.getGestionUsuarios().buscarUsuario(leerTexto());
            if (pareja == null) { System.out.println("La pareja debe estar registrada."); return; }
            jugadores.add(pareja);
        }
        InscripcionTorneo inscripcion = club.getGestionTorneos().inscribirIndividual(torneo, jugadores, club.getGestionDeportiva(), club.getGestionTurnos());
        if (inscripcion == null) System.out.println("No se pudo inscribir: revisa nivel, turnos, cupos y elegibilidad.");
        else System.out.println("Inscrito: " + inscripcion.getIdentificador() + ". Tarifa: $" + inscripcion.getTarifa());
    }
    private void salirTorneo() {
        Torneo torneo = seleccionarTorneo();
        if (torneo == null) return;
        boolean salio = false;
        for (InscripcionTorneo inscripcion : torneo.getInscripciones()) {
            if (inscripcion.contieneJugador(usuario)) {
                salio = club.getGestionTorneos().retirar(torneo, inscripcion.getIdentificador());
                break;
            }
        }
        if (salio) System.out.println("Inscripcion cancelada; cupo liberado.");
        else System.out.println("No se puede salir: no inscrito o torneo iniciado.");
    }
    private void verBonos() {
        if (usuario.esInvitadoTorneo()) {
            InvitadoTorneo invitado = (InvitadoTorneo) usuario;
            for (BonoDescuento bono : invitado.getBonos()) {
                String estado = "Disponible";
                if (bono.isUsado()) estado = "Usado";
                System.out.println(bono.getNombreTorneo()+" - $"+bono.getValor()+" - "+estado);
            }
            return;
        }
        Socio socio = socio();
        if (socio == null) return;
        System.out.println("Puntos: " + socio.getPuntosFidelidad());
        for (BonoDescuento bono : socio.getBonos()) {
            String estadoBono = "Disponible";
            if (bono.isUsado()) estadoBono = "Usado";
            System.out.println(bono.getNombreTorneo() + " - $" + bono.getValor() + " - " + estadoBono);
        }
    }
    private void inscribirEquipo() {
        Socio socio = socio();
        if (socio == null) return;
        List<Equipo> equipos = club.getGestionDeportiva().getEquipos();
        if (equipos.isEmpty()) { System.out.println("No hay equipos."); return; }
        for (int i=0; i<equipos.size(); i++) System.out.println((i+1)+". "+equipos.get(i).getNombre());
        Equipo equipo = equipos.get(leerEntero(1, equipos.size())-1);
        System.out.println("Posicion:");
        String posicion = leerTexto();
        System.out.println("Numero de camiseta:");
        int numero = leerEntero(1, 999);
        if (club.getGestionDeportiva().inscribirSocioEnEquipo(socio, equipo, posicion, numero)) System.out.println("Inscrito en equipo.");
        else System.out.println("Inscripcion rechazada por edad, cupo o camiseta.");
    }
    private void misReservas() {
        Socio socio = socio();
        if (socio == null) return;
        for (Reserva r : club.getGestionDeportiva().getReservas()) {
            if (r.getSocio() != null && r.getSocio().getLogin().equals(usuario.getLogin())) {
                System.out.println(r.getInstalacion().getNombre()+" " + r.getFecha()+" " + r.getHoraInicio());
            }
        }
        for (DisciplinaIndividual d : club.getGestionDeportiva().getDisciplinas()) {
            for (PartidoPractica p : d.getPartidosPractica()) {
                for (InscripcionDisciplina i : p.getParticipantes()) {
                    if (i.getTitular().getLogin().equals(usuario.getLogin())) {
                        System.out.println("Partido: " + d.getNombre() + " " + p.getFecha());
                    }
                }
            }
        }
    }
    private void ranking() {
        if (socio() == null) return;
        for (DisciplinaIndividual disciplina : club.getGestionDeportiva().getDisciplinas()) {
            InscripcionDisciplina inscripcion = club.getGestionDeportiva().buscarInscripcion(usuario,disciplina);
            if (inscripcion != null) {
                System.out.println(disciplina.getNombre() + " - Nivel: " + inscripcion.getNivel() + " - ELO: " + inscripcion.getRankingElo());
            }
        }
    }
    private void resultados() {
        Socio socio = socio();
        if (socio == null) return;
        for (Partido partido : club.getGestionDeportiva().getPartidos()) {
            boolean participa = false;
            if (partido.esInterno()) {
                PartidoInterno interno = (PartidoInterno)partido;
                participa = interno.getEquipo1().buscarJugador(socio) != null || interno.getEquipo2().buscarJugador(socio) != null;
            }
            if (partido.esCompetencia()) {
                PartidoCompetencia competencia = (PartidoCompetencia)partido;
                participa = competencia.getInscripcion().getEquipo().buscarJugador(socio) != null;
            }
            if (participa) {
                System.out.println(partido.getFecha() + " Resultado: " + partido.getMarcadorEquipo1() + " - " + partido.getMarcadorEquipo2());
                for (EstadisticaJugador estadistica : partido.getEstadisticas()) {
                    if (estadistica.getJugador().getSocio().getLogin().equals(socio.getLogin())) {
                        System.out.println("Estadistica registrada para: " + socio.getLogin() + " | Tipo: " + estadistica.getClass().getSimpleName());
                    }
                }
            }
        }
    }
    private void acceso() {
        Socio socio = socio();
        if (socio == null) return;
        DisciplinaIndividual disciplina = seleccionarDisciplina();
        Instalacion instalacion = seleccionarInstalacion(disciplina);
        if (instalacion == null) return;
        System.out.println("¿Tienes una bebida caliente? 1=Si 2=No");
        socio.setTieneBebidaCaliente(leerEntero(1,2)==1);
        if (club.getGestionDeportiva().puedeUsarInstalacion(socio, instalacion)) System.out.println("Acceso permitido.");
        else System.out.println("Acceso denegado por seguridad.");
    }
}
