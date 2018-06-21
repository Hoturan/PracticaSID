package ontology;

public class Rio{
	
    private final MasaDeAgua[] flow;

    public Rio(int size){
        flow = new MasaDeAgua[size];
        for(int i = 0; i<size; ++i) {
            MasaDeAgua mtemp = new MasaDeAgua();
            mtemp.setPosicion(i);
            flow[i] = mtemp;
        }
    }

    private void muestraRio(){
        System.out.println("ESTADO DEL RIO:");
        for(int i = 0; i<flow.length; ++i){
            System.out.print(String.valueOf(flow[i].getVolumen()) + " >> ");
        }
        System.out.println("MAR");
    }
    
    // avanza todas las masas de agua del rio 1 posicion
    public String avanzarCurso(){
            System.out.println("RIO AVANZA CURSO");
            // buscamos si hay un hueco
            int found = -1;
            for(int i = 0; i<flow.length && found == -1; ++i){
                if(flow[i].getVolumen() == 0) found = i;
            }
            if(found != -1){
                for(int i = found; i>0; --i){
                    flow[i] = flow[i-1];
                    flow[i].aumentarPosicion();
                }
            }
            
           String msg = "";
           msg += flow[flow.length-1].getVolumen();
           if(flow[flow.length-1].aguaLimpia()) msg += " LIMPIA ";
           else msg += " SUCIA ";
            
           for(int i = flow.length-1; i>0; --i){
                MasaDeAgua mtemp = flow[i-1];
                mtemp.aumentarPosicion();
                flow[i] = mtemp;
            }
            MasaDeAgua mNew = new MasaDeAgua();
            flow[0]= mNew; // al rio siempre le llega agua limpia*/
            muestraRio();
            
            return msg;
    }


    // devuelve un entero informando de los millones de litros extraidos
    public int extraerAgua(int index, int volumenExtraer){
          MasaDeAgua mtemp = flow[index];
          if(mtemp.aguaLimpia()){ 
            int volumenActual = mtemp.getVolumen();
            if(volumenActual == 0) return 0;
            if(volumenActual >= volumenExtraer){
                volumenActual -= volumenExtraer;
                mtemp.setVolumen(volumenActual);
                flow[index] = mtemp;
                return volumenExtraer;
            }
            else{
                mtemp.setVolumen(0);
                flow[index] = mtemp;
                return volumenActual;
            }
          }
          else return 0;
    }

    // descarga agua al rio (teoricamente limpia)
    public void descargarAgua(int index, int litrosDescarga){
            MasaDeAgua mtemp = flow[index];
            mtemp.setVolumen(mtemp.getVolumen() + litrosDescarga);
            flow[index] = mtemp;
    }
    
    public void verterAgua(int tramo, int litros, int gradoContaminacion){
        MasaDeAgua aguaVertida = new MasaDeAgua();
        aguaVertida.setVolumen(litros);
        aguaVertida.contaminaAgua(gradoContaminacion);
        flow[tramo].mezclaMasasDeAgua(aguaVertida);
    }
}