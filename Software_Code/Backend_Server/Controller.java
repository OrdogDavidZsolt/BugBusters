/**
 * Ez a fájl indítja el a teljes backend szervert
 * A többi kódban csak metódusok (API-k) legyenek, main nem kell!
 */

package Software_Code.Backend_Server;

public class Controller {
    
    public static void main(String[] args) {
        
    }

    public static void processID(String CardID)
    {
        /* Ez a metódus feldolgozza a kártyaolvasótól érkező adatokat
         *
         * Paraméter: String CardID -> a kártyaolvasó által küldött kártyaazonosító 
         * Meghívja: HW_Connection.java osztály
         * Feladata: Az adatbázisból kideríti lekérdezés(ek)-el, hogy tanár vagy
         *           hallgató ID-t jelent a CardID paraméter.
         *           Ezalapján vagy elindítja a timert (20p) és új konténert hoz
         *           létre a további kártyák tárolására
         *           vagy pedig (ha hallgatóról van szó) hozzáadja az adatokat a
         *           megfelelő tárolóhoz.
         */

        // teszt jellegű kiíratás
        System.out.println(CardID);
    }
}
