package com.ubtedu.base.storage.core;

import android.os.Environment;

import androidx.test.runner.AndroidJUnit4;

import com.ubtedu.base.storage.StorageUtils;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Bright. Create on 2017/6/13.
 */
@RunWith(AndroidJUnit4.class)
public class AbstractDiskStorageTest {

    private ExternalStorage storage = StorageUtils.getExternalStorage();

    String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath() +
            File.separator + "Ubtedu" + File.separator + "Alpha1X";

    @Test
    public void createDirectory() {
        assertTrue(storage.createDirectory(rootPath, true));
    }

    @Test
    public void createDirectory1() {
        assertTrue(storage.createDirectory(rootPath, true, true));
    }

    @Test
    public void deleteDirectory() {
        assertTrue(storage.deleteDirectory(rootPath, true));
    }

    @Test
    public void isDirectoryExists() {
        assertTrue(storage.isDirectoryExists(rootPath, true));
    }

    @Test
    public void createFile() {
        assertTrue(storage.createFile(rootPath, "test.txt", "for test\n", true));
    }

    @Test
    public void deleteFile() {
        assertTrue(storage.createFile(rootPath, "test1.txt", "for test\n", true));
        assertTrue(storage.deleteFile(rootPath, "test1.txt", true));
    }

    @Test
    public void isFileExist() {
        assertFalse(storage.isFileExist(rootPath, "test1.txt", true));
    }

    @Test
    public void readTextFile() {
        if (storage.isFileExist(rootPath, "test.txt", true)) {
            assertEquals("for test\n", storage.readTextFile(rootPath, "test.txt", true));
        }
    }

    @Test
    public void getFile() {
        assertNotNull(storage.getFile(rootPath, "test.txt", true));
        assertEquals(rootPath + "/test.txt",
                storage.getFile(rootPath, "test.txt", true).getAbsolutePath());
    }

    @Test
    public void getFile1() {
        assertNotNull(storage.getFile(rootPath + "/test.txt", true));
        assertEquals(rootPath + "/test.txt",
                storage.getFile(rootPath + "/test.txt", true).getAbsolutePath());
    }

    @Test
    public void rename() {
        storage.createFile(rootPath, "test3.txt", "nothing.", true);
        storage.rename(storage.getFile(rootPath + "/test3.txt", true), "test2.txt");
        assertEquals(rootPath + "/test3.txt",
                storage.getFile(rootPath + "/test3.txt", true).getAbsolutePath());
    }

    @Test
    public void copy() {
        storage.createFile(storage.buildAbsolutePath(), "/test.txt", "nothing", true);
        storage.copy(new File(storage.buildAbsolutePath() + "/test.txt"), rootPath, "what", true);
    }

    @Test
    public void move() {
        storage.createFile(storage.buildAbsolutePath(), "/test.txt", "nothing", true);
        storage.move(new File(storage.buildAbsolutePath() + "/test.txt"), rootPath, "what", true);
    }

    @Test
    public void buildAbsolutePath() {
        assertEquals("/storage/emulated/0", storage.buildAbsolutePath());
    }

}