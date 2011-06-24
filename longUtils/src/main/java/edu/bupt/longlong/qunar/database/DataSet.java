package edu.bupt.longlong.qunar.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

public class DataSet {

	protected static final Logger logger = Logger.getLogger(DataSet.class);

	public static Connection getConnection(String poolName) {
		Connection connection = null;
		try {
			connection = DriverManager.getConnection("proxool." + poolName);
		} catch (SQLException e1) {
			logger.error("get pool connection error:" + poolName, e1);
		}
		return connection;
	}

	public static boolean update(String poolName, String sql, String[] values) {
		Connection connection = null;
		try {
			connection = DriverManager.getConnection("proxool." + poolName);
		} catch (SQLException e1) {
			logger.error("get pool connection error:" + poolName, e1);
		}

		if (connection == null) {
			return false;
		}

		PreparedStatement statement = null;
		try {
			if (!connection.getAutoCommit()) {
				connection.setAutoCommit(true);
			}
			statement = connection.prepareStatement(sql);
			for (int i = 0; values != null && i < values.length; i++) {
				statement.setString(i + 1, values[i]);
			}
			return statement.execute();
		} catch (Exception e) {
			logger.error("update error for sql:" + sql, e);
			return false;
		} finally {
			try {
				if (statement != null) {
					statement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				logger.error("Problem closing connection", e);
			}
		}
	}

	public static int[] update(String poolName, String sql, String[][] values) {
		Connection connection = null;
		try {
			connection = DriverManager.getConnection("proxool." + poolName);
		} catch (SQLException e1) {
			logger.error("get pool connection error:" + poolName, e1);
		}

		if (connection == null) {
			return null;
		}

		PreparedStatement statement = null;
		try {
			if (connection.getAutoCommit()) {
				connection.setAutoCommit(false);
			}
			statement = connection.prepareStatement(sql);
			for (int recIndex = 0; values != null && recIndex < values.length; recIndex++) {
				for (int i = 0; i < values[recIndex].length; i++) {
					statement.setString(i + 1, values[recIndex][i]);
				}
				statement.addBatch();
			}
			int[] result = statement.executeBatch();
			connection.commit();
			return result;
		} catch (Exception e) {
			logger.error(sql, e);
			return null;
		} finally {
			try {
				if (statement != null) {
					statement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				logger.error("Problem closing connection", e);
			}
		}
	}

	private static String[][] getStringData(ResultSet rs, int count, int fieldCount) {
		try {
			String[][] data = new String[count][fieldCount];

			int size = 0;
			String str;
			while (rs.next()) {
				if (size == count) {
					count = (count * 3) / 2 + 1;
					String[][] oldData = data;
					data = new String[count][fieldCount];
					System.arraycopy(oldData, 0, data, 0, size);
				}
				for (int i = 0; i < fieldCount; i++) {
					str = rs.getString(i + 1);
					data[size][i] = str;
				}
				size++;
			}
			if (size == data.length) {
				return data;
			} else {
				String[][] re = new String[size][fieldCount];
				System.arraycopy(data, 0, re, 0, size);
				return re;
			}
		} catch (Exception e) {
			logger.error("get data from resultset error", e);
			return null;
		}
	}

	public static String[][] query(String poolName, String sql, String[] values) {
		return query(poolName, sql, values, 0, 0);
	}

	/**
	 * 参数的方式,避免SQL注入攻击
	 * 
	 * @param poolName
	 * @param sql
	 * @param values
	 * @param offset
	 * @param count
	 * @return
	 */
	public static String[][] query(String poolName, String sql, String[] values, int offset, int count) {
		Connection connection = null;
		try {
			connection = DriverManager.getConnection("proxool." + poolName);
		} catch (SQLException e1) {
			logger.error("get pool connection error:" + poolName, e1);
		}

		if (connection == null) {
			return null;
		}

		PreparedStatement stmt = null;
		ResultSet rs = null;
		String[][] data = null;
		try {
			if (offset > 0) {
				stmt = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			} else {
				stmt = connection.prepareStatement(sql);
			}

			for (int i = 0; i < values.length; i++)
				stmt.setString(i + 1, values[i]);

			int fetchSize = count;
			if (fetchSize > 100 || fetchSize < 1) fetchSize = 100;

			if (count > 0) {
				stmt.setMaxRows(offset + count);
				stmt.setFetchSize(fetchSize);
			} else {
				count = 100;
			}

			rs = stmt.executeQuery();
			if (offset > 0) rs.absolute(offset);

			data = getStringData(rs, count, rs.getMetaData().getColumnCount());
		} catch (Exception e) {
			logger.error("query error for sql:" + sql + ",Params:" + values.toString(), e);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException ex) {
				logger.error("Problem closing connection", ex);
			}
		}
		return data;
	}

	public static String[][] query(String poolName, String sql) {
		return query(poolName, sql, 0, 0);
	}

	public static String[][] query(String poolName, String sql, int offset, int count) {
		Connection connection = null;
		try {
			connection = DriverManager.getConnection("proxool." + poolName);
		} catch (SQLException e1) {
			logger.error("get pool connection error:" + poolName, e1);
		}

		if (connection == null) {
			return null;
		}

		Statement stmt = null;
		ResultSet rs = null;
		String[][] data = null;
		try {
			if (offset > 0) {
				stmt = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			} else {
				stmt = connection.createStatement();
			}

			int fetchSize = count;
			if (fetchSize > 100 || fetchSize < 1) fetchSize = 100;

			if (count > 0) {
				stmt.setMaxRows(offset + count);
				//hint
				stmt.setFetchSize(fetchSize);
			} else {
				count = 100;
			}

			rs = stmt.executeQuery(sql);
			if (offset > 0) rs.absolute(offset);

			data = getStringData(rs, count, rs.getMetaData().getColumnCount());
		} catch (Exception e) {
			logger.error("query error for sql:" + sql, e);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException ex) {
				logger.error("Problem closing connection", ex);
			}
		}
		return data;
	}
}
