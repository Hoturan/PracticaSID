package ontology;

import java.util.List;

public class Rio{
	
    private List<MasaDeAgua> flow;

    public Rio(int size){
        /*for(int i = 0; i<size; ++i) {
            MasaDeAgua mtemp = new MasaDeAgua();
            mtemp.setPosicion(i);
            flow.add(mtemp);
        }*/
    }

    // avanza todas las masas de agua del rio 1 posicion
    public void avanzarCurso(){
           /* for(int i = flow.size()-1; i>0; --i){
                MasaDeAgua mtemp = flow.get(i-1);
                mtemp.aumentarPosicion();
                flow.set(i, mtemp);
            }
            MasaDeAgua mNew = new MasaDeAgua();
            flow.set(0, mNew); // al rio siempre le llega agua limpia*/
    }


    // devuelve un entero informando de los millones de litros extraidos
    public int extraerAgua(int index, int volumenExtraer){
          MasaDeAgua mtemp = flow.get(index);
          int volumenActual = mtemp.getVolumen();
          if(volumenActual == 0) return 0;
          if(volumenActual >= volumenExtraer){
              volumenActual -= volumenExtraer;
              mtemp.setVolumen(volumenActual);
              flow.set(index, mtemp);
              return volumenExtraer;
          }
          else{
              mtemp.setVolumen(0);
              flow.set(index,mtemp);
              return volumenActual;
          }
    }

    // descarga agua al rio (teoricamente limpia)
    public void descargarAgua(int index, MasaDeAgua masaDescarga){
            MasaDeAgua mtemp = flow.get(index);
            mtemp.mezclaMasasDeAgua(masaDescarga);
            flow.set(index, mtemp);
        }
}