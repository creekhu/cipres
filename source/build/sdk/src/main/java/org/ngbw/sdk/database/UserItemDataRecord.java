/*
 * UserItemDataRecord.java
 */
package org.ngbw.sdk.database;


import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ngbw.sdk.WorkbenchException;
import org.ngbw.sdk.core.types.RecordFieldType;
import org.ngbw.sdk.core.types.RecordType;


/**
 *
 * @author Roland H. Niedner
 * @author Paul Hoover
 *
 */
public class UserItemDataRecord extends GeneratedKeyRow implements DataRecord, Comparable<UserItemDataRecord> {

	// data fields


	private static final String TABLE_NAME = "data_records";
	private static final String KEY_NAME = "RECORD_ID";
	private final Column<Long> m_userDataId = new LongColumn("USERDATA_ID", false);
	private final Column<String> m_recordType = new StringColumn("RECORD_TYPE", false, 100);
	private Map<RecordFieldType, RecordField> m_fields;


	// constructors


	public UserItemDataRecord(RecordType type, Set<RecordFieldType> fields, UserDataItem owner)
	{
		this(type, owner);

		for (Iterator<RecordFieldType> types = fields.iterator() ; types.hasNext() ; ) {
			RecordFieldType fieldType = types.next();

			m_fields.put(fieldType, new RecordField(fieldType));
		}
	}

	public UserItemDataRecord(RecordType type, Map<RecordFieldType, Object> values, UserDataItem owner)
	{
		this(type, owner);

		for (Iterator<Map.Entry<RecordFieldType, Object>> entries = values.entrySet().iterator() ; entries.hasNext() ; ) {
			Map.Entry<RecordFieldType, Object> entry = entries.next();
			RecordFieldType fieldType = entry.getKey();

			m_fields.put(fieldType, new RecordField(fieldType, entry.getValue()));
		}
	}

	public UserItemDataRecord(DataRecord other, UserDataItem owner)
	{
		this(other.getRecordType(), owner);

		for (Iterator<RecordField> fields = other.getFields().iterator() ; fields.hasNext() ; ) {
			RecordField field = fields.next();

			m_fields.put(field.getFieldType(), new RecordField(field));
		}
	}

	public UserItemDataRecord(long recordId) throws IOException, SQLException
	{
		this();

		m_key.assignValue(recordId);

		load();
	}

	UserItemDataRecord(Connection dbConn, long recordId) throws IOException, SQLException
	{
		this();

		m_key.assignValue(recordId);

		load(dbConn);
	}

	UserItemDataRecord(DataRecord other)
	{
		this(other.getRecordType());

		for (Iterator<RecordField> fields = other.getFields().iterator() ; fields.hasNext() ; ) {
			RecordField field = fields.next();

			m_fields.put(field.getFieldType(), new RecordField(field));
		}
	}

	private UserItemDataRecord()
	{
		super(TABLE_NAME, KEY_NAME);

		construct(m_userDataId, m_recordType);
	}

	private UserItemDataRecord(RecordType type)
	{
		this();

		m_recordType.setValue(type.toString());

		m_fields = new EnumMap<RecordFieldType, RecordField>(RecordFieldType.class);
	}

	private UserItemDataRecord(RecordType type, UserDataItem owner)
	{
		this(type);

		if (!owner.isNew())
			setUserDataId(owner.getUserDataId());
	}


	// public methods


	public long getRecordId()
	{
		return m_key.getValue();
	}

	public long getUserDataId()
	{
		return m_userDataId.getValue();
	}

	@Override
	public RecordType getRecordType()
	{
		return RecordType.valueOf(m_recordType.getValue());
	}

	@Override
	public List<RecordField> getFields()
	{
		return new ArrayList<RecordField>(m_fields.values());
	}

	@Override
	public RecordField getField(RecordFieldType field)
	{
		return m_fields.get(field);
	}

	@Override
	public boolean equals(Object other)
	{
		if (other == null)
			return false;

		if (this == other)
			return true;

		if (other instanceof UserItemDataRecord == false)
			return false;

		UserItemDataRecord otherRecord = (UserItemDataRecord) other;

		if (isNew() || otherRecord.isNew())
			return false;

		return getRecordId() == otherRecord.getRecordId();
	}

	@Override
	public int hashCode()
	{
		return (new Long(getRecordId())).hashCode();
	}

