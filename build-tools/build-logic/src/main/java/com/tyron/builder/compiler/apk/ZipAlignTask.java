package com.tyron.builder.compiler.apk;

import android.content.Context;

import com.tyron.builder.BuildModule;
import com.tyron.builder.compiler.BuildType;
import com.tyron.builder.compiler.Task;
import com.tyron.builder.exception.CompilationFailedException;
import com.tyron.builder.log.ILogger;
import com.tyron.builder.project.api.AndroidModule;
import com.tyron.common.ApplicationProvider;
import com.tyron.common.util.BinaryExecutor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ZipAlignTask extends Task<AndroidModule> {

    private static final String TAG = ZipAlignTask.class.getSimpleName();

    private File mApkFile;

    public ZipAlignTask(AndroidModule project, ILogger logger) {
        super(project, logger);
    }

    @Override
    public String getName() {
        return TAG;
    }

    @Override
    public void prepare(BuildType type) throws IOException {
        mApkFile = new File(getModule().getBuildDirectory(), "bin/generated.apk");

        if (!mApkFile.exists()) {
            throw new IOException("Unable to find signed apk file in projects build path");
        }
    }

    @Override
    public void run() throws IOException, CompilationFailedException {
        File binary = getZipAlignBinary();
        List<String> args = new ArrayList<>();
        args.add(binary.getAbsolutePath());
        args.add("-f");
        args.add("-v");
        args.add("4");
        args.add(mApkFile.getAbsolutePath());
        args.add(mApkFile.getParent() + "/aligned.apk");
        BinaryExecutor executor = new BinaryExecutor();
        executor.setCommands(args);
        if (!executor.execute().isEmpty()) {
            throw new CompilationFailedException(executor.getLog());
        }
    }

    private File getZipAlignBinary() throws IOException {
        Context context = ApplicationProvider.getApplicationContext();
        String path = context.getApplicationInfo().nativeLibraryDir + "/libzipalign.so";
        File file = new File(path);
        if (!file.exists()) {
            throw new IOException("ZipAlign binary not found.");
        }
        return file;
    }
}