import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Scanner;

public class Matrix { 
    
	public static Connection con;
	
    public static void main(String[] args) throws ClassNotFoundException,SQLException {
    
	    // Get connection string and file name
		String CONNECTION_STRING = args[0];
		String INPUT_FILE = args[1];
	
	    con = DriverManager.getConnection(CONNECTION_STRING);
	
	    if (con == null) {
	    	System.out.println("Connection failed.");
	    	return;
	    }
	    
	    Scanner in = null;
	    String line = null;
	    String[] params = null;
	    String command = null;
	    int id = 0, rowDim = 0, colDim = 0, row = 0, col = 0;
	    double val = 0;
	    int destID, srcID, srcID1, srcID2;
	    String queryString;
	    
	    try {
	    	
	    	in = new Scanner(new File(INPUT_FILE));
	    	
	    	while (in.hasNextLine()) {
	    		
	    		line = in.nextLine();
	    		params = line.split(" ");
	    		command = params[0];
	    		
	    		switch (command) {
	    		
	    		case "SETM": 
	    			
	    			id = Integer.parseInt(params[1]);
	    			rowDim = Integer.parseInt(params[2]);
	    			colDim = Integer.parseInt(params[3]);
	    			
	    			setM(id, rowDim, colDim);
	    			
	    			break;
	    			
	    		case "SETV":
	    			
	    			id = Integer.parseInt(params[1]);
	    			row = Integer.parseInt(params[2]);
	    			col = Integer.parseInt(params[3]);
	    			val = Double.parseDouble(params[4]);
	    			
	    			setV(id, row, col, val);
	    		    
	    			break;
	    		
	    		case "GETV": 
	    			
	    			id = Integer.parseInt(params[1]);
	    			row = Integer.parseInt(params[2]);
	    			col = Integer.parseInt(params[3]);
	    			
	    			getV(id, row, col);
	    			
	    			break;
	    		
	    		case "DELETE":
	    			
	    			if (params[1].equalsIgnoreCase("ALL")) {
	    				deleteAll();
	    			} else {
	    				id = Integer.parseInt(params[1]);
	    				deleteM(id);
	    			}
	    			
	    			break;
	    			
	    		case "ADD":
	    			
	    			destID = Integer.parseInt(params[1]);
	    			srcID1 = Integer.parseInt(params[2]);
	    			srcID2 = Integer.parseInt(params[3]);
	    			
	    			addSub(destID, srcID1, srcID2, '+');
	    			
	    			break;
	    		
	    		case "SUB":
	    			
	    			destID = Integer.parseInt(params[1]);
	    			srcID1 = Integer.parseInt(params[2]);
	    			srcID2 = Integer.parseInt(params[3]);
	    			
	    			addSub(destID, srcID1, srcID2, '-');
	    			
	    			break;
	    			
	    		case "MULT":
	    			
	    			destID = Integer.parseInt(params[1]);
	    			srcID1 = Integer.parseInt(params[2]);
	    			srcID2 = Integer.parseInt(params[3]);
	    			
	    			mult(destID, srcID1, srcID2);
	    			
	    			break;
	    			
	    		case "TRANSPOSE":
	    			
	    			destID = Integer.parseInt(params[1]);
	    			srcID = Integer.parseInt(params[2]);
	    			
	    			transpose(destID, srcID);
	    			
	    			break;
	    			
	    		case "SQL":
	    			
	    			queryString = "";
	    			
	    			for(int i=1; i<params.length; i++) {
	    				queryString += params[i] + " ";
	    			}
	    			
	    			execute(queryString);
	    			
	    			break;
	    			
	    		default: System.out.println("Unknown command.");
	    			
	    		}
	    	}
	    	
	    } catch (IOException e) {
	    	
	    	System.out.println("File not found.");
	    	
	    } catch (Exception e) {
	    	
	    	e.printStackTrace();
	    	
	    } finally {
	    	
	    	if (in != null) {
	    		in.close();
	    	}
	    	
	    	if (con != null) {
	    		con.close();
	    	}
	    }
    
    }
    
