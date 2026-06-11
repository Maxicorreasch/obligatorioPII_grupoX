package uy.edu.um.doors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    private int UID;
    private String alias;
    private String tipo; //admin, generic


}
