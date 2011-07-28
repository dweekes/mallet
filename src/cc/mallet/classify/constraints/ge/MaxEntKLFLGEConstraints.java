package cc.mallet.classify.constraints.ge;

public class MaxEntKLFLGEConstraints extends MaxEntFLGEConstraints {
  
  public MaxEntKLFLGEConstraints(int numFeatures, int numLabels, boolean useValues) {
    super(numFeatures, numLabels, useValues);
  }

  public double getValue() {
    double value = 0.0;
    for (int fi : constraints.keys()) {
      MaxEntFLGEConstraint constraint = constraints.get(fi);
      if (constraint.count > 0.0) {

        //if (fi < 100) System.err.println(fi);
        double constraintValue = 0.0;
        for (int labelIndex = 0; labelIndex < numLabels; ++labelIndex) {
          
          //if (fi < 100) System.err.println(constraint.expectation[labelIndex] / constraint.count + " " + constraint.target[labelIndex]);
          
          if (constraint.target[labelIndex] > 0.0) {
            // if target is non-zero and expectation is 0, infinite penalty
            if (constraint.expectation[labelIndex] == 0.0) {
              return Double.NEGATIVE_INFINITY;
            }
            else {
              // p*log(q) - p*log(p)
              // negative KL
              constraintValue += constraint.target[labelIndex] * 
                  (Math.log(constraint.expectation[labelIndex]/constraint.count) - 
                  Math.log(constraint.target[labelIndex]));
            }
          }
        }
        assert(!Double.isNaN(constraintValue) &&
               !Double.isInfinite(constraintValue));

        value += constraintValue * constraint.weight;
      }
    }
    return value;
  }

  @Override
  public void addConstraint(int fi, double[] ex, double weight) {
    constraints.put(fi,new MaxEntKLFLGEConstraint(ex,weight));
  }
  

  protected class MaxEntKLFLGEConstraint extends MaxEntFLGEConstraint {
    public MaxEntKLFLGEConstraint(double[] target, double weight) {
      super(target, weight);
    }

    @Override
    public double getValue(int li) {
      assert(this.count != 0);
      if (this.target[li] == 0 && this.expectation[li] == 0) {
        return 0;
      }
      return this.weight * (this.target[li] / this.expectation[li]);
    }
  }
}
