package com.cmcc.cmvideo.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.os.MemoryFile;
import android.text.TextUtils;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

public class FileUtil {

    // MSC临时文件保存路径
    private static String userPath = "";

    private final static String FORMAT_WAV = "wav";
    private final static String FORMAT_PCM = "pcm";

    private final static String FOLDER_NAME = "msc";
    private final static String CHILD_FOLDER_NAME = "res";
    public static final int SIZE_64K = 1024 * 64;

    /**
     * 识别资源文件后缀名
     */
    public static final String RES_SUFFIX = ".jet";

    /**
     * 获取MSC临时文件保存路径
     * @param context
     * @return
     */
    public static String getUserPath(Context context)
    {
        if(!TextUtils.isEmpty(userPath))
            return userPath;
        File fdir = context.getFilesDir();
        String path = fdir.getAbsolutePath();
        if(!path.endsWith("/"))
            path += "/";
        path += "msclib/";
        File pf = new File(path);
        if(!pf.exists())
            pf.mkdirs();
        userPath = path;
        return userPath;
    }

    public static String getResFilePath(Context context, String resName) {
        // get fileName
        if(TextUtils.isEmpty(resName)){
            resName = System.currentTimeMillis()+"";
        };
        String filePath = resName;

        // get folder path
        String folderPath = null;
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            folderPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            folderPath = context.getFilesDir().getAbsolutePath();
        }

        if(!folderPath.endsWith("/")) {
            folderPath+="/";
        }
        folderPath += FOLDER_NAME+"/";
        folderPath += CHILD_FOLDER_NAME+"/";

        File pf = new File(folderPath);
        if(!pf.exists())
            pf.mkdirs();
        filePath = folderPath + filePath;

