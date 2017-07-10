package com.ppzhu.calendar.constants;

import android.net.Uri;
import android.provider.BaseColumns;

public class FestiValColums implements BaseColumns {

	public static final String TABLE_NAME = "festival";

	public static final String TIME = "time";

	public static final String IS_FESTIVAL = "isfetival";

	public static final String YEAR = "year";
	
	public static final String MONTH = "month";

	public static final String NAME = "name";

	public static final String AUTOHORITY = "com.studyos.calendar";
	
	public static final int DB_VERSION = 10;
	
	public static final String DB_NAME = "calendar.db";
	
	public static final int ITEM = 1;
	
	public static final int ITEM_ID = 2;
	
	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.studyos.calendar";
	
	public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.studyos.calendar";

	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTOHORITY+"/"+TABLE_NAME);

}
