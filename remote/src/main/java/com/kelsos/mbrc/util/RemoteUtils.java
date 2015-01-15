package com.kelsos.mbrc.util;

import android.os.Environment;
import com.kelsos.mbrc.BuildConfig;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class RemoteUtils {

	private static final File SD_CARD = Environment.getExternalStorageDirectory();
	private static final File CACHE = new File(String.format("%s/Android/data/%s/cache",
			SD_CARD.getAbsolutePath(), BuildConfig.APPLICATION_ID));

	private RemoteUtils() { }

	public static String getTimeStamp() {
		final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyymmddhhmmss", Locale.getDefault());
		return simpleDateFormat.format(new Date());
	}

	public static boolean isNullOrEmpty(String string) {
		return (string == null || string.equals(""));
	}

	public static File getStorage() {
		return CACHE;
	}

}
