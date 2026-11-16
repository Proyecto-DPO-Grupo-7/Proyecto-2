package clubseneca.persistencia;

import java.io.Serializable;
import clubseneca.gestion.GestionComercial;
import clubseneca.gestion.GestionDeportiva;
import clubseneca.gestion.GestionTorneos;
import clubseneca.gestion.GestionTurnos;
import clubseneca.gestion.GestionUsuarios;

public class EstadoClub implements Serializable {
    private GestionUsuarios usuarios;
    private GestionDeportiva deportiva;
    private GestionComercial comercial;
    private GestionTurnos turnos;
    private GestionTorneos torneos;

    public EstadoClub(GestionUsuarios usuarios, GestionDeportiva deportiva, GestionComercial comercial, GestionTurnos turnos, GestionTorneos torneos) {
        this.usuarios = usuarios;
        this.deportiva = deportiva;
        this.comercial = comercial;
        this.turnos = turnos;
        this.torneos = torneos;
    }
    public GestionUsuarios getUsuarios() { return usuarios; }
    public GestionDeportiva getDeportiva() { return deportiva; }
    public GestionComercial getComercial() { return comercial; }
    public GestionTurnos getTurnos() { return turnos; }
    public GestionTorneos getTorneos() { return torneos; }
}
