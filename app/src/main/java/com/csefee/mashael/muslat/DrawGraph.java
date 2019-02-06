package com.csefee.mashael.muslat;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import database.PublicCairoDatabaseHelper;
import database.TrainDatabaseHelper;

public class DrawGraph {
    boolean x = false;
    private int[] startpoint, endpoint;
    private int type, s, d;
    private Context context;
    private List<Integer> final_path;

    public DrawGraph(Context context, int s, int d, int type) {
        this.context = context;
        this.s = s;
        this.d = d;
        this.type = type;
    }

    public void drawGraph() {
        //type =0 "train"
        //type = 1 "cairo bus"
        switch (type) {
            case 0:

                SQLiteOpenHelper trainDatabaseHelper = new TrainDatabaseHelper(context);
                SQLiteDatabase db1 = trainDatabaseHelper.getReadableDatabase();
                Cursor cursor1 = db1.rawQuery("SELECT * FROM trainline", null);
                startpoint = new int[cursor1.getCount()];
                endpoint = new int[cursor1.getCount()];

                int i = 0;
                if (cursor1.moveToFirst()) {

                    while (!cursor1.isAfterLast()) {

                        startpoint[i] = cursor1.getInt(2);
                        endpoint[i] = cursor1.getInt(3);
                        cursor1.moveToNext();
                        i++;
                    }
                }

                cursor1.close();

                Graph g = new Graph(1000);
                for (i = 0; i < startpoint.length; i++) {
                    g.addEdge(startpoint[i], endpoint[i]);

                }
                Log.w("graph", "Following are all different paths from " + s + " to " + d);
                g.printAllPaths(s, d);
                break;
            case 1:
                SQLiteOpenHelper publicCairoDatabaseHelper = new PublicCairoDatabaseHelper(context);
                db1 = publicCairoDatabaseHelper.getReadableDatabase();
                GraphEdited ge = new GraphEdited(1000);
                cursor1 = db1.rawQuery("SELECT id FROM line", null);
                cursor1.moveToFirst();
                while (!cursor1.isAfterLast()) {
                    Cursor c1 = db1.rawQuery("Select area_id from ordering where lineid=? ", new String[]{String.valueOf(cursor1.getInt(0))});
                    startpoint = new int[cursor1.getCount() + cursor1.getCount()];

                    i = 0;
                    c1.moveToFirst();
                    while (!c1.isAfterLast()) {
                        startpoint[i] = c1.getInt(0);
                        c1.moveToNext();
                        if (c1.isAfterLast())
                            break;
                        startpoint[i + 1] = c1.getInt(0);
                        ge.addEdge(startpoint[i], startpoint[i + 1]);

                        i++;

                    }
                    cursor1.moveToNext();
                }


                /* startpoint = new int[cursor1.getCount()];
                endpoint = new int[cursor1.getCount()];

                i = 0;
                if (cursor1.moveToFirst()) {

                    while (!cursor1.isAfterLast()) {

                        startpoint[i] = cursor1.getInt(2);
                        endpoint[i] = cursor1.getInt(3);
                        cursor1.moveToNext();
                        i++;
                    }
                }

                cursor1.close();
*//*
                for(i=0;i<startpoint.length;i++)
                {
                    g.addEdge(startpoint[i],endpoint[i]);

                }*/
                //Log.w("graph","Following are all different paths from /cairo/ "+s+" to "+d);
                ge.printAllPaths(s, d);
                break;
        }


    }

    public ArrayList<Integer> getFinal_path() {
        return (ArrayList<Integer>) final_path;
    }

    public class Graph {

        private int v;

        // adjacency list
        private ArrayList<Integer>[] adjList;

        //Constructor
        public Graph(int vertices) {

            //initialise vertex count
            this.v = vertices;

            // initialise adjacency list
            initAdjList();
        }

        // utility method to initialise
        // adjacency list
        @SuppressWarnings("unchecked")
        private void initAdjList() {
            adjList = new ArrayList[v];

            for (int i = 0; i < v; i++) {
                adjList[i] = new ArrayList<>();
            }
        }

        public void addEdge(int u, int v) {
            // Add v to u's list.
            adjList[u].add(v);
        }

        // Prints all paths from
        // 's' to 'd'
        public void printAllPaths(int s, int d) {
            boolean[] isVisited = new boolean[v];
            ArrayList<Integer> pathList = new ArrayList<>();

            //add source to path[]
            pathList.add(s);

            //Call recursive utility
            printAllPathsUtil(s, d, isVisited, pathList);
        }

        // A recursive function to print
        // all paths from 'u' to 'd'.
        // isVisited[] keeps track of
        // vertices in current path.
        // localPathList<> stores actual
        // vertices in the current path
        private void printAllPathsUtil(Integer u, Integer d,
                                       boolean[] isVisited,
                                       List<Integer> localPathList) {

            // Mark the current node
            isVisited[u] = true;

            if (u.equals(d)) {
                Log.w("SS", String.valueOf(localPathList) + String.valueOf(localPathList.size()));

                final_path = new ArrayList<>(localPathList.size());
                final_path.addAll(localPathList);
            }

            // Recur for all the vertices
            // adjacent to current vertex
            for (Integer i : adjList[u]) {
                if (!isVisited[i]) {
                    // store current node
                    // in path[]
                    localPathList.add(i);
                    printAllPathsUtil(i, d, isVisited, localPathList);

                    // remove current node
                    // in path[]
                    localPathList.remove(i);
                }
            }

            // Mark the current node
            isVisited[u] = false;

        }
    }

    public class GraphEdited {

        private int v;

        // adjacency list
        private ArrayList<Integer>[] adjList;

        //Constructor
        public GraphEdited(int vertices) {

            //initialise vertex count
            this.v = vertices;

            // initialise adjacency list
            initAdjList();
        }

        // utility method to initialise
        // adjacency list
        @SuppressWarnings("unchecked")
        private void initAdjList() {
            adjList = new ArrayList[v];

            for (int i = 0; i < v; i++) {
                adjList[i] = new ArrayList<>();
            }
        }

        public void addEdge(int u, int v) {
            // Add v to u's list.
            adjList[u].add(v);
        }

        // Prints all paths from
        // 's' to 'd'
        public void printAllPaths(int s, int d) {
            boolean[] isVisited = new boolean[v];
            ArrayList<Integer> pathList = new ArrayList<>();

            //add source to path[]
            pathList.add(s);

            //Call recursive utility
            printAllPathsUtil(s, d, isVisited, pathList);
        }

        // A recursive function to print
        // all paths from 'u' to 'd'.
        // isVisited[] keeps track of
        // vertices in current path.
        // localPathList<> stores actual
        // vertices in the current path
        private void printAllPathsUtil(Integer u, Integer d,
                                       boolean[] isVisited,
                                       List<Integer> localPathList) {

            // Mark the current node
            isVisited[u] = true;

            if (u.equals(d)) {
                Log.w("SS", String.valueOf(localPathList) + String.valueOf(localPathList.size()));

                if (!localPathList.isEmpty())
                    x = true;
                final_path = new ArrayList<>(localPathList.size());
                final_path.addAll(localPathList);
            }

            // Recur for all the vertices
            // adjacent to current vertex
            for (Integer i : adjList[u]) {
                if (!isVisited[i]) {
                    // store current node
                    // in path[]
                    localPathList.add(i);
                    printAllPathsUtil(i, d, isVisited, localPathList);

                    // remove current node
                    // in path[]
                    localPathList.remove(i);
                    if (x)
                        break;
                }
            }

            // Mark the current node
            isVisited[u] = false;

        }
    }
}
