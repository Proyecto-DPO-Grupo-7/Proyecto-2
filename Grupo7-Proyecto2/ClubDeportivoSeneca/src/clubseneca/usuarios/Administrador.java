package clubseneca.usuarios;

public class Administrador extends Usuario {
    public boolean esAdministrador() { return true; }
    public Administrador(String login, String password) {
        super(login, password);
    }
}
