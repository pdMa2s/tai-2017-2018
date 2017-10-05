
import java.util.*;

public class ProbabilityModel {
    private Map<String, Map<Character, Double>> probabilityMultiModel;
    private Map<Character, Double> probabilityUniModel;

    private Set<Character> alphabet;
    private ContextModel contextModel;
    private double alpha;

    public ProbabilityModel(ContextModel model, double alpha){
        this.alphabet = model.getAlphabet();
        this.contextModel = model;
        this.alpha = alpha;

        probabilityUniModel = new HashMap<>();
        fillProbabilityUniModel();

        if(model.getOrder() > 0){
            probabilityMultiModel = new HashMap<>();
            fillProbabilityMultiModel();
        }

    }

    public double entropy(char caracter){
        if(contextModel.getOrder() == 0){
            return rowEntropy(probabilityUniModel);
        }

        int totalContextOcurrences = contextModel.totalContextOcurrences();
        double entropy = 0;
        /*for (String term : dictionary) {
            Map<Character, Double> row = probabilityMultiModel.get(term);
            int totalRowOcurrences = getTotalOcurencesOfRow(contextModel.getOcurrencesForOrderHigherThanZero(term));
            entropy += rowEntropy(row)*(totalRowOcurrences/totalContextOcurrences);
        }*/
        return entropy;
    }

    private double rowEntropy(Map<Character, Double> row){

        double rowEntropy = 0;
        for(Map.Entry<Character, Double> entry: row.entrySet()){
            rowEntropy += charEntropy(entry.getValue());
        }

        return rowEntropy;
    }

    private double charEntropy(double prob){
        return -(prob*log2(prob));
    }

    private void fillProbabilityMultiModel(){
        for(String term : contextModel.getTermsForOrderHigherThanZero()) {
            Map<Character, Integer> ocurrences = contextModel.getOcurrencesForOrderHigherThanZero(term);
            int totalOcurrences = getTotalOcurencesOfRow(ocurrences);
            for (char c : alphabet) {
                Integer nrOcurrences = ocurrences.get(c);
                if(nrOcurrences == null)
                    nrOcurrences = 0;
                fillCharProbabilities(term, c, totalOcurrences, nrOcurrences);
            }
        }

    }

    private void fillProbabilityUniModel(){
        Map<Character, Integer> ocurrences = contextModel.getOcurrencesForOrderEqualToZero();
        System.out.println(ocurrences);
        int totalOcurrences = getTotalOcurencesOfRow(ocurrences);
        for(Character term : ocurrences.keySet()) {
            probabilityUniModel.put(term, probabilityOfAChar(totalOcurrences, ocurrences.get(term)));
        }
    }

    private void fillCharProbabilities(String term ,char c,int total, int nrOcurrences){
        Map<Character, Double> probabilities;
        if(probabilityMultiModel.containsKey(term)) 
            probabilities = probabilityMultiModel.get(term);
        
        else
            probabilities = new HashMap<>();
        
        probabilities.put(c,probabilityOfAChar(total, nrOcurrences));
        probabilityMultiModel.put(term,probabilities);
    }
    private int getTotalOcurencesOfRow(Map<Character, Integer> ocurrences){
        int sum = 0;
        for(Map.Entry<Character, Integer> entry : ocurrences.entrySet()){
            sum += entry.getValue();
        }
        return sum;
    }


    private double probabilityOfAChar(int total, int nrOcurrences){
        return (nrOcurrences + alpha)/(total +(alpha *alphabet.size()));
    }
    private double log2( double a )
    {
        return logb(a,2);
    }
    private double logb( double a, double b )
    {
        return Math.log(a) / Math.log(b);
    }

    @Override
    public String toString(){
        if(contextModel.getOrder() == 0)
            return probabilityUniModel.toString();
        StringBuilder sb = new StringBuilder();
        for(String k : probabilityMultiModel.keySet()){
            sb.append(k+":"+probabilityMultiModel.get(k)+"\n");
        }
        return sb.toString();
    }
}
