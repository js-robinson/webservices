/**
 *  This class consists of the SQL queries for creating the tables which enable the support of Version Control within the application.
 *  Note that these are only put here for reference! A script should be written to create ALL tables.
 *  It also includes queries which will be executed on the back-end to accommodate front-end interaction with the program.
 *  The database used is Derby, so all queries are written to support the Derby SQL dialect.
 *
 *  @author Yordanka Ivanova
 *  @since July 2017
 */

package database;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import utils.TimeHelper;

import java.sql.Timestamp;
import java.util.*;



public class DBQuery {

    /*This object manages the connection to the database and the execution of queries
    * @see DBConnect in the database package
    */
    protected DBConnect dbcn;

    public DBQuery() {
        dbcn = new DBConnect();
    }

    /*
     These methods show the statements used to create the database tables. They are for reference only! A script should be written to create the database tables.
     */
    public void createTableGraph(){
        String query = "CREATE TABLE CISPACES_GRAPH ( graphid varchar (255), userid varchar(255), timest timestamp, isshared boolean, parentgraphid varchar(255), ";
        query+="CONSTRAINT CISPACES_GRAPH_pk PRIMARY KEY (graphid))";
        dbcn.updateSQL(query);
    }

    public void createTableGraphHistory(){
        String query = "CREATE TABLE CISPACES_GRAPH_HISTORY (revisionid varchar(255), graphid varchar(255), userid varchar(255), " +
                "timest timestamp, isshared boolean, parentgraphid varchar(255), ";
        query+="CONSTRAINT GRAPH_SESSION_HISTORY_pk PRIMARY KEY (revisionid))";
        dbcn.updateSQL(query);
    }

    public void createTableGraphHistoryNew(){
        String query = "CREATE TABLE CISPACES_GRAPH_HISTORY (snapid varchar(255), graphid varchar(255), userid varchar(255), " +
                "timest timestamp, analysis clob(2M), title varchar(255), ";
        query+="CONSTRAINT CISPACES_GRAPH_HISTORY_pk PRIMARY KEY (snapid))";
        dbcn.updateSQL(query);
    }

    public void createTableNode(){
        String query="CREATE TABLE CISPACES_NODE ( nodeid varchar(255), source varchar (255), uncert varchar(20), eval varchar(5000), "
                + "txt varchar (5000), inp varchar (20), dtg timestamp, cmt varchar(50), type varchar(10), annot varchar (5000), graphid varchar(255), "
                + "islocked boolean, ";
        query+="CONSTRAINT CISPACES_NODE_pk PRIMARY KEY (nodeid))";
        dbcn.updateSQL(query);
    }

    public void createTableNodeHistory(){
        String query="CREATE TABLE CISPACES_NODE_HISTORY ( nodeid varchar(255), source varchar (255), uncert varchar(20), eval varchar(5000), "
                + "txt varchar (5000), inp varchar (20), dtg timestamp, cmt varchar(50), type varchar(10), annot varchar (5000), graphid varchar(255), "
                + "islocked boolean, revisionid varchar(255), ";
        query+="CONSTRAINT CISPACES_NODE_HISTORY_pk PRIMARY KEY (revisionid))";
        dbcn.updateSQL(query);
    }

    public void createTableEdge(){
        String query="create table cispaces_edge ( edgeID varchar(255), target varchar(255), source varchar(255), formEdgeID varchar(255), graphID varchar(255), islocked boolean, CONSTRAINT CISPACES_EDGE_pk PRIMARY KEY (edgeID))";
        dbcn.updateSQL(query);
    }

    public void createTableEdgeHistory(){
        String query="CREATE TABLE CISPACES_EDGE_HISTORY ( edgeid varchar(255), tonodeid varchar(255), fromnodeid varchar(255), formedgeid varchar(255)," +
                "graphid varchar(255), islocked boolean, revisionid varchar(255), ";
        query+="CONSTRAINT CISPACES_EDGE_pk PRIMARY KEY (revisionid))";
        dbcn.updateSQL(query);
    }

    //shut down the communication between the web service and the database
    public void closeDatabase() {
        dbcn.forceClose();
    }

    //the query to insert a new graph into the database
    public void insertGraph(String graphid, String userid, Timestamp timest, boolean isshared, String parentgraphid){
        String sql;
        sql = "INSERT INTO CISPACES_GRAPH(graphid, userid, timest, isshared, parentgraphid) VALUES "
                + "( '" + graphid + "' ,"
                + " '" + userid + "' ,"
                + " '" + timest + "' ,"
                + " '" + isshared + "' ,"
                + " '" + parentgraphid + "'"
                +" )";

        System.out.println(sql);
        dbcn.updateSQL(sql);
    }

