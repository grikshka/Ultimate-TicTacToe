/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaapplication16;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author nedas
 */
public class BotExampleMerge {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
    }

    public class Tree {

        Node root;

        public Tree() {
            root = new Node();
        }

        public Tree(Node root) {
            this.root = root;
        }

        public Node getRoot() {
            return root;
        }

        public void setRoot(Node root) {
            this.root = root;
        }

        public void addChild(Node parent, Node child) {
            parent.getChildArray().add(child);
        }

    }

    public class State {

        private Board board;
        private int playerNo;
        private int visitCount;
        private double winScore;

        public State() {
            board = new Board();
        }

        public State(State state) {
            this.board = new Board(state.getBoard());
            this.playerNo = state.getPlayerNo();
            this.visitCount = state.getVisitCount();
            this.winScore = state.getWinScore();
        }

        public State(Board board) {
            this.board = new Board(board);
        }

        Board getBoard() {
            return board;
        }

        void setBoard(Board board) {
            this.board = board;
        }

        int getPlayerNo() {
            return playerNo;
        }

        void setPlayerNo(int playerNo) {
            this.playerNo = playerNo;
        }

        int getOpponent() {
            return 3 - playerNo;
        }

        public int getVisitCount() {
            return visitCount;
        }

        public void setVisitCount(int visitCount) {
            this.visitCount = visitCount;
        }

        double getWinScore() {
            return winScore;
        }

        void setWinScore(double winScore) {
            this.winScore = winScore;
        }

        public List<State> getAllPossibleStates() {
            List<State> possibleStates = new ArrayList<>();
            List<javaapplication16.Position> availablePositions = this.board.getEmptyPositions();
            availablePositions.forEach(p -> {
                State newState = new State(this.board);
                newState.setPlayerNo(3 - this.playerNo);
                newState.getBoard().performMove(newState.getPlayerNo(), p);
                possibleStates.add(newState);
            });
            return possibleStates;
        }

        void incrementVisit() {
            this.visitCount++;
        }

        void addScore(double score) {
            if (this.winScore != Integer.MIN_VALUE) {
                this.winScore += score;
            }
        }

        void randomPlay() {
            List<javaapplication16.Position> availablePositions = this.board.getEmptyPositions();
            int totalPossibilities = availablePositions.size();
            int selectRandom = (int) (Math.random() * totalPossibilities);
            this.board.performMove(this.playerNo, availablePositions.get(selectRandom));
        }

        void togglePlayer() {
            this.playerNo = 3 - this.playerNo;
        }
    }



        public  double uctValue(int totalVisit, double nodeWinScore, int nodeVisit) {
            if (nodeVisit == 0) {
                return Integer.MAX_VALUE;
            }
            return (nodeWinScore / (double) nodeVisit) + 1.41 * Math.sqrt(Math.log(totalVisit) / (double) nodeVisit);
        }

        public Node  findBestNodeWithUCT(Node node) {
            int parentVisit = node.getState().getVisitCount();
            return Collections.max(
                    node.getChildArray(),
                    Comparator.comparing(c -> uctValue(parentVisit, c.getState().getWinScore(), c.getState().getVisitCount())));
        }
    

    public class Position {

        int x;
        int y;

        public Position() {
        }

        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

    }

    public class MonteCarloTreeSearch {

        private static final int WIN_SCORE = 10;
        private int level;
        private int opponent;

        public MonteCarloTreeSearch() {
            this.level = 3;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        private int getMillisForCurrentLevel() {
            return 2 * (this.level - 1) + 1;
        }

        public Board findNextMove(Board board, int playerNo) {
            long start = System.currentTimeMillis();
            long end = start + 60 * getMillisForCurrentLevel();

            opponent = 3 - playerNo;
            Tree tree = new Tree();
            Node rootNode = tree.getRoot();
            rootNode.getState().setBoard(board);
            rootNode.getState().setPlayerNo(opponent);

            while (System.currentTimeMillis() < end) {
                // Phase 1 - Selection
                Node promisingNode = selectPromisingNode(rootNode);
                // Phase 2 - Expansion
                if (promisingNode.getState().getBoard().checkStatus() == Board.IN_PROGRESS) {
                    expandNode(promisingNode);
                }

                // Phase 3 - Simulation
                Node nodeToExplore = promisingNode;
                if (promisingNode.getChildArray().size() > 0) {
                    nodeToExplore = promisingNode.getRandomChildNode();
                }
                int playoutResult = simulateRandomPlayout(nodeToExplore);
                // Phase 4 - Update
                backPropogation(nodeToExplore, playoutResult);
            }

            Node winnerNode = rootNode.getChildWithMaxScore();
            tree.setRoot(winnerNode);
            return winnerNode.getState().getBoard();
        }

        private Node selectPromisingNode(Node rootNode) {
            Node node = rootNode;
            while (node.getChildArray().size() != 0) {
                node = findBestNodeWithUCT(node);
            }
            return node;
        }

        private void expandNode(Node node) {
            List<State> possibleStates = node.getState().getAllPossibleStates();
            possibleStates.forEach(state -> {
                Node newNode = new Node(state);
                newNode.setParent(node);
                newNode.getState().setPlayerNo(node.getState().getOpponent());
                node.getChildArray().add(newNode);
            });
        }

        private void backPropogation(Node nodeToExplore, int playerNo) {
            Node tempNode = nodeToExplore;
            while (tempNode != null) {
                tempNode.getState().incrementVisit();
                if (tempNode.getState().getPlayerNo() == playerNo) {
                    tempNode.getState().addScore(WIN_SCORE);
                }
                tempNode = tempNode.getParent();
            }
        }

        private int simulateRandomPlayout(Node node) {
            Node tempNode = new Node(node);
            State tempState = tempNode.getState();
            int boardStatus = tempState.getBoard().checkStatus();

            if (boardStatus == opponent) {
                tempNode.getParent().getState().setWinScore(Integer.MIN_VALUE);
                return boardStatus;
            }
            while (boardStatus == Board.IN_PROGRESS) {
                tempState.togglePlayer();
                tempState.randomPlay();
                boardStatus = tempState.getBoard().checkStatus();
            }

            return boardStatus;
        }

    }

    public class Node {

        State state;
        Node parent;
        List<Node> childArray;

        public Node() {
            this.state = new State();
            childArray = new ArrayList<>();
        }

        public Node(State state) {
            this.state = state;
            childArray = new ArrayList<>();
        }

        public Node(State state, Node parent, List<Node> childArray) {
            this.state = state;
            this.parent = parent;
            this.childArray = childArray;
        }

        public Node(Node node) {
            this.childArray = new ArrayList<>();
            this.state = new State(node.getState());
            if (node.getParent() != null) {
                this.parent = node.getParent();
            }
            List<Node> childArray = node.getChildArray();
            for (Node child : childArray) {
                this.childArray.add(new Node(child));
            }
        }

        public State getState() {
            return state;
        }

        public void setState(State state) {
            this.state = state;
        }

        public Node getParent() {
            return parent;
        }

        public void setParent(Node parent) {
            this.parent = parent;
        }

        public List<Node> getChildArray() {
            return childArray;
        }

        public void setChildArray(List<Node> childArray) {
            this.childArray = childArray;
        }

        public Node getRandomChildNode() {
            int noOfPossibleMoves = this.childArray.size();
            int selectRandom = (int) (Math.random() * noOfPossibleMoves);
            return this.childArray.get(selectRandom);
        }

        public Node getChildWithMaxScore() {
            return Collections.max(this.childArray, Comparator.comparing(c -> {
                return c.getState().getVisitCount();
            }));
        }

    }
}
