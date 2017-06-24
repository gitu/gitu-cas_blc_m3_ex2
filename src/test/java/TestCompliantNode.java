// Example of a Simulation. This test runs the nodes on a random graph.
// At the end, it will print out the Transaction ids which each node
// believes consensus has been reached upon. You can use this simulation to
// test your nodes. You will want to try creating some deviant nodes and
// mixing them in the network to fully test.

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


@RunWith(Parameterized.class)
public class TestCompliantNode {

    private final int numNodes;
    private final double p_graph;
    private final double p_malicious;
    private final double p_txDistribution;
    private final int numRounds;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        List<Object[]> objects = new LinkedList<>();

        int numNodes = 100;
        for (double p_graph : new double[]{.1, .2, .3}) {
            for (double p_malicious : new double[]{.1, .2, .4}) {
                for (double p_txDistribution : new double[]{.01, .05, .1}) {
                    for (int numRounds : new int[]{10, 20}) {
                        objects.add(new Object[]{numNodes, p_graph, p_malicious, p_txDistribution, numRounds});
                    }
                }
            }
        }

        return objects;
    }

    public TestCompliantNode(int numNodes, double p_graph, double p_malicious, double p_txDistribution, int numRounds) throws IOException {
        this.numNodes = numNodes;
        this.p_graph = p_graph;
        this.p_malicious = p_malicious;
        this.p_txDistribution = p_txDistribution;
        this.numRounds = numRounds;
    }


    @Override
    public String toString() {
        return "{" +
                "numNodes=" + numNodes +
                ", p_graph=" + p_graph +
                ", p_malicious=" + p_malicious +
                ", p_txDistribution=" + p_txDistribution +
                ", numRounds=" + numRounds +
                '}';
    }

    @Test
    public void test() throws IOException {
        System.out.print(this);
        System.out.println();
        System.out.println();

        boolean[][] followees = new boolean[numNodes][numNodes]; // followees[i][j] is true iff i follows j
        boolean[] malicious = new boolean[100];

        BufferedReader br = new BufferedReader(new FileReader("files/" + numRounds + "_" + Math.round(100 * p_malicious) + "_" + Math.max(2,Math.round(10 * p_graph)) + ".txt"));
        try {
            String[] line = br.readLine().split(" ");
            for (String s : line)
                malicious[Integer.parseInt(s)] = true;

            br.readLine();

            for (int i = 0; i < numNodes; i++) {
                line = br.readLine().split(" ");
                for (String s : line)
                    followees[i][Integer.parseInt(s)] = true;
            }
        } finally {
            br.close();
        }

        // pick which nodes are malicious and which are compliant
        Node[] nodes = new Node[numNodes];
        for (int i = 0; i < numNodes; i++) {
            if (malicious[i])
                nodes[i] = new MaliciousNode(p_graph, p_malicious, p_txDistribution, numRounds);
            else
                nodes[i] = new CompliantNode(p_graph, p_malicious, p_txDistribution, numRounds);
        }

        // notify all nodes of their followees
        for (int i = 0; i < numNodes; i++)
            nodes[i].setFollowees(followees[i]);

        // initialize a set of 500 valid Transactions with random ids
        int numTx = 500;
        HashSet<Integer> validTxIds = new HashSet<Integer>();

        HashMap<Integer, Set<Transaction>> allTx = new HashMap<Integer, Set<Transaction>>();
        for (int i = 0; i < numNodes; i++)
            allTx.put(i, new HashSet<Transaction>());

        br = new BufferedReader(new FileReader("files/" + Math.round(100 * p_txDistribution) + "percent_" + Math.round(10 * p_graph) + ".txt"));
        try {
            for (int i = 0; i < numTx; i++) {
                int txID = Integer.parseInt(br.readLine());
                validTxIds.add(txID);
                String[] line = br.readLine().split(" ");
                for (String s : line)
                    allTx.get(Integer.parseInt(s)).add(new Transaction(txID));
            }
        } finally {
            br.close();
        }

        for (int i = 0; i < numNodes; i++) {
            nodes[i].setPendingTransaction(allTx.get(i));
        }

        // Simulate for numRounds times
        Simulation.simulate(numNodes, numRounds, followees, nodes, validTxIds);

        // print results
        System.out.println("Malicious node code in MaliciousNode.java.");
        System.out.println("These are the number of transactions outputted by each of your compliant nodes:");
        for (int i = 0; i < numNodes; i++) {
            if (malicious[i]) continue;
            Set<Transaction> transactions = nodes[i].getProposals();
            System.out.print(transactions.size() + " ");
            Assert.assertEquals("MaliciousNode.java",500, transactions.size());
        }
        System.out.println();
        System.out.println();


        // pick which nodes are malicious and which are compliant
        nodes = new Node[numNodes];
        for (int i = 0; i < numNodes; i++) {
            if (malicious[i])
                nodes[i] = new MalOne(p_graph, p_malicious, p_txDistribution, numRounds);
            else
                nodes[i] = new CompliantNode(p_graph, p_malicious, p_txDistribution, numRounds);
        }

        // notify all nodes of their followees
        for (int i = 0; i < numNodes; i++)
            nodes[i].setFollowees(followees[i]);

        for (int i = 0; i < numNodes; i++) {
            nodes[i].setPendingTransaction(allTx.get(i));
        }

        // Simulate for numRounds times
        Simulation.simulate(numNodes, numRounds, followees, nodes, validTxIds);

        // print results
        System.out.println("Malicious node code in MalOne.java.");
        System.out.println("These are the number of transactions outputted by each of your compliant nodes:");
        for (int i = 0; i < numNodes; i++) {
            if (malicious[i]) continue;
            Set<Transaction> transactions = nodes[i].getProposals();
            System.out.print(transactions.size() + " ");
            Assert.assertEquals("MalOne.java",500, transactions.size());
        }
        System.out.println();
        System.out.println();


        // pick which nodes are malicious and which are compliant
        nodes = new Node[numNodes];
        for (int i = 0; i < numNodes; i++) {
            if (malicious[i])
                nodes[i] = new MalTwo(p_graph, p_malicious, p_txDistribution, numRounds);
            else
                nodes[i] = new CompliantNode(p_graph, p_malicious, p_txDistribution, numRounds);
        }

        // notify all nodes of their followees
        for (int i = 0; i < numNodes; i++)
            nodes[i].setFollowees(followees[i]);

        for (int i = 0; i < numNodes; i++) {
            nodes[i].setPendingTransaction(allTx.get(i));
        }

        // Simulate for numRounds times
        Simulation.simulate(numNodes, numRounds, followees, nodes, validTxIds);

        // print results
        System.out.println("Malicious node code in MalTwo.java.");
        System.out.println("These are the number of transactions outputted by each of your compliant nodes:");
        for (int i = 0; i < numNodes; i++) {
            if (malicious[i]) continue;
            Set<Transaction> transactions = nodes[i].getProposals();
            System.out.print(transactions.size() + " ");
            Assert.assertEquals("MalTwo.java",500, transactions.size());
        }
        System.out.println();
        System.out.println();


        // pick which nodes are malicious and which are compliant
        nodes = new Node[numNodes];
        for (int i = 0; i < numNodes; i++) {
            if (malicious[i])
                nodes[i] = new MalThree(p_graph, p_malicious, p_txDistribution, numRounds);
            else
                nodes[i] = new CompliantNode(p_graph, p_malicious, p_txDistribution, numRounds);
        }

        // notify all nodes of their followees
        for (int i = 0; i < numNodes; i++)
            nodes[i].setFollowees(followees[i]);

        for (int i = 0; i < numNodes; i++) {
            nodes[i].setPendingTransaction(allTx.get(i));
        }
        Simulation.simulate(numNodes, numRounds, followees, nodes, validTxIds);


        // print results
        System.out.println("Malicious node code in MalThree.java.");
        System.out.println("These are the number of transactions outputted by each of your compliant nodes:");
        for (int i = 0; i < numNodes; i++) {
            if (malicious[i]) continue;
            Set<Transaction> transactions = nodes[i].getProposals();
            System.out.print(transactions.size() + " ");
            Assert.assertEquals("MalThree.java",500, transactions.size());
        }
        System.out.println();
        System.out.println();
    }

}