    //queries to insert a node or update an existing node information in the database
    public void insertNode(String nodeID, String source, String uncert, String eval, String txt, String input,
                           Timestamp timestamp, String commit, String type, String annot, String graphID, boolean isLocked)
    {
        String sql;
        sql = "Select * from CISPACES_NODE WHERE nodeid = " + "'" + nodeID + "'";
        ArrayList<HashMap<String,Object>> rs = dbcn.execSQL(sql);
        if(rs.isEmpty()){
            System.out.println("NODE DOESNT EXIST");
            sql = "INSERT INTO CISPACES_NODE (nodeid, source, uncert, eval, txt, inp, dtg, cmt, type, annot, graphid, islocked) VALUES "
                    + "( '"+nodeID+"' ,"
                    + " '"+source+"' ,"
                    + " '"+uncert+"' ,"
                    + " '"+eval+"' ,"
                    + " '"+txt+"' ,"
                    + " '"+input+"' ,"
                    + " '"+timestamp+"' ,"
                    + " '"+commit+"' ,"
                    + " '"+type+"' ,"
                    + " '"+annot+"' ,"
                    + " '"+graphID+"' ,"
                    + " '"+isLocked+"'"
                    + " )";
            System.out.println(sql);
        }else{
            System.out.println("NODE EXISTS");
            sql = "UPDATE CISPACES_NODE SET uncert = " + "'" + uncert + "'" + " ,"
                                        + " eval = " + "'" + eval + "'" + " ,"
                                        + " txt = "  + "'" + txt + "'" + " ,"
                                        + " inp = "  + "'" + input + "'" + " ,"
                                        + " cmt = "  + "'" + commit + "'" + " ,"
                                        + " annot = " + "'" + annot + "'"
                    + " WHERE nodeid = " + "'" + nodeID + "'";
            System.out.println(sql);
        }

        dbcn.updateSQL(sql);
    }

    //insert a node in the history table or update the information if it exists
    public void insertNodeHistory(String nodeID, String source, String uncert, String eval, String txt, String input,
                           Timestamp timestamp, String commit, String type, String annot, String graphID, boolean isLocked, String revisionID)
    {
        String sql;
        sql = "Select * from CISPACES_NODE_HISTORY WHERE nodeid = " + "'" + nodeID + "'";
        ArrayList<HashMap<String,Object>> rs = dbcn.execSQL(sql);
        if(rs.isEmpty()){
            System.out.println("NODE DOESNT EXIST IN HISTORY TABLE");
            sql = "INSERT INTO CISPACES_NODE_HISTORY (nodeid, source, uncert, eval, txt, inp, dtg, cmt, type, annot, graphid, islocked, revisionid) VALUES "
                    + "( '"+nodeID+"' ,"
                    + " '"+source+"' ,"
                    + " '"+uncert+"' ,"
                    + " '"+eval+"' ,"
                    + " '"+txt+"' ,"
                    + " '"+input+"' ,"
                    + " '"+timestamp+"' ,"
                    + " '"+commit+"' ,"
                    + " '"+type+"' ,"
                    + " '"+annot+"' ,"
                    + " '"+graphID+"'"
                    + " '"+isLocked+"'"
                    + " '"+revisionID+"'"
                    + " )";
            System.out.println(sql);
        }else{
            System.out.println("NODE EXISTS IN HISTORY TABLE");
            sql = "UPDATE CISPACES_NODE SET uncert = " + "'" + uncert + "'" + " ,"
                    + " eval = " + "'" + eval + "'" + " ,"
                    + " txt = "  + "'" + txt + "'" + " ,"
                    + " inp = "  + "'" + input + "'" + " ,"
                    + " cmt = "  + "'" + commit + "'" + " ,"
                    + " annot = " + "'" + annot + "'" + " ,"
                    + " islocked = " + "'" + isLocked + "'"
                    + " WHERE nodeid = " + "'" + nodeID + "'";
            System.out.println(sql);
        }

        dbcn.updateSQL(sql);
    }

    //insert an edge into the database - edges are never updated!
    public void insertEdge(String toID, String fromID, String formEdgeID, String edgeID, String graphID, boolean isLocked) {
        String sql;
        sql = "Select * from CISPACES_EDGE WHERE edgeid = " + "'" + edgeID + "'";
        ArrayList<HashMap<String,Object>> rs = dbcn.execSQL(sql);
        if(rs.isEmpty()) {
            System.out.println("EDGE DOESNT EXIST");
            sql = "INSERT INTO CISPACES_EDGE (edgeID, target, source, formEdgeID, graphID, islocked) VALUES "
                    + "( '" + edgeID + "' ,"
                    + " '" + toID + "' ,"
                    + " '" + fromID + "' ,"
                    + " '" + formEdgeID + "' ,"
                    + " '" + graphID + "' ,"
                    + " '" + isLocked + "'"
                    + " )";
            dbcn.updateSQL(sql);
        }

        System.out.println(sql);

    }

