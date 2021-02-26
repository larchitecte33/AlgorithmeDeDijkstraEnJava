import java.util.HashMap;
import java.util.Stack;

public class Main {

    static int currentNode;
    static int minCout;
    static int minNode;
    static int indexMaxTabListeChemins = 0;
    static int numPileAModifier;

    static int SAINE = 1;
    static int CASCONTACT = 2;
    static int CONTAMINE = 3;

    static int[][] matriceAdjacente = {
            {-1, 2, -1, -1, 1, -1, -1, -1, -1}, // noeud 0
            {2, -1, 3, -1, -1, 1, -1, -1, -1}, // noeud 1
            {-1, 3, -1, 7, -1, -1, -1, -1, 2}, // noeud 2
            {-1, -1, 7, -1, 4, 5, -1, 1, -1}, // noeud 3
            {1, -1, -1, 4, -1, -1, 5, -1, -1}, // noeud 4
            {-1, 1, -1, 5, -1, -1, 9, -1, -1}, // noeud 5
            {-1, -1, -1, -1, 5, 9, -1, 3, -1}, // noeud 6
            {-1, -1, -1, 1, -1, -1, 3, -1, 8}, // noeud 7
            {-1, -1, 2, -1, -1, -1, -1, 8, -1} // noeud 8
    };

    static int[] maisonsSainesContactContam = {SAINE, CASCONTACT, CONTAMINE, SAINE, CASCONTACT, CONTAMINE, SAINE, CASCONTACT, CASCONTACT};

    public static class IntegerResult {
        private int result;

        public void setResult(int value) {
            result = value;
        }
    }

    public static void visuPiles(Stack<Integer>[] tabListesChemins) {
        int i = 0, poids = 0;

        // Pour chacune des piles contenant une liste de chemins
        while(tabListesChemins[i] != null) {
            System.out.print("Pile " + i + " : ");

            for(int j = 0 ; j < tabListesChemins[i].size() ; j++) {
                System.out.print(tabListesChemins[i].get(j));
            }

            poids = 0;

            for(int j = 1 ; j < tabListesChemins[i].size() ; j++) {
                poids = poids + matriceAdjacente[tabListesChemins[i].get(j - 1)][tabListesChemins[i].get(j)];
            }

            System.out.print("   " + poids);

            System.out.println();

            i++;
        }

        System.out.println("--------------------");
    }

    public static boolean listeCheminsContienChemin(Stack<Integer>[] tabListesChemins, Stack<Integer> chemin) {
        boolean cheminTrouve = false;
        int i = 0;

        while((i < indexMaxTabListeChemins + 1) && (!cheminTrouve)) {
            cheminTrouve = false;

            if(tabListesChemins[i].size() < chemin.size())
                cheminTrouve = false;
            else {
                for (int j = 0; j < chemin.size(); j++) {
                    cheminTrouve = true;

                    if (tabListesChemins[i].get(j) != chemin.get(j))
                        cheminTrouve = false;
                }
            }

            i++;
        }

        return cheminTrouve;
    }

    public static int getNoeudCoutMoinsEleve(HashMap<Integer, Integer> nodesToExplorePoids, Stack<Integer>[] tabListesChemins,
                                             int[][] matriceAdjacente) {
        minCout = 1000000;
        IntegerResult poidsCheminSortie = new IntegerResult();
        poidsCheminSortie.setResult(0);

        // On parcourt tous les noeuds à explorr
        nodesToExplorePoids.forEach((node, cout) -> {
            // On parcourt toutes les listes de chemins
            for(int i = 0 ; i < indexMaxTabListeChemins + 1 ; i++) {
                // Si le noeud est compatible avec le chemin en cours
                if(cheminEstCompatible(tabListesChemins[i], node, matriceAdjacente, poidsCheminSortie, tabListesChemins)) {

                    // Si le poids du chemin, en y ajoutant le noeud, est inférieur au poids minimum calculé,
                    // le noeud ayant un coût minimum correspond au noeud actuellement parcourut
                    if(poidsCheminSortie.result < minCout) {
                        minCout = poidsCheminSortie.result;
                        minNode = node;
                        numPileAModifier = i;
                    }
                }
            }
        });

        nodesToExplorePoids.put(minNode, minCout);

        return minNode;
    }

