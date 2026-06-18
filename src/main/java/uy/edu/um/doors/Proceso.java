package uy.edu.um.doors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uy.edu.um.tad.list.MyList;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class Proceso implements Comparable<Proceso> {
    private int PID;
    private String nombre;
    private Usuario propietario;
    private int prioridad;
    private String estado; //new,pending,running, ok, error, terminated
    private MyList<Evento> eventos;


    public Proceso(int PID, String nombre, Usuario propietario, MyList<Evento> eventos) {
        this.PID = PID;
        this.nombre = nombre;
        this.propietario = propietario;
        this.eventos = eventos;
        this.prioridad = 0;
        this.estado = "NEW";
    }

    public int cantidadEventosCPU(){
        int cont = 0;

        for(int i = 0; i < eventos.size(); i++){
            Evento e = eventos.get(i);

            if(e.getTipo().equalsIgnoreCase("CPU")){
                cont++;
            }
        }
        return cont;

    }

    public int cantidadEventosRAM(){
        int cont = 0;
        for(int i = 0; i < eventos.size(); i++){
            Evento e = eventos.get(i);

            if(e.getTipo().equalsIgnoreCase("RAM")){
                cont++;
            }
        }
        return cont;
    }

    public int cantidadEventosDISK(){
        int cont = 0;
        for(int i = 0; i < eventos.size(); i++){
            Evento e = eventos.get(i);

            if(e.getTipo().equalsIgnoreCase("DISK")){
                cont++;
            }
        }
        return cont;

    }

    public void calcularPrioridad(){
        int cpu = this.cantidadEventosCPU();
        int ram = this.cantidadEventosRAM();
        int disk = this.cantidadEventosDISK();
        int totalEventos = eventos.size();
        int pesoUsuario;

        if(propietario.getTipo().equalsIgnoreCase("ADMIN")){
            pesoUsuario = 32;
        }else {
            pesoUsuario = 16;
        }

        prioridad = ((8 * cpu) + (2* ram) + (2 * disk))/totalEventos + (pesoUsuario * totalEventos);
    }

    @Override
    public int compareTo(Proceso otro) {
        return Integer.compare(this.prioridad, otro.prioridad);
    }
}