    //populate the edge history table
    public void insertEdgeHistory(String toID, String fromID, String formEdgeID, String edgeID, String graphID, boolean isLocked, String revisionID) {
        String sql;
        sql = "Select * from CISPACES_EDGE_HISTORY WHERE edgeid = " + "'" + edgeID + "'";
        ArrayList<HashMap<String,Object>> rs = dbcn.execSQL(sql);
        if(rs.isEmpty()) {
            System.out.println("EDGE DOESNT EXIST IN HISTORY TABLE");
            sql = "INSERT INTO CISPACES_EDGE_HISTORY (edgeid, tonodeid, fromnodeid, formedgeid, graphid, islocked, revisionid) VALUES "
                    + "( '" + edgeID + "' ,"
                    + " '" + toID + "' ,"
                    + " '" + fromID + "' ,"
                    + " '" + formEdgeID + "' ,"
                    + " '" + graphID + "' ,"
                    + " '" + isLocked + "' ,"
                    +  " '" + revisionID + "'"
                    + " )";
            dbcn.updateSQL(sql);
        }

        System.out.println(sql);

    }

    //delete an edge from the database
    public boolean deleteEdge(String edgeid) {
        String sql = "DELETE FROM CISPACES_EDGE WHERE edgeid = " + "'" + edgeid + "'";
        System.out.println(sql);
        boolean isStatementExecuted = dbcn.updateSQL(sql);

        return isStatementExecuted;
    }

    //delete a node from the database
    public boolean deleteNode(String nodeid) {
        String sql = "DELETE FROM CISPACES_NODE WHERE nodeid = " + "'" + nodeid + "'";
        System.out.println(sql);
        boolean isStatementExecuted = dbcn.updateSQL(sql);

        return isStatementExecuted;
    }

    /* check if a user exists in the database
     * @return either 'fail' or the user id if the validation is successful
     */
    public String checkUserExists(String username, String password, String aff) {
        String sql = "SELECT USER_ID from CISPACES_USERS WHERE USERNAME = " + "'" + username + "'" + " AND PASSWORD = " +  "'" + password + "'";
        System.out.println(sql);
        String response;
        ArrayList<HashMap<String,Object>> rs = dbcn.execSQL(sql);
        if(rs.isEmpty()){
            response = "fail";
        }else{
            response = rs.get(0).toString();
        }

        return response;
    }

