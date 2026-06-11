package uy.edu.um.doors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uy.edu.um.tad.list.MyList;

@Data
@NoArgsConstructor
@AllArgsConstructor


public class Evento {
    private String tipo; //cpu,ram,disk
    private MyList<String> instrucciones;




}
