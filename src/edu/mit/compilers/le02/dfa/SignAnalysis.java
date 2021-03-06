package edu.mit.compilers.le02.dfa;

/**
 * Lattice for Sign Analysis of Multiplication
 * @author Maria Frendberg (mfrend@mit.edu)
 *
 */
public class SignAnalysis implements Lattice<Sign, Integer> {

  public SignAnalysis() {
    super();
  }

  @Override
  public Sign abstractionFunction(Integer value) {
    if ((Integer) value > 0) {
      return Sign.POS;
    } else if ((Integer) value < 0) {
      return Sign.NEG;
    } else if ((Integer) value == 0) {
      return Sign.ZERO;
    }
    return Sign.BOT;
  }

  @Override
  public Sign transferFunction(Sign[] value) {
    Sign current = Sign.BOT;
    for (Sign next : value) {
      current = current.transferFunction(next);
    }
    return current;
  }

  @Override
  public Sign bottom() {
    return Sign.BOT;
  }

  @Override
  public Sign top() {
    return Sign.TOP;
  }

  @Override
  public Sign leastUpperBound(Sign v1, Sign v2) {
    if (v1.equals(v2)) {
      return v1;
    }

    if (v1.equals(Sign.BOT)) {
      return v2;
    }

    if (v2.equals(Sign.BOT)) {
      return v1;
    }

    return Sign.TOP;
  }

}
