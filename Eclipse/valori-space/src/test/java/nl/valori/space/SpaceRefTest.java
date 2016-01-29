package nl.valori.space;

import junit.framework.TestCase;

public class SpaceRefTest extends TestCase {

    private class Referent {
    }

    private class Referrer {
	private SpaceRef<Referent> reference;

	public Referrer() {
	    reference = new SpaceRef<Referent>();
	}

	public Referent getReferent() {
	    return reference.get();
	}

	public void setReferent(Referent target) {
	    this.reference.set(target);
	}
    }

    public void test() throws Exception {
	Referent referent1 = new Referent();
	Referrer x = new Referrer();
	x.setReferent(referent1);

	Referent referent2 = x.getReferent();
	assertSame("referent1 and target2 are not the same", referent1, referent2);

	Space space = Space.getInstance();
	SpaceId referentId = space.put(x.getReferent());
	Referent referent3 = (Referent) space.read(referentId);
	assertSame("referent1 and target3 are not the same", referent1, referent3);
    }
}