package ontology;

public class MessageManager {
    
    public MessageManager(){}
    
    public final String extraerAgua(String id, int tramo, int litros){
        return "EXTRAER AGUA: (AgenteIndustria) " + id + " (tramo) " + tramo + " (litros) " + litros;
    } 
    
    public final String extraerAguaReply(String id, int litros){
        return "SE HAN PODIDO EXTRAER: (AgenteIndustria) " + id + " (litros) " + litros;
    }
    
    public final String descargarAgua(int tramo, int litros){
        return "DEPURADORA DESCARGA AGUA LIMPIA AL RIO: (tramo) " + tramo + " (litros) " + litros;
    }
    
    public final String descargarAguaReply(int tramo, int litros){
        return "SE HAN PODIDO DESCARGAR AL RIO: (tramo) " + tramo + " (litros) " + litros;
    }
    
    public final String verterAgua(String id, int tramo, int litros){
        return "INDUSTRIA VIERTE AGUA DIRECTAMENTE AL RIO: (AgenteIndustria) " + id + " (tramo) " + tramo + " (litros) " + litros;
    }
    
    public final String enviaAgua(String id, int litros){
        return "INDUSTRIA ENVIA AGUA A LA DEPURADORA: (AgenteIndustria) " + id + " (litros) " + litros;
    }
    
    public final String aguaAlmacenada(String id, int litros){
        return "LA DEPURADORA HA PODIDO ALMACENAR: (AgenteIndustria) " + id + " (litros) " + litros;
    }
    
    public int getIndice(String[] words){
        for(int i = 0; i<words.length; ++i){
            if(words[i].equals("(AgenteIndustria)"))
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
    
    public String finalRio(String[] words){
        return "HAN LLEGADO " + words[0] + " LITROS DE AGUA " + words[1] + " AL MAR";
    }
    
}