        if(!filePath.endsWith(RES_SUFFIX)) {
            filePath += RES_SUFFIX;
        }
        return filePath;
    }

    /**
     * 删除文件
     * @param file 文件绝对路径
     */
    public static void deleteFile(String file)
    {
        File f = new File(file);
        if(f.exists())
        {
            f.delete();
        }
    }

    /**
     * 保存内存映射文件
     * @param io 内存文件句柄
     * @param length 文件长度
     * @param filename 文件名
     * @return
     */
    public static boolean saveFile(MemoryFile io, long length, String filename)
    {
        if(io == null || TextUtils.isEmpty(filename))
            return false;
        boolean ret = false;
        FileOutputStream out = null;
        try {
            deleteFile(filename);
            makeDir(filename);
            out = new FileOutputStream(filename);
            int offset = 0;
            byte[] buffer = new byte[SIZE_64K];
            while(offset < length)
            {
                int readAble = (int) ((length-offset) > SIZE_64K ? SIZE_64K : (length-offset));
                io.readBytes(buffer, offset,0,readAble);
                out.write(buffer, 0, readAble);
                offset += readAble;
            }
            ret = true;
        } catch (Exception e) {

        }finally
        {
            try{
                if(out != null){
                    out.close();
                    out = null;
                }
            }catch (Exception e) {
            }
        }
        return ret;
    }

    /**
     * 读取文件
     * @param filename
     * @return
     */
    public static byte[] readFile(String filename)
    {
        byte[] buffer = null;
        FileInputStream in = null;
        try {
            File f = new File(filename);
            if(!f.exists())
                return null;
            in = new FileInputStream(f);
            buffer = new byte[in.available()];
            in.read(buffer);
        } catch (Exception e) {

        }finally
        {
            try {
                if(in != null)
                {
                    in.close();
                    in = null;
                }
            } catch (IOException e) {

            }
        }
        return buffer;
    }

    /**
     * 读取asset文件
     * @param context
     * @param fileName
     * @return
     */
    public static byte[] readFileFromAssets(Context context, String fileName){
        byte[] buffer = null;
        InputStream in = null;
        try {
            in = context.getAssets().open(fileName);
            buffer = new byte[in.available()];
            in.read(buffer);
        } catch (Exception e) {

        }finally
        {
            try {
                if(in != null)
                {
                    in.close();
                    in = null;
                }
            } catch (IOException e) {

            }
        }
        return buffer;
    }


    /**
     * 保存文件
     * @param buffer
     * @param filename
     * @return
     */
    public static boolean saveFile(byte[] buffer, String filename, boolean append, int writeoffset)
    {
        boolean ret = false;
        RandomAccessFile out = null;
        try {
            if(!append)
                deleteFile(filename);
            makeDir(filename);
            out = new RandomAccessFile(filename,"rw");
            if(!append)
            {
                out.setLength(0);
            }
            else if(writeoffset < 0)
            {
                out.seek(out.length());
            }else
                out.seek(writeoffset);

            out.write(buffer);
            ret = true;
        } catch (Exception e) {

        }finally
        {
            try{
                if(out != null){
                    out.close();
                    out = null;
                }
            }catch (Exception e) {
            }
        }
        return ret;
    }

    /**
     * 保存文件
     * @param datas
     * @param fpath
     */
    public static boolean saveFile(ConcurrentLinkedQueue<byte[]> datas, String fpath)
    {
        boolean ret = false;
        FileOutputStream fos = null;
        try
        {
            makeDir(fpath);
            fos = new FileOutputStream(fpath);
            Iterator<byte[]> itor = datas.iterator();
            byte[] buffer = null;
            while(itor.hasNext())
            {
                buffer = itor.next();
                fos.write(buffer);
            }
            fos.close();
            fos = null;
            ret = true;
        } catch (Exception e)
        {

        }finally
        {
            try {
                if(fos != null)
                    fos.close();
            } catch (Exception e) {

            }
        }
        return ret;
    }
    /**
     * pcm加wav头
     */
    public static boolean pcm2Wav(String mLocal_save_path,int sample)
    {
        File file = new File(mLocal_save_path);
        try {
            WavWriter writer = new WavWriter(file, sample);
            writer.writeHeader();
        } catch (IOException e) {
            return false;
        }
        return true;
    }
   /**
     * 格式化pcm，目前只支持转wav
     * @param format
     * @param mLocal_save_path
     * @param sample
     * @return
     */
    public static boolean formatPcm(String format, String mLocal_save_path, int sample){
        if(TextUtils.isEmpty(format)) {
            return true;
        }

        if(FORMAT_PCM.equals(format)) {
            return true;
        }

        if(FORMAT_WAV.equals(format))
        {
            return FileUtil.pcm2Wav(mLocal_save_path, sample);
        }else {
            return false;
        }
    }

    /**
     * 创建文件路径
     * @param path 文件路径或文件名，例如：/sdcard/msc/ 或 /sdcard/msc/msc.log
     */
    public static void makeDir(String path)
    {
        if(TextUtils.isEmpty(path))
            return;
        File f = new File(path);
        if(!path.endsWith("/"))
            f =  f.getParentFile();
        if(!f.exists())
            f.mkdirs();
    }
    /**
     * 复制asset文件
     * @param context
     * @throws IOException
     */
    public static void copyAssetsFile(Context context, String assetpath, String despath) throws IOException
    {
        InputStream in = null;
        FileOutputStream out = null;

        try {
            AssetManager am = context.getAssets();
            in = am.open(assetpath);
            makeDir(despath);
            out = new FileOutputStream(despath,false);
            byte[] temp = new byte[2048];
            int len = 0;
            while (( len = in.read(temp)) > 0) {
                out.write(temp, 0, len);
            }
        }finally
        {
            if(in != null)
            {
                in.close();
            }
            if(out != null)
            {
                out.close();
            }
        }
    }

    /**
     * 校验文件的MD5值
     *
     * @param filePath
     */
    public static boolean checkFileMD5(String fileMd5, String filePath) {
        // 根据已下载安装包的MD5值，判断是否是完好的更新包，如果不是，则删除本地包，重新下载，如果是下了半截的包，则应该继续下载
        String md5 = getFileMd5(new File(filePath));
        if (fileMd5.equals(md5)) {
            return true;
        } else {
            // APK包下载错误，删除本地APK
            deleteFile(filePath);
            return false;
        }
    }
    /**
     * 获取文件的MD5
     * @param file
     * @return
     */
    public static String getFileMd5(File file) {
        String value = null;
        if (!file.exists()) {
            return null;
        }

        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            MappedByteBuffer byteBuffer = in.getChannel().map(
                    FileChannel.MapMode.READ_ONLY, 0, file.length());
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(byteBuffer);
            BigInteger bi = new BigInteger(1, md5.digest());
            value = bi.toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return value;
    }
}
