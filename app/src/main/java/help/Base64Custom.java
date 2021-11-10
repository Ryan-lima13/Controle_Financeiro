package help;

import android.util.Base64;

public class Base64Custom {

    public static String codificarBase64(String texto){
        return Base64.encodeToString(texto.getBytes(),Base64.DEFAULT).replaceAll("(\\n|\\r)", "");


    }
    public static String decofificarBase64(String textoDecodificar){
        return  new String( Base64.decode(textoDecodificar,Base64.DEFAULT));

    }
}
