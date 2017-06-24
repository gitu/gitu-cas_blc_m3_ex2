/* This malicious node could be thought of as being turned off.
 * It never broadcasts any transactions or responds to any
 * communication with other nodes.
 *
 * Note that this is just one example (the simplest one) of a
 * malicious node.
 */

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class CompliantNode implements Node {

    private boolean[] followees;
    private Set<Transaction> pendingTransactions;

    public CompliantNode(double p_graph, double p_malicious, double p_txDistribution, int numRounds) {
    }

    public void receiveCandidates(ArrayList<Integer[]> candidates) {
        for (Integer[] cand : candidates) {
            if (followees[cand[1]]) {
                pendingTransactions.add(new Transaction(cand[0]));
            }
        }
    }

    public Set<Transaction> getProposals() {
        return pendingTransactions;
    }

    public void setFollowees(boolean[] followees) {
        this.followees = followees;
    }

    public void setPendingTransaction(Set<Transaction> pendingTransactions) {
        this.pendingTransactions = pendingTransactions;
    }
}