    public static boolean estVoisin(int node1, int node2, int[][] matriceAdjacente) {
        if(matriceAdjacente[node1][node2] > -1)
            return true;
        else
            return false;
    }

    public static int getPoids(int node1, int node2, int[][] matriceAdjacente) {
        return matriceAdjacente[node1][node2];
    }

    public static int getEtatContamination(int maison) {
        if(maisonsSainesContactContam[maison] == SAINE)
            return 0;
        else if(maisonsSainesContactContam[maison] == CASCONTACT)
            return 4;
        else if(maisonsSainesContactContam[maison] == CONTAMINE)
            return 9;

        return 0;
    }

    // Fonction qui va recalculer le poids d'un chemin donné
    public static int recalculePoids(int[][] matriceAdjacente, Stack<Integer> chemin) {
        int poids = 0;

        if(chemin != null) {
            for (int i = 1; i < chemin.size(); i++)
                poids = poids + ((matriceAdjacente[chemin.get(i - 1)][chemin.get(i)] + getEtatContamination(chemin.get(i))) / 2);
        }

        return poids;
    }

    // Fonction qui vérifie si le noeud node peut être ajouté au chemin
    public static boolean cheminEstCompatible(Stack<Integer> chemin, int node, int[][] matriceAdjacente,
                                              IntegerResult poidsCheminSortie,
                                              Stack<Integer>[] tabListesChemins) {
        int dernierNodePile = -1, avantDernierNodePile = -1;
        Stack<Integer> cheminTmp = new Stack<>();

        if((chemin == null) && (node == 0)) {
            poidsCheminSortie.setResult(0);
            return true;
        }
        else if((chemin != null)) {
            if (chemin.size() > 0)
                dernierNodePile = chemin.get(chemin.size() - 1);

            if (chemin.size() > 1)
                avantDernierNodePile = chemin.get(chemin.size() - 2);

            if ((chemin.size() == 0) && (node == 0)) {
                poidsCheminSortie.setResult(0);
                return true;
            }
            else if (chemin.indexOf(node) == -1) {
                for(int i = 0 ; i < chemin.size() ; i++) {
                    cheminTmp.push(chemin.get(i));
                }

                if (estVoisin(node, dernierNodePile, matriceAdjacente)) {
                    cheminTmp.push(node);

                    if(listeCheminsContienChemin(tabListesChemins, cheminTmp)) {
                        System.out.println("Chemin " + chemin.toString() + " incompatible avec " + node);
                        return false;
                    }

                    poidsCheminSortie.setResult(recalculePoids(matriceAdjacente, cheminTmp));

                    return true;
                }
                else if (estVoisin(node, avantDernierNodePile, matriceAdjacente)) {
                    cheminTmp.pop();
                    cheminTmp.push(node);

                    if(listeCheminsContienChemin(tabListesChemins, cheminTmp)) {
                        System.out.println("Chemin " + chemin.toString() + " incompatible avec " + node);
                        return false;
                    }

                    poidsCheminSortie.setResult(recalculePoids(matriceAdjacente, cheminTmp));
                    //System.out.println("Le chemin " + cheminTmp.toString() + " n'existe pas.");
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean existePileRemplie(Stack<Integer>[] tabListesChemins) {
        for (int i = 0 ; i < indexMaxTabListeChemins ; i++) {
            if(tabListesChemins[i].size() == matriceAdjacente.length)
                return true;
        }

        return false;
    }

    public static void exploreNode(int currentNode, int[][] matriceAdjacente, HashMap<Integer, Integer> nodesToExploreValeur,
                                   HashMap<Integer, Integer> nodesToExplorePoids,
                                   Stack<Integer>[] tabListesChemins, Stack<Integer> exploredNodes,
                                   int[] listesPoids) {
        int dernierNodePile, avantDernierNodePile, poids = 0;

        nodesToExploreValeur.remove(currentNode);
        nodesToExplorePoids.remove(currentNode);

        // On va chercher les sommets connectés à currentNode et on vérifie qu'ils ne sont pas présents
        // dans les noeuds explorés
        for(int i = 0 ; i < matriceAdjacente.length ; i++) {
            int nextNode = i;
            poids = matriceAdjacente[currentNode][nextNode];

            if((poids >= 0) && (exploredNodes.indexOf(nextNode) == -1) &&
                    ((nodesToExplorePoids.get(nextNode) == null) || (poids < nodesToExplorePoids.get(nextNode)))) {
                nodesToExploreValeur.put(nextNode, nextNode);

                System.out.println("nodesToExplorePoids => Ajout de " + nextNode + ", poids = " + poids);
                nodesToExplorePoids.put(nextNode, poids);
            }
        }

        System.out.println("nodesToExplorePoids : ");
        System.out.println(nodesToExplorePoids.toString());

        // Si le currentNode est égal à 0, on va créer une nouvelle pile
        if(currentNode == 0) {
            tabListesChemins[indexMaxTabListeChemins] = new Stack<>();
            tabListesChemins[indexMaxTabListeChemins].add(currentNode);
            listesPoids[indexMaxTabListeChemins] = 0;
        }
        else {
            int i = 0;

            if (tabListesChemins[numPileAModifier].indexOf(currentNode) == -1) {
                dernierNodePile = -1;
                avantDernierNodePile = -1;

                if(tabListesChemins[numPileAModifier].size() > 0)
                    dernierNodePile = tabListesChemins[numPileAModifier].get(tabListesChemins[numPileAModifier].size() - 1);

                if(tabListesChemins[numPileAModifier].size() > 1)
                    avantDernierNodePile = tabListesChemins[numPileAModifier].get(tabListesChemins[numPileAModifier].size() - 2);

                // Si le currentNode est le voisin direct du dernier élément de la pile, alors on ajoute le currentNode
                // au sommet de la pile
                if((tabListesChemins[numPileAModifier].size() > 0) && (estVoisin(currentNode, dernierNodePile, matriceAdjacente))) {
                    tabListesChemins[numPileAModifier].add(currentNode);
                    listesPoids[numPileAModifier] = listesPoids[numPileAModifier] + poids;
                }
                // Si le currentNode est le voisin direct de l'avant-dernier élément de la pile, alors on duplique
                // la pile et on remplace le dernier élément de la pile par le currentNode
                else if((tabListesChemins[numPileAModifier].size() > 1) && (estVoisin(currentNode, avantDernierNodePile, matriceAdjacente))) {
                    indexMaxTabListeChemins++;
                    tabListesChemins[indexMaxTabListeChemins] = new Stack<>();

                    for(int j = 0 ; j < tabListesChemins[numPileAModifier].size() - 1 ; j++) {
                        tabListesChemins[indexMaxTabListeChemins].add(tabListesChemins[numPileAModifier].get(j));
                    }
                    tabListesChemins[indexMaxTabListeChemins].add(currentNode);

                    listesPoids[indexMaxTabListeChemins] =
                            listesPoids[i] - getPoids(avantDernierNodePile, dernierNodePile, matriceAdjacente);
                }

            } // Si le nextNode n'est pas dans la pile
        }

        visuPiles(tabListesChemins);
    }

    public static void main(String args[]) {
        HashMap<Integer, Integer> nodesToExploreValeur = new HashMap<>();
        HashMap<Integer, Integer> nodesToExplorePoids = new HashMap<>();
        Stack<Integer>[] tabListesChemins = new Stack[300];
        int[] listesPoids = new int[300];
        Stack<Integer> exploredNodes = new Stack<>();
        boolean pileRemplie = false;
        int poidsNoeudCourant = -1, nbIterations = 0;

        nodesToExploreValeur.put(0, 0);
        nodesToExplorePoids.put(0, 0);

        // Tant qu'aucune pile n'est remplie
        while(!pileRemplie) {
            nbIterations++;

            // On recherche le noeud qui a le poids le moins élevé
            currentNode = getNoeudCoutMoinsEleve(nodesToExplorePoids, tabListesChemins, matriceAdjacente);

            poidsNoeudCourant = nodesToExplorePoids.get(currentNode);

            System.out.println("Noeud courant = " + currentNode + ", Poids du noeud courant : " + poidsNoeudCourant);

            System.out.println("Poids du noeud courant = " + poidsNoeudCourant);

            exploreNode(currentNode, matriceAdjacente, nodesToExploreValeur, nodesToExplorePoids, tabListesChemins,
                    exploredNodes, listesPoids);

            pileRemplie = existePileRemplie(tabListesChemins);
        }

        System.out.println("Le nombre d'itérations pour trouver la solution à l'algorithme de Dijkstra est de " + nbIterations);
    }
}
