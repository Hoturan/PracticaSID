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

    // avanza todas las masas de agua del rio 1 posicion
    public void avanzarCurso(){
           for(int i = flow.length-1; i>0; --i){
                MasaDeAgua mtemp = flow[i-1];
                mtemp.aumentarPosicion();
                flow[i] = mtemp;
            }
            MasaDeAgua mNew = new MasaDeAgua();
            flow[0]= mNew; // al rio siempre le llega agua limpia*/
    }


    // devuelve un entero informando de los millones de litros extraidos
    public int extraerAgua(int index, int volumenExtraer){
          MasaDeAgua mtemp = flow[index];
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

    // descarga agua al rio (teoricamente limpia)
    public void descargarAgua(int index, MasaDeAgua masaDescarga){
            MasaDeAgua mtemp = flow[index];
            mtemp.mezclaMasasDeAgua(masaDescarga);
            flow[index] = mtemp;
        }
}