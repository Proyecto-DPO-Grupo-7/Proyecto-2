package clubseneca.consola;

import java.util.Date;
import clubseneca.deportes.Categoria;
import clubseneca.deportes.Competencia;
import clubseneca.deportes.Deporte;
import clubseneca.deportes.Equipo;
import clubseneca.deportes.PartidoInterno;
import clubseneca.deportes.PartidoCompetencia;
import clubseneca.deportes.InscripcionCompetencia;
import java.util.List;
import clubseneca.individual.DisciplinaIndividual;
import clubseneca.individual.Instalacion;
import clubseneca.usuarios.Administrador;
import clubseneca.usuarios.Entrenador;
import clubseneca.usuarios.Fisioterapeuta;
import clubseneca.usuarios.InvitadoTorneo;
import clubseneca.usuarios.Socio;
import clubseneca.usuarios.Usuario;

public class AdministracionUsuariosDeportes {
    private ConsolaAdministrador c;
    public AdministracionUsuariosDeportes(ConsolaAdministrador c) { this.c = c; }
    public void crearUsuario() {
        System.out.println("Tipo: 1 Socio, 2 Entrenador, 3 Fisioterapeuta, 4 Estudiante universidad, 5 Externo, 6 Administrador");
        int tipo = c.leerEntero(1,6);
        System.out.println("Login:");
        String login = c.leerTexto();
        if (c.club.getGestionUsuarios().existeLogin(login)) { System.out.println("Login existente."); return; }
        System.out.println("Contraseña:");
        String pass = c.leerTexto();
        Usuario usuario = null;
        if (tipo == 1) usuario = new Socio(login,pass,c.leerFecha());
        if (tipo == 2 || tipo == 3) {
            System.out.println("Codigo de descuento:");
            String codigo = c.leerTexto();
            if (tipo == 2) usuario = new Entrenador(login,pass,codigo);
            if (tipo == 3) usuario = new Fisioterapeuta(login,pass,codigo);
        }
        if (tipo == 4 || tipo == 5) {
            System.out.println("Nivel individual: Principiante, Intermedio o Avanzado:");
            String nivel = c.leerNivel();
            String categoria = "EXTERNO";
            if (tipo == 4) categoria = "ESTUDIANTE";
            InvitadoTorneo invitado = new InvitadoTorneo(login,pass,categoria,"Principiante");
            if (!invitado.cambiarNivel(nivel)) { System.out.println("Nivel invalido."); return; }
            usuario = invitado;
        }
        if (tipo == 6) usuario = new Administrador(login,pass);
        if (c.club.getGestionUsuarios().agregarUsuario(usuario)) System.out.println("Usuario registrado.");
        else System.out.println("No se pudo registrar.");
    }
    public void bajaUsuario() {
        System.out.println("Login del socio:");
        if (c.club.getGestionUsuarios().darDeBajaSocio(c.leerTexto())) System.out.println("Socio dado de baja.");
        else System.out.println("No existe o no es socio.");
    }
    public void crearEquipo() {
        System.out.println("Nombre de equipo:");
        String nombre = c.leerTexto();
        System.out.println("Deporte (Futbol, Baloncesto, Voleibol):");
        String deporte = c.leerTexto();
        int minimo = 0;
        if (deporte.equalsIgnoreCase("Futbol")) minimo = 7;
        if (deporte.equalsIgnoreCase("Baloncesto")) minimo = 5;
        if (deporte.equalsIgnoreCase("Voleibol")) minimo = 6;
        if (minimo == 0) { System.out.println("Deporte desconocido."); return; }
        System.out.println("Categoria (Sub-13, Sub-15, Sub-17, Mayores):");
        String categoria = c.leerTexto();
        int minEdad = 0; int maxEdad = 110;
        if (categoria.equalsIgnoreCase("Sub-13")) { maxEdad = 12; }
        else if (categoria.equalsIgnoreCase("Sub-15")) { minEdad = 13; maxEdad = 14; }
        else if (categoria.equalsIgnoreCase("Sub-17")) { minEdad = 15; maxEdad = 16; }
        else if (categoria.equalsIgnoreCase("Mayores")) { minEdad = 17; }
        else { System.out.println("Categoria invalida."); return; }
        System.out.println("Cupo maximo del equipo:");
        int cupo = c.leerEntero(minimo,100);
        Equipo equipo = new Equipo(nombre,cupo,new Deporte(deporte,minimo),new Categoria(categoria,minEdad,maxEdad));
        if (c.club.getGestionDeportiva().agregarEquipo(equipo)) System.out.println("Equipo creado.");
        else System.out.println("Nombre duplicado.");
    }
    public void crearInstalacion() {
        System.out.println("1 Crear disciplina; 2 Crear instalacion en disciplina existente:");
        int opcion = c.leerEntero(1,2);
        if (opcion == 1) {
            System.out.println("Nombre (Tenis, Padel, Tenis de mesa):");
            String nombre = c.leerTexto();
            System.out.println("Maximo de instalaciones simultaneas:");
            int maximo = c.leerEntero(1,50);
            c.club.getGestionDeportiva().agregarDisciplina(new DisciplinaIndividual(nombre,maximo));
            System.out.println("Disciplina creada.");
            return;
        }
        DisciplinaIndividual disciplina = c.seleccionarDisciplina();
        if (disciplina == null) return;
        System.out.println("Nombre de instalacion:");
        String nombre = c.leerTexto();
        System.out.println("Capacidad:");
        int capacidad = c.leerEntero(1,20);
        System.out.println("¿Techada? 1 Si, 2 No");
        boolean techada = c.leerEntero(1,2) == 1;
        disciplina.agregarInstalacion(new Instalacion(nombre,capacidad,techada,disciplina));
        System.out.println("Instalacion creada.");
    }
    public void bajaEquipo() {
        System.out.println("Equipo a dar de baja:");
        Equipo equipo = c.club.getGestionDeportiva().buscarEquipo(c.leerTexto());
        if (equipo == null) { System.out.println("Equipo no encontrado."); return; }
        equipo.darDeBaja();
        System.out.println("Equipo dado de baja.");
    }
    public void partidoInterno() {
        System.out.println("Primer equipo:");
        Equipo primero = c.club.getGestionDeportiva().buscarEquipo(c.leerTexto());
        System.out.println("Segundo equipo:");
        Equipo segundo = c.club.getGestionDeportiva().buscarEquipo(c.leerTexto());
        if (primero == null || segundo == null) { System.out.println("Equipo no encontrado."); return; }
        PartidoInterno partido = new PartidoInterno(c.leerFecha(),primero,segundo);
        if (!c.club.getGestionDeportiva().registrarPartido(partido)) {
            System.out.println("Partido rechazado: revisa deporte, categoria y habilitados."); return;
        }
        System.out.println("Goles/puntos del primer equipo:");
        int goles1 = c.leerEntero(0,10000);
        System.out.println("Goles/puntos del segundo equipo:");
        int goles2 = c.leerEntero(0,10000);
        partido.registrarMarcador(goles1,goles2);
        System.out.println("Partido interno registrado.");
    }
    public void partidoCompetencia() {
        List<Competencia> competencias = c.club.getGestionDeportiva().getCompetencias();
        if (competencias.isEmpty()) { System.out.println("No hay competencias registradas."); return; }
        for (int i=0;i<competencias.size();i++) System.out.println((i+1)+". "+competencias.get(i).getNombre());
        Competencia competencia = competencias.get(c.leerEntero(1,competencias.size())-1);
        List<InscripcionCompetencia> inscripciones = competencia.getInscripciones();
        if (inscripciones.isEmpty()) { System.out.println("No hay equipos inscritos."); return; }
        for (int i=0;i<inscripciones.size();i++) System.out.println((i+1)+". "+inscripciones.get(i).getEquipo().getNombre());
        InscripcionCompetencia inscripcion = inscripciones.get(c.leerEntero(1,inscripciones.size())-1);
        System.out.println("Nombre del rival:");
        String rival = c.leerTexto();
        System.out.println("Escenario:");
        String escenario = c.leerTexto();
        System.out.println("Fase:");
        String fase = c.leerTexto();
        PartidoCompetencia partido = new PartidoCompetencia(c.leerFecha(),rival,escenario,fase,competencia,inscripcion);
        if (!c.club.getGestionDeportiva().registrarPartido(partido)) { System.out.println("No hay jugadores suficientes."); return; }
        System.out.println("Marcador propio:");
        int propio = c.leerEntero(0,10000);
        System.out.println("Marcador rival:");
        int ajeno = c.leerEntero(0,10000);
        partido.registrarMarcador(propio,ajeno);
        competencia.registrarPartido(partido);
        System.out.println("Partido externo registrado.");
    }
    public void competencia() {
        System.out.println("Nombre del equipo participante (para obtener deporte y categoria):");
        Equipo equipo = c.club.getGestionDeportiva().buscarEquipo(c.leerTexto());
        if (equipo == null) { System.out.println("Equipo no encontrado."); return; }
        System.out.println("Nombre de la competencia:");
        String nombre = c.leerTexto();
        System.out.println("Organizador:");
        String organizador = c.leerTexto();
        System.out.println("Año:");
        int anio = c.leerEntero(2020,2100);
        System.out.println("Formato:");
        String formato = c.leerTexto();
        Competencia competencia = new Competencia(nombre,organizador,anio,formato,equipo.getDeporte(),equipo.getCategoria());
        c.club.getGestionDeportiva().registrarCompetencia(competencia);
        System.out.println("¿Inscribir equipo ahora? 1 Si, 2 No");
        if (c.leerEntero(1,2) == 1) {
            System.out.println("Grupo:");
            boolean exito = c.club.getGestionDeportiva().inscribirEquipoEnCompetencia(equipo,competencia,new Date(),c.leerTexto()) != null;
            if (exito) System.out.println("Equipo inscrito.");
            else System.out.println("Equipo no cumple requisitos de habilitados.");
        }
    }
}
