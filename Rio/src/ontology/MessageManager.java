package ontology;

public class MessageManager {
    
    public MessageManager(){}
    
    public final String extraerAgua(int tramo, int litros){
        return "EXTRAER AGUA: (tramo) " + tramo + " (litros) " + litros;
    } 
    
    public final String extraerAguaReply(int id, int litros){
        return "SE HAN PODIDO EXTRAER: (litros) " + litros;
    }
    
    public final String descargarAgua(int tramo, int litros){
        return "DEPURADORA DESCARGA AGUA LIMPIA AL RIO: (tramo) " + tramo + " (litros) " + litros;
    }
    
    public final String descargarAguaReply(int tramo, int litros){
        return "SE HAN PODIDO DESCARGAR AL RIO: (tramo) " + tramo + " (litros) " + litros;
    }
    
    public final String verterAgua(int id, int tramo, int litros){
        return "INDUSTRIA VIERTE AGUA DIRECTAMENTE AL RIO: (Industria) " + id + " (tramo) " + tramo + " (litros) " + litros;
    }
    
    public final String enviaAgua(int litros){
        return "INDUSTRIA ENVIA AGUA A LA DEPURADORA: (litros) " + litros;
    }
    
    public final String aguaAlmacenada(int litros){
        return "LA DEPURADORA HA PODIDO ALMACENAR: (litros) " + litros;
    }
    
    public int getIndice(String[] words){
        for(int i = 0; i<words.length; ++i){
            if(words[i].equals("(Industria)"))
                return Integer.parseInt(words[i+1]);
        }
        return -1;
    }
    
    public int getTramo(String[] words){
        for(int i = 0; i<words.length; ++i){
            if(words[i].equals("(tramo)"))
                return Integer.parseInt(words[i+1]);
        }
        return -1;
    }
    
    public int getLitros(String[] words){
        for(int i = 0; i<words.length; ++i){
            if(words[i].equals("(litros)"))
                return Integer.parseInt(words[i+1]);
        }
        return -1;
    }
    
}
