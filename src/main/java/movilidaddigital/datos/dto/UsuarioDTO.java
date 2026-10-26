package movilidaddigital.datos.dto;

public class UsuarioDTO {
    private final String nombre;
    private final String telefono;
    private final String email;
    private final String licenciaConducir;
    private final String patentes;

    public UsuarioDTO(String nombre, String telefono, String email,
                      String licenciaConducir, String patentes) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.email = email;
        this.licenciaConducir = licenciaConducir;
        this.patentes = patentes;
    }

    public String getNombre() { return nombre; }
    public String getTelefono() { return telefono; }
    public String getEmail() { return email; }
    public String getLicenciaConducir() { return licenciaConducir; }
    public String getPatentes() { return patentes; }
}
