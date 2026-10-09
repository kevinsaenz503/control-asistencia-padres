package com.saenz.login.model;

import java.time.LocalDateTime;

public class User {
    private Long id;
    private String username;
    private String password;
    private LocalDateTime ingreso;

    public User() {}
    public User(Long id, String username, String password) { this.id=id; this.username=username; this.password=password; }
    public User(Long id, String username, String password, LocalDateTime ingreso) { this(id, username, password); this.ingreso=ingreso; }
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public String getUsername(){return username;} public void setUsername(String username){this.username=username;}
    public String getPassword(){return password;} public void setPassword(String password){this.password=password;}
    public LocalDateTime getIngreso(){return ingreso;} public void setIngreso(LocalDateTime ingreso){this.ingreso=ingreso;}

    public Object getUsuario() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
