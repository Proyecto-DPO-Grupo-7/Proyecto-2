package clubseneca.torneos;

import java.util.Date;
import clubseneca.deportes.Categoria;
import clubseneca.deportes.Deporte;
import clubseneca.individual.DisciplinaIndividual;
import clubseneca.usuarios.Socio;
import clubseneca.usuarios.InvitadoTorneo;
import clubseneca.usuarios.Usuario;

public class TorneoRecreativo extends Torneo {
    private double valorBono;

    public TorneoRecreativo(String nombre, Date fecha, Deporte deporte, Categoria categoria, DisciplinaIndividual disciplina, String nivel, boolean dobles, int cupos, double valorBono) {
        super(nombre, fecha, deporte, categoria, disciplina, nivel, dobles, cupos);
        this.valorBono = valorBono;
    }
    public String getTipo() { return "RECREATIVO"; }
    public double getValorBono() { return valorBono; }
    public double calcularTarifa(InscripcionTorneo inscripcion) { return 0; }
    protected void entregarPremio(InscripcionTorneo ganador) {
        if (ganador.getEquipo() != null) {
            for (clubseneca.deportes.JugadorEquipo jugador : ganador.getEquipo().getJugadores()) {
                jugador.getSocio().agregarBono(new BonoDescuento(getNombre(), jugador.getSocio(), valorBono));
            }
        } else {
            for (Usuario usuario : ganador.getJugadores()) {
                if (usuario.esSocio()) {
                    Socio socio = (Socio) usuario;
                    socio.agregarBono(new BonoDescuento(getNombre(), socio, valorBono));
                }
                if (usuario.esInvitadoTorneo()) {
                    InvitadoTorneo invitado = (InvitadoTorneo) usuario;
                    invitado.agregarBono(new BonoDescuento(getNombre(), invitado, valorBono));
                }
            }
        }
    }
}