    /* Retrieves the latest analysis associated with a user upon loading the index page.
     * Checks if a user has worked on an analysis before.
     * If the user is new, then a new graph (analysis) is created and linked to them.
     * If the user has worked on an analysis before, a JSON object is constructed with
     * the nodes and edges linked to that analysis. The JSON object is turned into a JSON string
     * and passed to the client to visualize it in the work box.
     */
    public String getLatestAnalysis(String userID) {
        System.out.println("THE USER ID IS " + userID);
        String sql = "SELECT GRAPHID FROM CISPACES_GRAPH WHERE USERID = " +  "'"  + userID +  "'";
        System.out.println(sql);
        JSONObject jsonGraph = new JSONObject();
        ArrayList<HashMap<String,Object>> rs = dbcn.execSQL(sql);
        if(rs.isEmpty()){
            TimeHelper timeHelper = new TimeHelper();
            boolean isShared = false;
            String parentID = null;
            Date now = new Date();
            Timestamp timestamp = timeHelper.formatDateObjectCIS(now);
            String graphID = UUID.randomUUID().toString();

            //create new analysis and pass graphID to the client
            String newGraphQuery = "INSERT INTO CISPACES_GRAPH(graphid, userid, timest, isshared, parentgraphid) VALUES "
                    + "( '" + graphID + "' ,"
                    + " '" + userID + "' ,"
                    + " '" + timestamp + "' ,"
                    + " '" + isShared + "' ,"
                    + " '" + parentID + "'"
                    + " )";
            dbcn.updateSQL(newGraphQuery);
            jsonGraph.put("graphID",graphID);
            jsonGraph.put("nodes", new JSONArray());
            jsonGraph.put("edges", new JSONArray());
            return jsonGraph.toString();
        }else {
            String idQuery = "SELECT GRAPHID FROM CISPACES_GRAPH WHERE TIMEST = (SELECT MAX(TIMEST) FROM CISPACES_GRAPH WHERE USERID = " + "'" + userID + "'" + ")";
            String resultIDJSON = dbcn.execSQL(idQuery).get(0).toString();
            String resultID = resultIDJSON.substring(9, resultIDJSON.length() - 1);
            String getNodesSql = "SELECT * FROM CISPACES_NODE WHERE GRAPHID = " + "'" + resultID + "'";
            String getEdgesSql = "SELECT * FROM CISPACES_EDGE WHERE GRAPHID = " + "'" + resultID + "'";
            System.out.println("THE GRAPH ID FROM THE QUERY IS " + resultID);
            ArrayList<HashMap<String, Object>> resultNodes = dbcn.execSQL(getNodesSql);
            ArrayList<HashMap<String, Object>> resultEdges = dbcn.execSQL(getEdgesSql);

            JSONArray jsonNodesArray = getResultListJSON(resultNodes);
            jsonGraph.put("nodes", jsonNodesArray);

            JSONArray jsonEdgesArray = getResultListJSON(resultEdges);
            jsonGraph.put("edges", jsonEdgesArray);

            return jsonGraph.toString();
        }

    }
        /*A number of queries used to store an analysis in the database.
        * Retrieves all nodes and edges connected to a graph by its graph id.
        * Constructs a json object and populates it with the JSON arrays of the nodes and edges.
        * Timestamps the event and inserts the data into the database.
        */
         public String saveLatestAnalysis(String graphID, String userID, String title) {
            String getNodesSql = "SELECT * FROM CISPACES_NODE WHERE GRAPHID = " +  "'" + graphID + "'";
            String getEdgesSql = "SELECT * FROM CISPACES_EDGE WHERE GRAPHID = " +  "'" + graphID + "'";

            JSONObject jsonGraph = new JSONObject();
            jsonGraph.put("graphID",graphID);
            ArrayList<HashMap<String,Object>> resultNodes = dbcn.execSQL(getNodesSql);
            ArrayList<HashMap<String,Object>> resultEdges = dbcn.execSQL(getEdgesSql);

            JSONArray jsonNodesArray =  getResultListJSON(resultNodes);
            jsonGraph.put("nodes",jsonNodesArray);


            JSONArray jsonEdgesArray = getResultListJSON(resultEdges);
            jsonGraph.put("edges",jsonEdgesArray);

            Date now = new Date();
            TimeHelper timeHelper = new TimeHelper();
            String json =  jsonGraph.toString().replace("nodeid", "nodeID");
            json = json.replace("N\\/A", "N/A");
            String snapID = UUID.randomUUID().toString();
            Timestamp timestamp = timeHelper.formatDateObjectCIS(now);

            String saveGraphQuery = "INSERT INTO CISPACES_GRAPH_HISTORY(snapid, graphid, userid, timest, analysis, title) VALUES "
                    + "( '" + snapID + "' ,"
                    + " '" + graphID + "' ,"
                    + " '" + userID + "' ,"
                    + " '" + timestamp + "' ,"
                    + " '" + json + "' ,"
                    + " '" + title + "'"
                    + " )";
            boolean isExecuted = dbcn.updateSQL(saveGraphQuery);
            JSONObject result = new JSONObject();
            result.put("status",isExecuted);

            return result.toString();
        }

        /*helper method to iterate through the result set obtained after executing a query
        *the result sets are a list of hashmaps which have table column titles as keys and database entries as values
        */
        public JSONArray getResultListJSON(ArrayList<HashMap<String,Object>> resultEntities){
            JSONArray jsonArray = new JSONArray();
            for(HashMap<String,Object> edgesEntry : resultEntities){
                Iterator entryIterator = edgesEntry.entrySet().iterator();
                JSONObject obj = new JSONObject();
                while (entryIterator.hasNext()) {
                    Map.Entry pair = (Map.Entry)entryIterator.next();
                    //these are reserved words in derby!!!
                    if(pair.getKey().toString().equals("inp")){
                        obj.put("input", pair.getValue().toString());
                    }else if(pair.getKey().toString().equals("txt")) {
                        obj.put("text", pair.getValue().toString());
                    }else if(pair.getKey().toString().equals("nodeid")){
                        obj.put("nodeID", pair.getValue().toString());
                    }else if(pair.getKey().toString().equals("edgeid")){
                        obj.put("edgeID", pair.getValue().toString());
                    }else if(pair.getKey().toString().equals("graphid")){
                        obj.put("graphID", pair.getValue().toString());
                    }else {
                        obj.put(pair.getKey(), pair.getValue().toString());
                    }

                }
                jsonArray.add(obj);
            }

            return jsonArray;
        }

