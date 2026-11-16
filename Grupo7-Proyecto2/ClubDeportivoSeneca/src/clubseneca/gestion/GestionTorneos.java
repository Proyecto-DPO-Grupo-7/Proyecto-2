package clubseneca.gestion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import clubseneca.deportes.Equipo;
import clubseneca.torneos.EquipoInvitadoTorneo;
import clubseneca.individual.InscripcionDisciplina;
import clubseneca.torneos.InscripcionTorneo;
import clubseneca.torneos.Torneo;
import clubseneca.usuarios.InvitadoTorneo;
import clubseneca.usuarios.Socio;
import clubseneca.usuarios.Usuario;

public class GestionTorneos implements Serializable {
    private List<Torneo> torneos;
    public GestionTorneos() { torneos = new ArrayList<>(); }
    public List<Torneo> getTorneos() { return new ArrayList<>(torneos); }
    public Torneo buscarTorneo(String nombre) {
        if (nombre == null) return null;
        for (Torneo torneo : torneos) if (torneo.getNombre().equalsIgnoreCase(nombre)) return torneo;
        return null;
    }
    public boolean agregarTorneo(Torneo torneo) {
        if (torneo == null || torneo.getNombre() == null || torneo.getNombre().trim().isEmpty()) return false;
        if (buscarTorneo(torneo.getNombre()) != null || torneo.getCupos() < 2 || torneo.getFecha() == null) return false;
        if (torneo.isIndividual()) {
            if (!torneo.getDisciplina().nivelValido(torneo.getNivel())) return false;
        } else {
            if (torneo.getDeporte() == null || torneo.getCategoria() == null || torneo.isDobles()) return false;
        }
        if (torneo.getTipo().equals("RECREATIVO")) {
            if (((clubseneca.torneos.TorneoRecreativo) torneo).getValorBono() <= 0) return false;
        } else {
            clubseneca.torneos.TorneoCompetitivo competitivo = (clubseneca.torneos.TorneoCompetitivo) torneo;
            if (competitivo.getTarifaInscripcion() <= 0) return false;
            if (competitivo.getPorcentajePremio() <= 0 || competitivo.getPorcentajePremio() > 1) return false;
        }
        torneos.add(torneo);
        return true;
    }
    public InscripcionTorneo inscribirEquipo(Torneo torneo, Equipo equipo) {
        if (torneo == null || equipo == null || torneo.isIndividual() || !equipo.isActivo()) return null;
        if (!torneo.getDeporte().getNombre().equalsIgnoreCase(equipo.getDeporte().getNombre())) return null;
        if (!torneo.getCategoria().getNombre().equalsIgnoreCase(equipo.getCategoria().getNombre())) return null;
        if (!equipo.puedeJugarPartido()) return null;
        boolean destacado = torneo.esDestacado(equipo.getNombre());
        InscripcionTorneo inscripcion = new InscripcionTorneo(equipo, destacado, 0);
        if (torneo.getTipo().equals("COMPETITIVO")) {
            inscripcion = new InscripcionTorneo(equipo, destacado, ((clubseneca.torneos.TorneoCompetitivo) torneo).getTarifaInscripcion());
        }
        if (!torneo.inscribir(inscripcion)) return null;
        return inscripcion;
    }
    public InscripcionTorneo inscribirEquipoInvitado(Torneo torneo, EquipoInvitadoTorneo equipo) {
        if (torneo == null || equipo == null || torneo.isIndividual()) return null;
        if (!torneo.getDeporte().getNombre().equalsIgnoreCase(equipo.getDeporte().getNombre())) return null;
        if (!torneo.getCategoria().getNombre().equalsIgnoreCase(equipo.getCategoria().getNombre())) return null;
        if (!equipo.tieneMinimo()) return null;
        boolean destacado = torneo.esDestacado(equipo.getNombre());
        InscripcionTorneo inscripcion = new InscripcionTorneo(equipo,destacado,0);
        inscripcion = new InscripcionTorneo(equipo,destacado,torneo.calcularTarifa(inscripcion));
        if (!torneo.inscribir(inscripcion)) return null;
        return inscripcion;
    }
    public InscripcionTorneo inscribirIndividual(Torneo torneo, List<Usuario> participantes, GestionDeportiva deportiva, GestionTurnos turnos) {
        if (torneo == null || !torneo.isIndividual() || participantes == null) return null;
        int cantidadEsperada = 1;
        if (torneo.isDobles()) cantidadEsperada = 2;
        if (participantes.size() != cantidadEsperada) return null;
        List<String> ids = new ArrayList<>();
        for (Usuario participante : participantes) {
            if (participante == null || ids.contains(participante.getLogin())) return null;
            ids.add(participante.getLogin());
            if (participante.esSocio()) {
                if (!((Socio) participante).isActivo()) return null;
                InscripcionDisciplina inscripcion = deportiva.buscarInscripcion(participante, torneo.getDisciplina());
                if (inscripcion == null || !inscripcion.getNivel().equalsIgnoreCase(torneo.getNivel())) return null;
            } else if (participante.esEmpleado()) {
                if (turnos.estaDeTurno(participante, torneo.getFecha())) return null;
            } else if (participante.esInvitadoTorneo()) {
                if (!((InvitadoTorneo) participante).getNivel().equalsIgnoreCase(torneo.getNivel())) return null;
            } else return null;
        }
        String identificador = participantes.get(0).getLogin();
        if (participantes.size() == 2) identificador += " + " + participantes.get(1).getLogin();
        boolean destacado = torneo.esDestacado(identificador);
        if (!destacado && participantes.size() == 1 && participantes.get(0).esSocio()) {
            Usuario jugador = participantes.get(0);
            destacado = mejorRanking(torneo, jugador);
            if (destacado) torneo.registrarDestacado(identificador);
        }
        InscripcionTorneo inscripcion = new InscripcionTorneo(participantes, destacado, 0);
        double tarifa = torneo.calcularTarifa(inscripcion);
        inscripcion = new InscripcionTorneo(participantes, destacado, tarifa);
        if (!torneo.inscribir(inscripcion)) return null;
        return inscripcion;
    }
    private boolean mejorRanking(Torneo torneo, Usuario candidato) {
        InscripcionDisciplina inscripcion = null;
        for (InscripcionDisciplina actual : torneo.getDisciplina().getInscripciones()) {
            if (actual.getTitular().getLogin().equals(candidato.getLogin())) inscripcion = actual;
        }
        if (inscripcion == null || inscripcion.getRankingElo() <= 1000) return false;
        int total = 0;
        int mejores = 0;
        for (InscripcionDisciplina actual : torneo.getDisciplina().getInscripciones()) {
            if (actual.getNivel().equalsIgnoreCase(torneo.getNivel())) {
                total++;
                if (actual.getRankingElo() > inscripcion.getRankingElo()) mejores++;
            }
        }
        if (total < 2) return false;
        int cuposMejores = (total + 4) / 5;
        return mejores < cuposMejores;
    }
    public boolean retirar(Torneo torneo, String identificador) {
        if (torneo == null) return false;
        return torneo.retirar(identificador);
    }
}
