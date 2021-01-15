package com.ubtedu.ukit.doc;


import androidx.annotation.Nullable;

import com.ubtedu.alpha1x.utils.MD5Util;
import com.ubtedu.alpha1x.utils.TimeUtil;
import com.ubtedu.ukit.project.vo.Project;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class ProjectTest {
    private String createId() {
        return MD5Util.encodeByMD5(String.valueOf(ramdomTime())).toLowerCase();
    }

    Random mRandom = new Random(1000);

    private long ramdomTime() {
        return System.currentTimeMillis() + mRandom.nextInt();
    }

    @Test
    public void tests() {
        Comparator<Integer> mReverseTimeComparator = new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o2 - o1;
            }
        };

        List<Integer> integers = new ArrayList<>();
        integers.add(10);
        integers.add(8);
        integers.add(7);
        integers.add(5);
        integers.add(9);
        integers.add(3);

        Collections.sort(integers, mReverseTimeComparator);
        System.currentTimeMillis();

    }

    private String test(boolean isPhone, String account) {
        if (isPhone) {
            int explicitLength = 3;
            int length = account.length();
            if (length <= 3) {
                explicitLength = 1;
            } else if (length <= 5) {
                explicitLength = 2;
            }
            return account.substring(0, explicitLength) + "*****" + account.substring(account.length() - explicitLength, account.length());
        } else {
            if (account.contains("@")) {
                String[] strs = account.split("@");
                if (strs.length == 2) {
                    String prefix = strs[0];
                    if (strs[0].length() >= 3) {
                        prefix = strs[0].substring(0, 3);
                    } else if (strs[0].length() >= 2) {
                        prefix = strs[0].substring(0, 2);
                    } else if (strs[0].length() >= 1) {
                        prefix = strs[0].substring(0, 1);
                    }
                    String[] suffixs = strs[1].split("\\.");
                    String suffix = suffixs[suffixs.length - 1];
                    return prefix + "*****" + suffix;
                }

            }
            return account;
        }
    }

    public static String join(String... paths) {
        StringBuilder pathBuilder = new StringBuilder();
        if (paths != null && paths.length > 1) {
            for (int i = 0; i < paths.length; i++) {
                if (i == paths.length - 1) {

                    pathBuilder.append(paths[paths.length - 1]);
                } else {
                    pathBuilder.append(paths[i]).append(File.separator);
                }
            }
        }
        return pathBuilder.toString();
    }

    @Test
    public void testTime() {
        String s1 = TimeUtil.milliseconds2String(100);
        String s2 = TimeUtil.milliseconds2String(500);
        String s3 = TimeUtil.milliseconds2String(800);
        String s4 = TimeUtil.milliseconds2String(1000);
        System.out.println(s1);
        System.out.println(s2);
        System.out.println(s3);
        System.out.println(s4);
    }
    @Test
    public void testInternalProject(){
        List<Project> projects = new ArrayList<>();
        Project project = new InternalProject();

        Project project2 = new InternalProject();
        project2.projectId = project.projectId;
        projects.add(project);
        projects.remove(project2);
        Assert.assertEquals(project, project2);
    }
    private static class InternalProject extends Project {
        @Override
        public boolean equals(@Nullable Object obj) {
            StackTraceElement[] xx = Thread.currentThread().getStackTrace();
            if (obj instanceof Project) {
                Project p = (Project) obj;
                return projectId.equals(p.projectId);
            }
            return false;
        }
    }
}