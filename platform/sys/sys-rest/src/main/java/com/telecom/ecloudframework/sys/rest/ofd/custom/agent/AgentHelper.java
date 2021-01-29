package com.telecom.ecloudframework.sys.rest.ofd.custom.agent;

import com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.model.Common;
import com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.Const;
import com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.PackEntry;
import com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.Packet;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

public class AgentHelper {

    private static Logger log = LoggerFactory.getLogger(AgentHelper.class);

    public static int maxWaitCount = 10;

    private static AtomicReference<Semaphore> ars = new AtomicReference<Semaphore>();

    private static Semaphore get() {
        Semaphore semaphore = ars.get();
        if (semaphore == null) {
            if (maxWaitCount > 0) {
                for (; ; ) {
                    if (ars.compareAndSet(null, new Semaphore(maxWaitCount))) {
                        break;
                    }
                }
                semaphore = ars.get();
            }
        }
        return semaphore;
    }

    public static void submit(ConvertAgent agent, boolean block, File source, File out) throws Exception {
        Semaphore semaphore = get();
        if (semaphore != null) {
            semaphore.acquire();
        }

        final long time = System.currentTimeMillis();
        String name = source.getName();
        String fmt = FilenameUtils.getExtension(name);
        log.info("Submit {}, type is {}", name, fmt);
        Packet packet = new Packet(Const.PackType.COMMON, Const.Target.OFD);
        packet.file(new Common(name, fmt, 0, PackEntry.wrap(source)));
        if (block) {
            try {
                agent.convert(packet, new FileOutputStream(out));
            } finally {
                if (semaphore != null) {
                    semaphore.release();
                }
            }
            log.info("{} Finally cost {}", name, System.currentTimeMillis() - time);
        } else {
            FCB fcb = new FCB(name, time, out);
            fcb.semaphore = semaphore;
            agent.convertNoWait(packet, fcb);
        }
    }

    private static void walk(File dir, FileVisitor visitor, final Set<String> ass, int lv) {
        File[] fs;
        if (ass.isEmpty()) {
            fs = dir.listFiles();
        } else {
            fs = dir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    String ext = FilenameUtils.getExtension(name);
                    return ass.contains(ext);
                }
            });
        }
        if (fs != null && fs.length > 0) {
            Arrays.sort(fs, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    int o1d = o1.isDirectory() ? 2 : 1, o2d = o2.isDirectory() ? 2 : 1;
                    int v = o1d - o2d;
                    if (v == 0) {
                        return o1.getName().compareTo(o2.getName());
                    }
                    return v;
                }
            });
            for (File file : fs) {
                if (file.isFile()) {
                    if (!visitor.visit(lv, file)) {
                        break;
                    }
                } else {
                    walk(file, visitor, ass, lv + 1);
                }
            }
        }
    }

    public static void walk(File dir, FileVisitor visitor, String... acceptSuffix) {
        final Set<String> ass = new HashSet<String>();
        if (acceptSuffix != null && acceptSuffix.length > 0) {
            for (String suffix : acceptSuffix) {
                if (suffix != null) {
                    if (suffix.charAt(0) == '.') {
                        suffix = suffix.substring(1);
                    }
                    ass.add(suffix.toLowerCase());
                }
            }
        }
        walk(dir, visitor, ass, 1);
    }

    public interface FileVisitor {
        boolean visit(int level, File file);
    }

    private static class FCB implements ConvertCallback, TransferCallback {

        private long pks, ups, dls, qs, cs = System.currentTimeMillis();

        private Semaphore semaphore;

        private final String name;
        private final long start;
        private final File out;

        FCB(String name, long time, File out) {
            this.name = name;
            this.start = time;
            this.out = out;
        }

        @Override
        public OutputStream openOutput() throws IOException {
            return new FileOutputStream(out);
        }

        @Override
        public void onStart() {
            this.cs = System.currentTimeMillis();
            log.info("{} Start..., in queue wait {}", this.name, this.cs - this.qs);
        }

        @Override
        public void onSuccess() {
            log.info("{} Success cost {}", this.name, (System.currentTimeMillis() - this.cs));
        }

        @Override
        public void onFailed(String code, String message) {
            log.info("{} Failed cost {}", this.name, (System.currentTimeMillis() - this.cs));
        }

        @Override
        public void onException(Exception ex) {
            log.info("Exception", ex);
        }

        @Override
        public void onFinally() {
            log.info("{} Finally cost {}", this.name, (System.currentTimeMillis() - this.start));
            if (this.semaphore != null) {
                this.semaphore.release();
            }
        }

        @Override
        public void onPackStart() {
            this.pks = System.currentTimeMillis();
            log.info("{} Pack...", this.name);
        }

        @Override
        public void onPackEnd() {
            log.info("{} Pack cost {}", this.name, System.currentTimeMillis() - this.pks);
        }

        @Override
        public void onUploadStart() {
            log.info("{} Upload...", this.name);
            this.ups = System.currentTimeMillis();
        }

        @Override
        public void onUploadEnd() {
            log.info("{} Upload cost {}", this.name, (System.currentTimeMillis() - this.ups));
            this.qs = System.currentTimeMillis();
        }

        @Override
        public void onTicket(String ticket) {
            log.info("{} Real ticket = {}", this.name, ticket);
        }

        @Override
        public void onDownloadStart() {
            log.info("{} Download...", this.name);
            this.dls = System.currentTimeMillis();
        }

        @Override
        public void onDownloadEnd() {
            log.info("{} Download cost {}", this.name, (System.currentTimeMillis() - this.dls));
        }
    }

}