    // Create a new matrix
    static void setM(int id, int rowDim, int colDim) throws SQLException {
	    
    	PreparedStatement ps = null;
	    ResultSet rs = null;
	    String query = null;
	    
	    // check invalid inputs
    	if (rowDim < 1 || colDim < 1 || id < 0) {
    		System.out.println("ERROR");
    		return;
    	}
    	
	    try {
	    	
	    	// check if already exists
	    	query = "SELECT * FROM MATRIX WHERE MATRIX_ID=?";
			ps = con.prepareStatement(query);
			ps.setInt(1, id);
			rs = ps.executeQuery();
			
			if (rs != null) {
				
				if (!rs.next()) {
					
					// doesnt exist, create a new one
					query = "INSERT INTO MATRIX VALUES(?,?,?)";
					ps = con.prepareStatement(query);
					ps.setInt(1, id);
					ps.setInt(2, rowDim);
					ps.setInt(3, colDim);
					ps.executeUpdate();
					
					System.out.println("DONE");
					return;
					
				} else {
					
					// already exists, check if possible to contract
					int maxRow, maxCol;
					query = "SELECT max(ROW_NUM) AS MAX_ROW_NUM, max(COL_NUM) AS MAX_COL_NUM "
							+ "FROM MATRIX_DATA WHERE MATRIX_ID=?";
					ps = con.prepareStatement(query);
					ps.setInt(1, id);
					rs = ps.executeQuery();
					
					while (rs.next()) {
						
						maxRow = rs.getInt("MAX_ROW_NUM");
						maxCol = rs.getInt("MAX_COL_NUM");
						
						if (rowDim >= maxRow && colDim >= maxCol) {
							
							query = "UPDATE MATRIX "
									+ "SET ROW_DIM=?, COL_DIM=? "
									+ "WHERE MATRIX_ID=?";
							ps = con.prepareStatement(query);
							ps.setInt(1, rowDim);
							ps.setInt(2, colDim);
							ps.setInt(3, id);
							ps.executeUpdate();
							
							System.out.println("DONE");
							return;
							
						}
					}
				}
			}
			
			System.out.println("ERROR");
			
	    } catch (Exception e) {
	    	e.printStackTrace();
	    } finally {
	    	if (ps != null) ps.close();
	    	if (rs != null) rs.close();
	    }
    }
    
    // Set the value of specified matrix's row and column to val
    static void setV(int id, int row, int col, double val) throws SQLException {
    	
    	PreparedStatement ps = null;
	    ResultSet rs = null;
	    String query = null;
	    int rowDim, colDim;
	    
	    // check invalid inputs
	    if (row < 1 || col < 1 || id < 0) {
	    	System.out.println("ERROR");
	    	return;
	    }
	    
	    try {
			
	    	// check if matrix exists
	    	query = "SELECT * FROM MATRIX WHERE MATRIX_ID=?";
			ps = con.prepareStatement(query);
			ps.setInt(1, id);
			rs = ps.executeQuery();
			
			if (rs != null) {
					
				while (rs.next()) {
					
					rowDim = rs.getInt("ROW_DIM");
					colDim = rs.getInt("COL_DIM");
					
					if (row <= rowDim && col <= colDim) {
						
						// exists and in range of dimensions, delete old one and insert new one
						query = "DELETE FROM MATRIX_DATA "
								+ "WHERE MATRIX_ID=? AND ROW_NUM=? AND COL_NUM=?";
						ps = con.prepareStatement(query);
						ps.setInt(1, id);
						ps.setInt(2, row);
						ps.setInt(3, col);
						ps.executeUpdate();
						
						// if the value is 0, we don't store it
						if (val != 0) {
							query = "INSERT INTO MATRIX_DATA VALUES(?,?,?,?)";
							ps = con.prepareStatement(query);
							ps.setInt(1, id);
							ps.setInt(2, row);
							ps.setInt(3, col);
							ps.setDouble(4, val);
							ps.executeUpdate();
						}
						
						System.out.println("DONE");
						return;	
					}
				}
			}
			
			System.out.println("ERROR");
			
	    } catch (Exception e) {
	    	e.printStackTrace();
	    } finally {
	    	if (ps != null) ps.close();
	    	if (rs != null) rs.close();
	    }
    }
    
