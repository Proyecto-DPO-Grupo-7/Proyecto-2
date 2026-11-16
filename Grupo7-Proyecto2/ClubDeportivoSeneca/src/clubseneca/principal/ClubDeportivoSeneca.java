package clubseneca.principal;

import clubseneca.comercial.Venta;
import clubseneca.gestion.GestionComercial;
import clubseneca.gestion.GestionDeportiva;
import clubseneca.gestion.GestionTurnos;
import clubseneca.gestion.GestionTorneos;
import clubseneca.persistencia.EstadoClub;
import clubseneca.gestion.GestionUsuarios;
import clubseneca.individual.Instalacion;
import clubseneca.persistencia.PersistenciaClub;
import clubseneca.usuarios.Socio;
import clubseneca.usuarios.Usuario;

public class ClubDeportivoSeneca {
    private GestionUsuarios gestionUsuarios;
    private GestionDeportiva gestionDeportiva;
    private GestionComercial gestionComercial;
    private GestionTurnos gestionTurnos;
    private GestionTorneos gestionTorneos;
    private PersistenciaClub persistencia;

    public ClubDeportivoSeneca() {
        this("datos");
    }

    public ClubDeportivoSeneca(String carpetaDatos) {
        gestionUsuarios = new GestionUsuarios();
        gestionDeportiva = new GestionDeportiva();
        gestionComercial = new GestionComercial();
        gestionTurnos = new GestionTurnos();
        gestionTorneos = new GestionTorneos();
        persistencia = new PersistenciaClub(carpetaDatos);
    }

    public GestionUsuarios getGestionUsuarios() {
        return gestionUsuarios;
    }

    public GestionDeportiva getGestionDeportiva() {
        return gestionDeportiva;
    }

    public GestionComercial getGestionComercial() {
        return gestionComercial;
    }

    public GestionTurnos getGestionTurnos() {
        return gestionTurnos;
    }

    public GestionTorneos getGestionTorneos() { return gestionTorneos; }
    public void cargarDatos() {
        EstadoClub estado = persistencia.cargarEstado();
        if (estado != null) {
            gestionUsuarios = estado.getUsuarios();
            gestionDeportiva = estado.getDeportiva();
            gestionComercial = estado.getComercial();
            gestionTurnos = estado.getTurnos();
            gestionTorneos = estado.getTorneos();
            return;
        }
        gestionUsuarios = persistencia.cargarUsuarios();
        gestionDeportiva = persistencia.cargarDeportiva();
        gestionComercial = persistencia.cargarComercial();
        gestionTurnos = persistencia.cargarTurnos();
    }

    public void guardarDatos() {
        persistencia.guardarEstado(new EstadoClub(gestionUsuarios, gestionDeportiva, gestionComercial, gestionTurnos, gestionTorneos));
        persistencia.guardarUsuarios(gestionUsuarios);
        persistencia.guardarDeportiva(gestionDeportiva);
        persistencia.guardarComercial(gestionComercial);
        persistencia.guardarTurnos(gestionTurnos);
    }

    public boolean registrarCompra(Usuario comprador, Venta venta, Instalacion instalacionActual) {
        if (comprador == null || venta == null) {
            return false;
        }

        if (!comprador.esSocio() && !comprador.esEmpleado() && !comprador.esInvitadoTorneo()) {
            return false;
        }

        if (venta.getComprador() != comprador) {
            return false;
        }

        if (comprador.esSocio() && venta.contieneBebidaCaliente()) {
            if (instalacionActual != null && instalacionActual.restringeBebidaCaliente()) {
                return false;
            }

        }
        boolean registrada = gestionComercial.registrarVenta(venta);
        if (registrada && comprador.esSocio() && venta.contieneBebidaCaliente()) {
            Socio socio = (Socio) comprador;
            socio.setTieneBebidaCaliente(true);
        }
        return registrada;
    }
}
