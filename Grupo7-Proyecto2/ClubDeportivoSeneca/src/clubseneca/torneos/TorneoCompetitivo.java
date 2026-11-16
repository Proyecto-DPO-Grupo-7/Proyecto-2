package clubseneca.torneos;

import java.util.Date;
import clubseneca.deportes.Categoria;
import clubseneca.deportes.Deporte;
import clubseneca.individual.DisciplinaIndividual;
import clubseneca.usuarios.Usuario;

public class TorneoCompetitivo extends Torneo {
    private double tarifaInscripcion;
    private double porcentajePremio;
    private double premioEfectivo;

    public TorneoCompetitivo(String nombre, Date fecha, Deporte deporte, Categoria categoria, DisciplinaIndividual disciplina, String nivel, boolean dobles, int cupos, double tarifaInscripcion, double porcentajePremio) {
        super(nombre, fecha, deporte, categoria, disciplina, nivel, dobles, cupos);
        this.tarifaInscripcion = tarifaInscripcion;
        this.porcentajePremio = porcentajePremio;
        premioEfectivo = 0;
    }
    public String getTipo() { return "COMPETITIVO"; }
    public double getTarifaInscripcion() { return tarifaInscripcion; }
    public double getPorcentajePremio() { return porcentajePremio; }
    public double getTotalRecaudado() {
        double total = 0;
        for (InscripcionTorneo inscripcion : getInscripciones()) total += inscripcion.getTarifa();
        return total;
    }
    public double getPremioEfectivo() { return premioEfectivo; }
    public double calcularTarifa(InscripcionTorneo inscripcion) {
        if (inscripcion.getEquipo() != null || inscripcion.getEquipoInvitado() != null) return tarifaInscripcion;
        int cantidad = inscripcion.getJugadores().size();
        if (cantidad == 0) return 0;
        int empleados = 0;
        for (Usuario jugador : inscripcion.getJugadores()) if (jugador.esEmpleado()) empleados++;
        return tarifaInscripcion * (cantidad - empleados) / cantidad;
    }
    public double premioPara(Usuario usuario) {
        if (usuario == null || usuario.esEmpleado() || getGanador() == null) return 0;
        if (!getGanador().contieneJugador(usuario)) return 0;
        int elegibles = 0;
        for (Usuario integrante : getGanador().getJugadores()) if (!integrante.esEmpleado()) elegibles++;
        if (elegibles == 0) return 0;
        return premioEfectivo / elegibles;
    }
    protected void entregarPremio(InscripcionTorneo ganador) {
        premioEfectivo = getTotalRecaudado() * porcentajePremio;
        if (ganador.incluyeEmpleado() && ganador.getJugadores().size() == 1) premioEfectivo = 0;
    }
}
