package shared.tester;

import shared.Instance;

/**
 * A test metric for accuracy.  This metric reports of % correct and % incorrect for a test run.
 * 
 * @author Jesse Rosalia <https://github.com/theJenix>
 * @date 2013-03-05
 */
public class AccuracyTestMetric implements TestMetric {
	StringBuilder builder = new StringBuilder();
    private int count;    
    private int countCorrect;

    @Override
    public void addResult(Instance expected, Instance actual) {
        Comparison c = new Comparison(expected, actual);
        
        count++;
        if (c.isAllCorrect()) {
            countCorrect++;
        }
    }
    
    public double getPctCorrect() {
        return count > 0 ? ((double)countCorrect)/count : 1; //if count is 0, we consider it all correct
    }

    public void printResults() {
        System.out.println(getResults());
    }

	@Override
	public String getResults() {
        //only report results if there were any results to report.
		builder = new StringBuilder();
        if (count > 0) {
            double pctCorrect   = getPctCorrect();
            double pctIncorrect = (1 - pctCorrect);
            builder.append(String.format("Correctly Classified Instances: %.02f%%\n",   100 * pctCorrect));
            builder.append(String.format("Incorrectly Classified Instances: %.02f%%\n", 100 * pctIncorrect));
        } else {

        	builder.append("No results added.\n");
        }
		return builder.toString();
	}
}
