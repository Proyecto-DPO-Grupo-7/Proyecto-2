package clubseneca.consola;

import java.util.Date;
import java.util.List;
import clubseneca.deportes.Equipo;
import clubseneca.individual.DisciplinaIndividual;
import clubseneca.torneos.EncuentroTorneo;
import clubseneca.torneos.InscripcionTorneo;
import clubseneca.torneos.Torneo;
import clubseneca.torneos.TorneoCompetitivo;
import clubseneca.torneos.TorneoRecreativo;

public class AdministracionTorneos {
    private ConsolaAdministrador c;
    public AdministracionTorneos(ConsolaAdministrador c) { this.c = c; }
    public void menu() {
        int opcion = -1;
        while (opcion != 0) {
            System.out.println("\n=== TORNEOS INTERNOS ===");
            System.out.println("1. Crear torneo recreativo o competitivo");
            System.out.println("2. Registrar participante/equipo destacado");
            System.out.println("3. Inscribir equipo en torneo colectivo");
            System.out.println("4. Retirar inscripcion antes del inicio");
            System.out.println("5. Liberar cupos preferenciales sin usar");
            System.out.println("6. Iniciar torneo");
            System.out.println("7. Programar encuentro");
            System.out.println("8. Registrar marcador");
            System.out.println("9. Finalizar torneo y asignar premio");
            System.out.println("10. Consultar historial, posiciones y cupos");
            System.out.println("11. Inscribir equipo invitado externo");
            System.out.println("12. Registrar posicion de un participante");
            System.out.println("0. Volver");
            opcion = c.leerEntero(0,12);
            if (opcion == 1) crear();
            if (opcion == 2) destacar();
            if (opcion == 3) inscribirEquipo();
            if (opcion == 4) retirar();
            if (opcion == 5) liberar();
            if (opcion == 6) iniciar();
            if (opcion == 7) crearEncuentro();
            if (opcion == 8) marcador();
            if (opcion == 9) finalizar();
            if (opcion == 10) c.mostrarTorneo(c.seleccionarTorneo());
            if (opcion == 11) inscribirEquipoInvitado();
            if (opcion == 12) posicion();
        }
    }
    private void crear() {
        System.out.println("Nombre unico del torneo:");
        String nombre = c.leerTexto();
        Date fecha = c.leerFecha();
        System.out.println("Cupos totales:");
        int cupos = c.leerEntero(2,1000);
        System.out.println("1. Deporte de conjunto 2. Disciplina individual");
        int clase = c.leerEntero(1,2);
        Equipo referencia = null;
        DisciplinaIndividual disciplina = null;
        String nivel = null;
        boolean dobles = false;
        if (clase == 1) {
            System.out.println("Equipo existente de ese deporte/categoria para usar como referencia:");
            referencia = c.club.getGestionDeportiva().buscarEquipo(c.leerTexto());
            if (referencia == null) { System.out.println("Primero crea un equipo de ese deporte y categoria."); return; }
        } else {
            disciplina = c.seleccionarDisciplina();
            if (disciplina == null) return;
            System.out.println("Nivel del torneo (Principiante, Intermedio, Avanzado):");
            nivel = c.leerNivel();
            System.out.println("Modalidad: 1. Sencillos, 2. Dobles");
            dobles = c.leerEntero(1,2)==2;
        }
        System.out.println("Tipo: 1. Recreativo, 2. Competitivo");
        int tipo = c.leerEntero(1,2);
        Torneo torneo;
        clubseneca.deportes.Deporte deporte = null;
        clubseneca.deportes.Categoria categoria = null;
        if (referencia != null) { deporte = referencia.getDeporte(); categoria = referencia.getCategoria(); }
        if (tipo == 1) {
            System.out.println("Valor de cada bono para ganadores socios:");
            double bono = c.leerDecimal(1,100000000);
            torneo = new TorneoRecreativo(nombre,fecha,deporte,categoria,disciplina,nivel,dobles,cupos,bono);
        } else {
            System.out.println("Tarifa de inscripcion:");
            double tarifa = c.leerDecimal(1,100000000);
            System.out.println("Porcentaje destinado al premio en efectivo (0-100):");
            double porcentaje = c.leerDecimal(1,100)/100;
            torneo = new TorneoCompetitivo(nombre,fecha,deporte,categoria,disciplina,nivel,dobles,cupos,tarifa,porcentaje);
        }
        if (c.club.getGestionTorneos().agregarTorneo(torneo)) System.out.println("Torneo creado; cupos preferenciales: "+torneo.getCuposPreferenciales());
        else System.out.println("Torneo rechazado: revisa los datos.");
    }
    private void destacar() {
        Torneo torneo = c.seleccionarTorneo();
        if (torneo == null) return;
        System.out.println("Identificador del participante con ranking/desempeño destacado:");
        String identificador = c.leerTexto();
        if (torneo.registrarDestacado(identificador)) System.out.println("Clasificacion destacada registrada.");
        else System.out.println("No fue posible registrar destacado.");
    }
    private void inscribirEquipo() {
        Torneo torneo = c.seleccionarTorneo();
        if (torneo == null || torneo.isIndividual()) { System.out.println("Solo para torneos colectivos."); return; }
        System.out.println("Nombre del equipo:");
        Equipo equipo = c.club.getGestionDeportiva().buscarEquipo(c.leerTexto());
        InscripcionTorneo inscripcion = c.club.getGestionTorneos().inscribirEquipo(torneo,equipo);
        if (inscripcion == null) System.out.println("Inscripcion rechazada por deporte, categoria, cupos o habilitados.");
        else System.out.println("Equipo inscrito; tarifa: $"+inscripcion.getTarifa());
    }
    private void inscribirEquipoInvitado() {
        Torneo torneo = c.seleccionarTorneo();
        if (torneo == null || torneo.isIndividual()) { System.out.println("Debe ser un torneo colectivo."); return; }
        System.out.println("Nombre del equipo invitado:");
        String nombre = c.leerTexto();
        clubseneca.torneos.EquipoInvitadoTorneo equipo = new clubseneca.torneos.EquipoInvitadoTorneo(nombre,torneo.getDeporte(),torneo.getCategoria());
        System.out.println("Cuantos participantes tiene el equipo:");
        int cantidad = c.leerEntero(torneo.getDeporte().getMinJugadoresHabilitados(),50);
        for (int i=0; i<cantidad; i++) {
            System.out.println("Login del jugador " + (i+1) + ":");
            clubseneca.usuarios.Usuario jugador = c.club.getGestionUsuarios().buscarUsuario(c.leerTexto());
            if (!equipo.agregarJugador(jugador)) { System.out.println("Jugador invalido o duplicado."); return; }
        }
        InscripcionTorneo inscripcion = c.club.getGestionTorneos().inscribirEquipoInvitado(torneo,equipo);
        if (inscripcion == null) System.out.println("Inscripcion rechazada por cupos o condiciones del torneo.");
        else System.out.println("Equipo invitado inscrito. Tarifa: $" + inscripcion.getTarifa());
    }
    private void retirar() {
        Torneo torneo = c.seleccionarTorneo();
        if (torneo == null) return;
        c.mostrarTorneo(torneo);
        System.out.println("Identificador de la inscripcion a retirar:");
        if (torneo.retirar(c.leerTexto())) System.out.println("Inscripcion retirada; cupo liberado.");
        else System.out.println("No se pudo retirar.");
    }
    private void liberar() {
        Torneo torneo = c.seleccionarTorneo();
        if (torneo == null) return;
        if (torneo.liberarPreferenciales()) System.out.println("Cupos preferenciales vacantes liberados como regulares.");
        else System.out.println("No se pueden liberar despues del inicio.");
    }
    private void iniciar() {
        Torneo torneo = c.seleccionarTorneo();
        if (torneo == null) return;
        if (torneo.iniciar()) System.out.println("Torneo EN_CURSO.");
        else System.out.println("Se necesitan al menos dos inscripciones y estado de inscripcion.");
    }
    private InscripcionTorneo elegirInscripcion(Torneo torneo) {
        List<InscripcionTorneo> lista = torneo.getInscripciones();
        if (lista.isEmpty()) { System.out.println("No hay participantes inscritos."); return null; }
        for (int i=0; i<lista.size(); i++) System.out.println((i+1)+". "+lista.get(i).getIdentificador());
        return lista.get(c.leerEntero(1,lista.size())-1);
    }
    private void crearEncuentro() {
        Torneo torneo = c.seleccionarTorneo();
        if (torneo == null) return;
        System.out.println("Primer participante:");
        InscripcionTorneo primero = elegirInscripcion(torneo);
        System.out.println("Segundo participante:");
        InscripcionTorneo segundo = elegirInscripcion(torneo);
        if (primero == null || segundo == null) return;
        EncuentroTorneo encuentro = new EncuentroTorneo(c.leerFecha(),primero,segundo);
        if (torneo.registrarEncuentro(encuentro)) System.out.println("Encuentro registrado.");
        else System.out.println("Torneo no iniciado o participantes invalidos.");
    }
    private void marcador() {
        Torneo torneo = c.seleccionarTorneo();
        if (torneo == null || torneo.getEncuentros().isEmpty()) { System.out.println("Sin encuentros."); return; }
        List<EncuentroTorneo> encuentros = torneo.getEncuentros();
        for (int i=0; i<encuentros.size(); i++) {
            System.out.println((i+1)+". "+encuentros.get(i).getPrimero().getIdentificador()+" contra "+encuentros.get(i).getSegundo().getIdentificador());
        }
        EncuentroTorneo encuentro = encuentros.get(c.leerEntero(1,encuentros.size())-1);
        System.out.println("Marcador primero:");
        int primero = c.leerEntero(0,10000);
        System.out.println("Marcador segundo:");
        int segundo = c.leerEntero(0,10000);
        if (encuentro.registrarMarcador(primero,segundo)) System.out.println("Marcador guardado.");
        else System.out.println("Marcador invalido o ya registrado.");
    }
    private void posicion() {
        Torneo torneo = c.seleccionarTorneo();
        if (torneo == null) return;
        System.out.println("Identificador de participante:");
        String id = c.leerTexto();
        System.out.println("Posicion a asignar:");
        int posicion = c.leerEntero(1,torneo.getCupos());
        if (torneo.registrarPosicion(id,posicion)) System.out.println("Posicion registrada.");
        else System.out.println("Posicion invalida o ya ocupada.");
    }
    private void finalizar() {
        Torneo torneo = c.seleccionarTorneo();
        if (torneo == null) return;
        System.out.println("Elige el ganador:");
        InscripcionTorneo ganador = elegirInscripcion(torneo);
        if (ganador == null) return;
        if (!torneo.finalizar(ganador)) { System.out.println("No se pudo finalizar: revisa encuentros pendientes."); return; }
        System.out.println("Torneo finalizado; ganador: " + ganador.getIdentificador());
        if (torneo.getTipo().equals("COMPETITIVO")) {
            TorneoCompetitivo competitivo = (TorneoCompetitivo)torneo;
            System.out.println("Recaudo: $"+competitivo.getTotalRecaudado()+" | Premio en efectivo: $"+competitivo.getPremioEfectivo());
        } else System.out.println("Los bonos de los socios ganadores fueron guardados.");
    }
}
