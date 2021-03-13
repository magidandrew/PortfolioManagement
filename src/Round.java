import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Round {
    public static double round(double toRound, int precision){
        double precisionFactor = Math.pow(10,precision);
        double rounded = Math.round(toRound * precisionFactor)/precisionFactor;
        return rounded;
    }

    public static String roundToString(double doubleToRound){
        NumberFormat numberFormat = new DecimalFormat("#0.00");
        return numberFormat.format(doubleToRound);
    }
}