    // Get the value of specified matrix's row and column
    static void getV(int id, int row, int col) throws SQLException {
    	
    	PreparedStatement ps = null;
	    ResultSet rs = null;
	    String query = null;
	    double val;
	    int rowDim, colDim;
	    
	    // check invalid inputs
	    if (row < 1 || col < 1 || id < 0) {
	    	System.out.println("ERROR");
	    	return;
	    }
	    
	    try {
	    	
	    	// check if exists and return it
	    	query = "SELECT VALUE FROM MATRIX_DATA "
	    			+ "WHERE MATRIX_ID=? AND ROW_NUM=? AND COL_NUM=?";
	    	ps = con.prepareStatement(query);
	    	ps.setInt(1, id);
	    	ps.setInt(2, row);
	    	ps.setInt(3, col);
	    	rs = ps.executeQuery();
	    	
	    	if (rs != null) {
	    		
	    		while (rs.next()) {
	    			val = rs.getDouble("VALUE");
	    			System.out.println(val);
	    			return;
	    		}
	    	}
	    	
	    	// check if exists, but zero
	    	query = "SELECT * FROM MATRIX WHERE MATRIX_ID=?";
	    	ps = con.prepareStatement(query);
	    	ps.setInt(1, id);
	    	rs = ps.executeQuery();
	    	
	    	if (rs != null) {
	    		
	    		while (rs.next()) {
	    			
	    			rowDim = rs.getInt("ROW_DIM");
	    			colDim = rs.getInt("COL_DIM");
	    			
	    			if (row <= rowDim && col <= colDim) {
	    				System.out.println(0);
	    				return;
	    			}
	    		}
	    	}
	    	
	    	System.out.println("ERROR");
	    	
	    } catch (Exception e) {
	    	e.printStackTrace();
	    } finally {
	    	if (ps != null) ps.close();
	    	if (rs != null) rs.close();
	    }
    	
    }
    
    // Delete specified matrix
    static void deleteM(int id) throws SQLException {
    	
    	PreparedStatement ps = null;
	    String query = null;
	    
	    try {
	    	
	    	query = "DELETE FROM MATRIX WHERE MATRIX_ID=?";
	    	ps = con.prepareStatement(query);
	    	ps.setInt(1, id);
	    	ps.executeUpdate();
	    	
	    	query = "DELETE FROM MATRIX_DATA WHERE MATRIX_ID=?";
	    	ps = con.prepareStatement(query);
	    	ps.setInt(1, id);
	    	ps.executeUpdate();
	    	
	    	System.out.println("DONE");
	    	
	    } catch (Exception e) {
	    	e.printStackTrace();
	    } finally {
	    	if (ps != null) ps.close();
	    }
    }
    
    // Delete all matrices
    static void deleteAll() throws SQLException {
    	
    	Statement stmt = null;
	    String query = null;
	    
	    try {
	    	
	    	query = "DELETE FROM MATRIX";
	    	stmt = con.createStatement();
	    	stmt.executeUpdate(query);
	    	
	    	query = "DELETE FROM MATRIX_DATA";
	    	stmt = con.createStatement();
	    	stmt.executeUpdate(query);
	    	
	    	System.out.println("DONE");
	    	
	    } catch (Exception e) {
	    	e.printStackTrace();
	    } finally {
	    	if (stmt != null) stmt.close();
	    }
    }
    