        //constructs a json object containing all saved variations of an analysis and returns it in a json string format
    public String getHistoryOfAnalysis(String graphID) {

            String sql = "SELECT * FROM CISPACES_GRAPH_HISTORY WHERE GRAPHID = " + "'" + graphID + "'" + " ORDER BY TIMEST";
            System.out.println(sql);
            ArrayList<HashMap<String,Object>> rs = dbcn.execSQL(sql);
            JSONObject jsonHistory = new JSONObject();
            if(!rs.isEmpty()){
                JSONArray jsonHistoryArray = getResultListJSON(rs);
                jsonHistory.put("history",jsonHistoryArray);

            }else{
                jsonHistory.put("history", new JSONArray());

            }

            return jsonHistory.toString();
    }

    public void updateAnalysis(HashMap analysis) {

       // dbcn.setAutoCommit(false);
        org.json.JSONObject obj = new org.json.JSONObject(analysis);

        String oldGraphID = obj.getString("graphID");
        System.out.println(oldGraphID + " is the graphID");
        String sql = "DELETE FROM CISPACES_NODE WHERE GRAPHID = " + "'" + oldGraphID + "'";
        String sql2 = "DELETE FROM CISPACES_EDGE WHERE GRAPHID = " + "'" + oldGraphID + "'";
        System.out.println(sql);
        dbcn.updateSQL(sql);
        dbcn.updateSQL(sql2);


        dbcn.tryConnect();
            org.json.JSONArray nodes = obj.getJSONArray("nodes");
            //iterate through nodes
            for (int i = 0; i < nodes.length(); i++) {
                String input = tryToGet(nodes.getJSONObject(i), "input");
                String eval = tryToGet(nodes.getJSONObject(i), "eval");
                String source = tryToGet(nodes.getJSONObject(i), "source");
                String uncert = tryToGet(nodes.getJSONObject(i), "uncert");
                String text = tryToGet(nodes.getJSONObject(i), "text");
                String dtg = tryToGet(nodes.getJSONObject(i), "dtg");
                String commit = tryToGet(nodes.getJSONObject(i), "commit");
                String type = tryToGet(nodes.getJSONObject(i), "type");
                String nodeID = tryToGet(nodes.getJSONObject(i), "nodeID");
                System.out.println(nodeID + " is the node id");
                String annot = tryToGet(nodes.getJSONObject(i), "annot");
                String graphID = tryToGet(nodes.getJSONObject(i), "graphID");
                String isLocked = tryToGet(nodes.getJSONObject(i), "islocked");

                String sql3 = "INSERT INTO CISPACES_NODE (nodeid, source, uncert, eval, txt, inp, dtg, cmt, type, annot, graphid, islocked) VALUES "
                        + "( '" + nodeID + "' ,"
                        + " '" + source + "' ,"
                        + " '" + uncert + "' ,"
                        + " '" + eval + "' ,"
                        + " '" + text + "' ,"
                        + " '" + input + "' ,"
                        + " '" + dtg + "' ,"
                        + " '" + commit + "' ,"
                        + " '" + type + "' ,"
                        + " '" + annot + "' ,"
                        + " '" + graphID + "' ,"
                        + " '" + isLocked + "'"
                        + " )";
                dbcn.prepareInsertStatementInTransaction(sql3);
                System.out.println(sql3);

            }

            org.json.JSONArray edges = obj.getJSONArray("edges");
            for (int i = 0; i < edges.length(); i++) {
                String edgeid = tryToGet(edges.getJSONObject(i), "edgeID");
                String islocked = tryToGet(edges.getJSONObject(i), "islocked");
                String source = tryToGet(edges.getJSONObject(i), "source");
                String formedgeid = tryToGet(edges.getJSONObject(i), "formedgeid");
                String graphID = tryToGet(edges.getJSONObject(i), "graphID");
                String target = tryToGet(edges.getJSONObject(i), "target");

                String sql4 = "INSERT INTO CISPACES_EDGE (edgeID, target, source, formEdgeID, graphID, islocked) VALUES "
                        + "( '" + edgeid + "' ,"
                        + " '" + target + "' ,"
                        + " '" + source + "' ,"
                        + " '" + formedgeid + "' ,"
                        + " '" + graphID + "' ,"
                        + " '" + islocked + "'"
                        + " )";
                dbcn.prepareInsertStatementInTransaction(sql4);
                System.out.println(sql4);
            }

            dbcn.commit();


    }


    public static String tryToGet(org.json.JSONObject jsonObj, String key) {
        if (jsonObj.has(key))
            return jsonObj.opt(key).toString();
        return null;
    }
}