	@Override
	public int compareTo(UserItemDataRecord other)
	{
		if (other == null)
			throw new NullPointerException("other");

		if (this == other)
			return 0;

		if (isNew())
			return -1;

		if (other.isNew())
			return 1;

		return (int) (getRecordId() - other.getRecordId());
	}


	// package methods


	void setUserDataId(Long userDataId)
	{
		m_userDataId.setValue(userDataId);
	}

	@Override
	void load(Connection dbConn) throws IOException, SQLException
	{
		super.load(dbConn);

		m_fields = getRecordFields(dbConn);
	}

	@Override
	void delete(Connection dbConn) throws IOException, SQLException
	{
		if (isNew())
			throw new WorkbenchException("Not persisted");

		delete(dbConn, getKey());

		m_key.reset();
	}

	static List<UserItemDataRecord> findDataRecords(Criterion key) throws IOException, SQLException
	{
		StringBuilder stmtBuilder = new StringBuilder("SELECT " + KEY_NAME + " FROM " + TABLE_NAME + " WHERE ");

		stmtBuilder.append(key.getPhrase());

		Connection dbConn = ConnectionManager.getConnectionSource().getConnection();
		PreparedStatement selectStmt = null;
		ResultSet recordRows = null;

		try {
			selectStmt = dbConn.prepareStatement(stmtBuilder.toString());

			key.setParameter(selectStmt, 1);

			recordRows = selectStmt.executeQuery();

			List<UserItemDataRecord> records = new ArrayList<UserItemDataRecord>();

			while (recordRows.next())
				records.add(new UserItemDataRecord(dbConn, recordRows.getLong(1)));

			return records;
		}
		finally {
			if (recordRows != null)
				recordRows.close();

			if (selectStmt != null)
				selectStmt.close();

			dbConn.close();
		}
	}

	static void delete(Connection dbConn, long recordId) throws IOException, SQLException
	{
		Criterion key = new LongCriterion(KEY_NAME, recordId);

		delete(dbConn, key);
	}


	// protected methods


	/**
	 *
	 */
	@Override
	protected void pushInsertOps()
	{
		for (RecordField field : m_fields.values() ) {
			Column<String> fieldName = new StringColumn("RECORD_FIELD", false, 100, field.getFieldType().toString());
			Column<String> fieldValue = new StringColumn("VALUE", true, 1023, field.getValueAsString());
			List<Column<?>> cols = new ArrayList<Column<?>>();

			cols.add(m_key);
			cols.add(fieldName);
			cols.add(fieldValue);

			m_opQueue.push(new InsertOp("record_fields", cols));
		}

		super.pushInsertOps();
	}

	/**
	 *
	 */
	@Override
	protected void pushUpdateOps()
	{
		for (RecordField field : m_fields.values() ) {
			if (field.isModified()) {
				Column<String> fieldName = new StringColumn("RECORD_FIELD", false, 100, field.getFieldType().toString());
				Column<String> fieldValue = new StringColumn("VALUE", true, 1023, field.getValueAsString());
				CompositeKey fieldKey = new CompositeKey(m_key, fieldName);

				m_opQueue.push(new UpdateOp("record_fields", fieldKey, fieldValue));
			}
		}

		super.pushUpdateOps();
	}


	// private methods


	private static void delete(Connection dbConn, Criterion recordKey) throws IOException, SQLException
	{
		(new DeleteOp("record_fields", recordKey)).execute(dbConn);
		(new DeleteOp(TABLE_NAME, recordKey)).execute(dbConn);
	}

	private Map<RecordFieldType, RecordField> getRecordFields(Connection dbConn) throws IOException, SQLException
	{
		PreparedStatement selectStmt = dbConn.prepareStatement("SELECT RECORD_FIELD, VALUE FROM record_fields WHERE " + KEY_NAME + " = ?");
		ResultSet fieldRows = null;

		try {
			m_key.setParameter(selectStmt, 1);

			fieldRows = selectStmt.executeQuery();

			Map<RecordFieldType, RecordField> fields = new EnumMap<RecordFieldType, RecordField>(RecordFieldType.class);

			while (fieldRows.next()) {
				RecordFieldType type = RecordFieldType.valueOf(fieldRows.getString(1));

				fields.put(type, new RecordField(type, fieldRows.getString(2)));
			}

			return fields;
		}
		catch (ParseException parseErr) {
			throw new AssertionError(parseErr);
		}
		finally {
			if (fieldRows != null)
				fieldRows.close();

			selectStmt.close();
		}
	}
}
