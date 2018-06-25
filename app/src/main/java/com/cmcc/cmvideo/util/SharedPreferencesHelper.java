package com.cmcc.cmvideo.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Yyw on 2018/6/25.
 * Describe:
 */

public class SharedPreferencesHelper {
    private static final String APP_NAME = "com.cmcc.cmvideo";

    public static SharedPreferencesHelper instance;
    protected Context mContext;
    protected SharedPreferences sharedPreferences;

    public SharedPreferencesHelper(Context context) {
        mContext = context;
        if (mContext != null) {
            sharedPreferences = mContext.getSharedPreferences(APP_NAME, 0);
        }
    }

    /**
     * 线程安全调用
     *
     * @param context
     * @return
     */
    public static synchronized SharedPreferencesHelper getInstance(Context context) {
        if (null == instance && context != null) {
            instance = new SharedPreferencesHelper(context.getApplicationContext());
        }
        return instance;
    }

    public String getValue(String key) {
        if (sharedPreferences != null) {
            return sharedPreferences.getString(key, "");
        } else {
            return "";
        }
    }

    public Boolean getBoolValue(String key) {
        if (sharedPreferences != null) {
            return sharedPreferences.getBoolean(key, false);
        } else {
            return false;
        }
    }

    public Boolean getBoolean(String key, boolean defaultValue) {
        if (sharedPreferences != null) {
            return sharedPreferences.getBoolean(key, defaultValue);
        } else {
            return defaultValue;
        }
    }

    public int getIntValue(String key) {
        if (sharedPreferences != null) {
            return sharedPreferences.getInt(key, 0);
        } else {
            return 0;
        }
    }

    public int getIntValue(String key, int defaultValue) {
        if (sharedPreferences != null) {
            return sharedPreferences.getInt(key, defaultValue);
        } else {
            return defaultValue;
        }
    }

    public long getLongValue(String key) {
        if (sharedPreferences != null) {
            return sharedPreferences.getLong(key, 0);
        } else {
            return 0;
        }
    }

    public float getFloatValue(String key) {
        if (sharedPreferences != null) {
            return sharedPreferences.getFloat(key, 0f);
        } else {
            return 0f;
        }
    }

    public float getFloatValue(String key, float defaultValue) {
        if (sharedPreferences != null) {
            return sharedPreferences.getFloat(key, defaultValue);
        } else {
            return defaultValue;
        }
    }

    public void setValue(String key, Boolean value) {
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (editor != null) {
                editor.putBoolean(key, value);
                editor.apply();
            }
        }
    }

    public void setValue(String key, String value) {
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (editor != null) {
                editor.putString(key, value);
                editor.apply();
            }
        }
    }

    public void setValue(String key, int value) {
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (editor != null) {
                editor.putInt(key, value);
                editor.apply();
            }
        }
    }

    public void setValue(String key, long value) {
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (editor != null) {
                editor.putLong(key, value);
                editor.apply();
            }
        }
    }

    public void setValue(String key, float value) {
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (editor != null) {
                editor.putFloat(key, value);
                editor.apply();
            }
        }
    }

    public void remove(String key) {
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (editor != null) {
                editor.remove(key);
                editor.apply();
            }
        }
    }

    public void clear() {
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (editor != null) {
                editor.clear();
                editor.apply();
            }
        }
    }

    public Set<String> getSaveKeys() {
        if (sharedPreferences != null && sharedPreferences.getAll() != null) {
            return sharedPreferences.getAll().keySet();
        }
        return new HashSet<>();
    }
}