    // Add or subtract source matrices and save the result in destination matrix
    static void addSub(int destID, int srcID1, int srcID2, char op) {

	    double[][] sum = null;
	    double[][] matrix1 = null;
	    double[][] matrix2 = null;
	    
	    try {
		    
	    	matrix1 = getMatrix(srcID1);
	    	matrix2 = getMatrix(srcID2);
		    
	    	if (matrix1 == null || matrix2 == null || 
	    		matrix1.length != matrix2.length || matrix1[0].length != matrix2[0].length) {
	    		System.out.println("ERROR");
	    		return;
	    	}
	    	
		    sum = new double[matrix1.length][matrix1[0].length];
		    
		    for(int i=0; i<matrix1.length; i++) {
		    	for(int j=0; j<matrix1[0].length; j++) {
		    		if (op == '+') sum[i][j] = matrix1[i][j] + matrix2[i][j];
		    		else sum[i][j] = matrix1[i][j] - matrix2[i][j];
		    	}
		    }
		    
		    saveMatrix(destID, sum);
	    	
	    	System.out.println("DONE");
		    
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	    
    }
    
    // Multiply source matrices and save the result in destination matrix
    static void mult(int destID, int srcID1, int srcID2) {
    	
	    double[][] product = null;
	    double[][] matrix1 = null;
	    double[][] matrix2 = null;
	    double sum = 0;
	    
	    try {
	    	
	    	matrix1 = getMatrix(srcID1);
	    	matrix2 = getMatrix(srcID2);
		    
	    	if (matrix1 == null || matrix2 == null || matrix1[0].length != matrix2.length) {
	    		System.out.println("ERROR");
	    		return;
	    	}
	    	
		    product = new double[matrix1.length][matrix2[0].length];
		    
		    for(int i=0; i<matrix1.length; i++) {
		    	
                for(int j=0; j<matrix2[0].length; j++) {
                	
                	sum = 0;
                	
                	for(int k=0; k<matrix2.length; k++) {
                		sum += matrix1[i][k] * matrix2[k][j];
                	}
                	
                	product[i][j] = sum;
                }
		    }
		    
		    saveMatrix(destID, product);
		    
		    System.out.println("DONE");
		    
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
    }
    
    // Transpose the source matrix and save it in destination matrix
    static void transpose(int destID, int srcID) throws SQLException {
    	
    	double[][] matrix = getMatrix(srcID);
    	
    	if (matrix == null) {
    		System.out.println("ERROR");
    		return;
    	}
    	
    	double[][] matrixT = new double[matrix[0].length][matrix.length];
    	
    	for(int i=0; i<matrixT.length; i++) {
    		for(int j=0; j<matrixT[0].length; j++) {
    			matrixT[i][j] = matrix[j][i];
    		}
    	}
    	
    	saveMatrix(destID, matrixT);
    	System.out.println("DONE");
    }
    
    // Execute the given query string
    static void execute(String queryString) throws SQLException {
    	
    	ResultSet rs = null;
    	
    	try {
    		
    		Statement stmt = con.createStatement();
    		rs = stmt.executeQuery(queryString);
    		
    		if (rs != null) {
    			
    			while (rs.next()) {
    				System.out.println(rs.getString(1));
    			}
    		}
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    	} finally {
	    	if (rs != null) rs.close();
	    }
    }
    
    // Save given matrix to database
    static void saveMatrix(int id, double[][] matrix) throws SQLException {
    	
    	PreparedStatement ps = null;
	    String query = null;
	    
	    try {
	    	
	    	query = "DELETE FROM MATRIX WHERE MATRIX_ID=?";
	    	ps = con.prepareStatement(query);
	    	ps.setInt(1, id);
	    	ps.executeUpdate();
	    	
	    	query = "DELETE FROM MATRIX_DATA WHERE MATRIX_ID=?";
	    	ps = con.prepareStatement(query);
	    	ps.setInt(1, id);
	    	ps.executeUpdate();
	    	
	    	query = "INSERT INTO MATRIX VALUES(?,?,?)";
	    	ps = con.prepareStatement(query);
	    	ps.setInt(1, id);
	    	ps.setInt(2, matrix.length);
	    	ps.setInt(3, matrix[0].length);
	    	ps.executeUpdate();
	    	
	    	query = "INSERT INTO MATRIX_DATA VALUES(?,?,?,?)";
	    	ps = con.prepareStatement(query);
	    	
	    	for(int i=0; i<matrix.length; i++) {
	    		for(int j=0; j<matrix[0].length; j++) {
	    			if (matrix[i][j] != 0) {
	    				ps.setInt(1, id);
	    				ps.setInt(2, i+1);
	    				ps.setInt(3, j+1);
	    				ps.setDouble(4, matrix[i][j]);
	    				ps.executeUpdate();
	    			}
	    		}
	    	}
	    	
	    } catch (Exception e) {
	    	e.printStackTrace();
	    } finally {
	    	if (ps != null) ps.close();
	    }
    }
    
    // Get the specified matrix
    static double[][] getMatrix(int id) throws SQLException {
    	
    	double[][] matrix = null;
    	PreparedStatement ps = null;
	    ResultSet rs = null;
	    String query = null;
	    int rowDim, colDim;
	    int row, col;
	    double val;
	    
	    try {
		    
	    	query = "SELECT * FROM MATRIX WHERE MATRIX_ID=?";
		    ps = con.prepareStatement(query);
		    ps.setInt(1, id);
		    rs = ps.executeQuery();
		    
		    if (rs != null) {
		    	
		    	while (rs.next()) {
		    		rowDim = rs.getInt("ROW_DIM");
		    		colDim = rs.getInt("COL_DIM");
		    		matrix = new double[rowDim][colDim];
		    	}
		    }
		    
		    if (matrix == null) return matrix;
		    
		    query = "SELECT * FROM MATRIX_DATA WHERE MATRIX_ID=?";
		    ps = con.prepareStatement(query);
		    ps.setInt(1, id);
		    rs = ps.executeQuery();
		    
		    if (rs != null) {
		    	
		    	while (rs.next()) {
		    		
		    		row = rs.getInt("ROW_NUM");
		    		col = rs.getInt("COL_NUM");
		    		val = rs.getDouble("VALUE");
		    		
		    		matrix[row-1][col-1] = val;
		    	}
		    }
		    
	    } catch (Exception e) {
	    	e.printStackTrace();
	    } finally {
	    	if (ps != null) ps.close();
	    	if (rs != null) rs.close();
	    }
	    
    	return matrix;
    }
    
    
}
