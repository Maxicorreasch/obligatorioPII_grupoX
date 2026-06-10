package uy.edu.um.doors;

import uy.edu.um.tad.list.MyList;

public class Proceso implements Comparable<Proceso> {
    private int PID;
    private String nombre;
    private Usuario propietario;
    private int prioridad;
    private String estado; //new,pending,running,finished
    private MyList<Evento> eventos;

    @Override
    public int compareTo(Proceso otro) {
        return Integer.compare(this.prioridad, otro.prioridad);
    }
}
