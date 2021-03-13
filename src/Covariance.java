import java.util.ArrayList;

public class Covariance {
   private Fund fundA;
   private Fund fundB;
   private double covariance;

    public Covariance(Fund fundA, Fund fundB) {
        this.fundA = fundA;
        this.fundB = fundB;
    }

    public double calculateCovariance(){
        ArrayList<Double> fundADistances = new ArrayList<>(fundA.getDistances().values());
        ArrayList<Double> fundBDistances = new ArrayList<>(fundB.getDistances().values());

        double productOfDistances=0;
        for (int i=0;i<fundADistances.size();i++){
            productOfDistances += fundADistances.get(i) * fundBDistances.get(i);
        }

        //TODO IS IT .size() or .size()-1 ?
        covariance = productOfDistances/(fundADistances.size()-1);
        return covariance;
    }

    public Fund getFundA(){
        return fundA;
    }

    public Fund getFundB(){
        return fundB;
    }

}
