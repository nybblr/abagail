package shared;


/**
 * A data set is just a collection of instances
 * @author Andrew Guillory gtg008g@mail.gatech.edu
 * @version 1.0
 */
public class DataSet {
    /**
     * The list of instances
     */
    private Instance[] instances;
    
    /**
     * The description of the data set
     */
    private DataSetDescription description;
    
    /**
     * Make a new data set from the given instances
     * @param instances the instances
     * @param description the data set description
     */
    public DataSet(Instance[] instances, DataSetDescription description) {
        this.instances = instances;
        this.description = description;
    }
    
    /**
     * Make a new data set with the given instances
     * @param instances the instances
     */
    public DataSet(Instance[] instances) {
        this.instances = instances;
    }
    
    /**
     * Get the size of the data set
     * @return the size of the data set
     */
    public int size() {
        return instances.length;
    }
    
    /**
     * Get the ith instance
     * @param i the index
     * @return the instance
     */
    public Instance get(int i) {
        return instances[i];
    }
    
    /**
     * Set the ith instance
     * @param i the index
     * @param instance the new instance
     */
    public void set(int i, Instance instance) {
        instances[i] = instance;
    }
    
    /**
     * Get the description of the data set
     * @return the description
     */
    public DataSetDescription getDescription() {
        return description;
    }
    
    /**
     * Set the description
     * @param description the new description
     */
    public void setDescription(DataSetDescription description) {
        this.description = description;
    }
    
    /**
     * Get the instances
     * @return the instances
     */
    public Instance[] getInstances() {
        return instances;
    }

    /**
     * Set the instances
     * @param instances the instances
     */
    public void setInstances(Instance[] instances) {
        this.instances = instances;
    }
    
    /**
     * Get the label {@linkplain DataSet}
     * @return the label {@linkplain DataSet}
     */
    public DataSet getLabelDataSet() {
    	if (instances[0].getLabel() != null) {
	        Instance[] labels = new Instance[instances.length];
	        for (int i = 0; i < labels.length; i++) {
	            labels[i] = instances[i].getLabel();
	            if (labels[i].getWeight() == 1.0) {
	                labels[i].setWeight(instances[i].getWeight());
	            }
	        }
	        DataSetDescription labelDescription = null;
	        if (description != null) {
	            labelDescription = description.getLabelDescription();
	        }
	        	return new DataSet(labels, labelDescription);
	    	}
    	return null;
    }
    
    /**
     * Set the label {@linkplain DataSet}
     * @param labels the labels to set
     */
    public void setLabelDataSet(DataSet labelSet) {
    	Instance[] labels = labelSet.getInstances();
    	for (int i = 0; i < labels.length; i++) {
    		instances[i].setLabel(labels[i]);
    	}
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        String result = "Description:\n" + description + "\n";
        for (int i = 0; i < instances.length; i++) {
            result += instances[i] + "\n";
        }
        return result;
    }


}
